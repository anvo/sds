package com.github.anvo.sds.network

import com.github.anvo.sds.Log
import com.github.anvo.sds.logic.GameUseCase
import com.github.anvo.sds.model.Game
import com.github.anvo.sds.model.Player
import com.github.anvo.sds.network.udp.ClientHello
import com.github.anvo.sds.network.udp.PlayerGameState
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class UdpClientConnection(
    private val connectionId: UShort,
    private val clientSocket: DatagramSocket,
    private val gameUseCase: GameUseCase
) {

    private lateinit var clientAddress: InetAddress
    private var clientPort: Int = -1

    private val logTag = "Client|UDP|$connectionId"

    private val gameStateListener = object: GameUseCase.GameStateListener {
        override fun gameStateAvailable(game: Game, player: Player, data: ByteArray) {
            val identifier: UShort = (game.players.indexOf(player) + 1).toUShort()
            val packet = PlayerGameState(identifier, data)

            Log.packet(logTag) { "Sending $packet" }
            Log.traffic(logTag) { packet.toByteArray() }

            val pkg = DatagramPacket(packet.toByteArray(), PlayerGameState.length, clientAddress, clientPort)
            clientSocket.send(pkg)
        }

    }


    fun run() {
        Log.server(logTag) { "Listening on port ${clientSocket.localPort}" }

        val buffer = DatagramPacket(ByteArray(ClientHello.length), ClientHello.length)
        clientSocket.receive(buffer)
        val clientHello = ClientHello.fromDatagramPacket(buffer)

        Log.packet(logTag) { "Received $clientHello" }
        Log.traffic(logTag) { buffer.data }

        this.clientAddress = buffer.address
        this.clientPort = buffer.port

        this.gameUseCase.addGameStateListener(this.connectionId, this.gameStateListener)

        while(!clientSocket.isClosed) {

            try {
                val buffer = DatagramPacket(ByteArray(PlayerGameState.length), PlayerGameState.length)
                clientSocket.receive(buffer)
                val playerInfo = PlayerGameState.fromDatagramPacket(buffer)
                Log.packet(logTag) { "Received $playerInfo" }
                Log.traffic(logTag) { buffer.data }

                this.gameUseCase.deliverGameState(playerInfo.identifier, playerInfo.data)

            } catch (e: SocketException) {
                //Ignore
                Log.server(logTag) { e.localizedMessage }
            }
        }

        this.gameUseCase.removeGameStateListener(this.connectionId, this.gameStateListener)

    }

    fun close() {
        clientSocket.close()
    }
}