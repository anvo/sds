import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

class ClientAnnounce {

    public static void main(String[] args) throws SocketException, InterruptedException, IOException  {
        new ClientAnnounce().run();
    }

    public void run() throws SocketException, InterruptedException, IOException {

        System.out.print("Enter server hostname: ");
        String hostname = System.console().readLine();

        InetAddress address = InetAddress.getByName(hostname);

        while(true) {
            DatagramSocket udpSocket = new DatagramSocket(null);
            udpSocket.setReuseAddress(true);
            udpSocket.bind(new InetSocketAddress(16220));

            DatagramPacket packet = new DatagramPacket(new byte[1], 1);
            packet.setPort(16200);
            packet.setAddress(address);

            System.out.println("Looking for server at " + packet.getAddress() + ":" + packet.getPort());

            udpSocket.send(packet);
            udpSocket.close();
            Thread.sleep(20000);
        }

    }
}