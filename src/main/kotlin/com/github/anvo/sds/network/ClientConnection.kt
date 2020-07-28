package com.github.anvo.sds.network

import com.github.anvo.sds.Log
import com.github.anvo.sds.logic.GameUseCase
import java.net.DatagramSocket
import java.net.Socket
import kotlin.concurrent.thread

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ClientConnection(private val connectionId: UShort,
                       private val gameUseCase: GameUseCase) {

    companion object {
        const val BASE_PORT = 16221 // Port range: 16221 - (16221+connectionCount)
        const val connectionCount = 64 // Maximum amount of parallel connections
        var counter = 0
    }

    fun handleConnection(clientSocket: Socket) {
        counter = (counter + 1) % connectionCount
        val udpSocket = DatagramSocket(BASE_PORT + counter)

        val tcpConnection = TcpClientConnection(this.connectionId, clientSocket, this.gameUseCase)
        val udpConnection = UdpClientConnection(this.connectionId, udpSocket, this.gameUseCase)

        thread(true, true) {
            udpConnection.run()
        }
        tcpConnection.run(udpSocket.localPort.toUShort())

        udpConnection.close()
        Log.server("Client|$connectionId") {"Connection closed"}
    }
}