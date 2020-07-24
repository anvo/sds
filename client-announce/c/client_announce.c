#include <winsock2.h>
#include <windows.h>

#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <sys/time.h>
#include <errno.h>

int main (int argc, char **argv) {

    WSADATA wsa;
    int rc = WSAStartup(MAKEWORD(2,0),&wsa);
    if(rc != 0) {
        printf("Failed to start winsock %d\n", rc);
        exit(EXIT_FAILURE);
    }

    printf("Enter server hostname: ");
    char buffer[256];
    fgets(buffer, 256, stdin);
    strtok(buffer, "\n");

    HOSTENT *he = gethostbyname(buffer);
    if( he == NULL) {
        printf("Invalid hostname: %d\n", WSAGetLastError());
        exit(EXIT_FAILURE);
    }

    while (1)
    {   
        SOCKADDR_IN remoteAddr;
        memset(&remoteAddr,0,sizeof(SOCKADDR_IN)); 
        remoteAddr.sin_family=AF_INET;
        remoteAddr.sin_port=htons(16200); 
        memcpy ( (char *) &remoteAddr.sin_addr.s_addr,
            he->h_addr_list[0], he->h_length);

        printf("Looking for server at %s:%i\n", inet_ntoa (*(struct in_addr *) he->h_addr_list[0]), ntohs(remoteAddr.sin_port)) ;

        SOCKET s = socket(AF_INET,SOCK_DGRAM,0);
        if (s == INVALID_SOCKET) {
            printf("Failed to create socket: %d\n", WSAGetLastError());
            exit(EXIT_FAILURE);
        }
        int enable = TRUE;
        int rc = setsockopt(s, SOL_SOCKET, SO_REUSEADDR, (char *)&enable, sizeof(int));
        if (rc < 0) {
            printf("Failed to set SO_REUSEADDR: %s\n", strerror(errno));
            exit(EXIT_FAILURE);
        }     

        SOCKADDR_IN localAddr;
        memset(&localAddr,0,sizeof(SOCKADDR_IN)); 
        localAddr.sin_family=AF_INET;
        localAddr.sin_port=htons(16220); 
        localAddr.sin_addr.s_addr=ADDR_ANY;

        rc = bind(s,(SOCKADDR*)&localAddr,sizeof(SOCKADDR_IN));
        if (rc == SOCKET_ERROR) {
            printf("Failed to bind socket: %d\n", WSAGetLastError());
            exit(EXIT_FAILURE);
        }        

        char * payload = "0";
        rc=sendto(s,payload,1,0,(SOCKADDR*)&remoteAddr,sizeof(SOCKADDR_IN));
        if (rc == SOCKET_ERROR) {
            printf("Failed to send: %d\n", WSAGetLastError());
            exit(EXIT_FAILURE);
        }             

        closesocket(s);
        sleep(20);
    }

    WSACleanup();
}