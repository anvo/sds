package com.github.anvo.sds.network

import com.github.anvo.sds.logic.GameUseCase
import java.net.DatagramSocket
import java.net.Socket
import kotlin.concurrent.thread

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ClientConnection(private val connectionId: UShort,
                       private val gameUseCase: GameUseCase) {

    fun handleConnection(clientSocket: Socket) {
        val udpSocket = DatagramSocket()

        val tcpConnection = TcpClientConnection(this.connectionId, clientSocket, this.gameUseCase)
        val udpConnection = UdpClientConnection(this.connectionId, udpSocket, this.gameUseCase)

        thread(true, true) {
            udpConnection.run()
        }
        tcpConnection.run(udpSocket.localPort.toUShort())

        udpConnection.close()
    }
}