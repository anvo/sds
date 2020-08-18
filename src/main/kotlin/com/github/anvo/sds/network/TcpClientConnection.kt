package com.github.anvo.sds.network

import com.github.anvo.sds.Log
import com.github.anvo.sds.logic.GameUseCase
import com.github.anvo.sds.model.FinishTime
import com.github.anvo.sds.model.Game
import com.github.anvo.sds.model.Player
import com.github.anvo.sds.network.tcp.*
import java.io.IOException
import java.io.InputStream
import java.net.Socket
import java.net.SocketException

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class TcpClientConnection(private val connectionId: UShort,
                          private val clientSocket: Socket,
                          private val gameUseCase: GameUseCase) {

    private val socketIn = clientSocket.getInputStream()
    private val socketOut = clientSocket.getOutputStream()

    private var currentPlayer: Player? = null
    private var currentGame: Game? = null

    private val logTag = "Client|TCP|$connectionId"

    private val listener = object: GameUseCase.GameListener {

        override fun playerJoined(game: Game, player: Player) {
            val info = ServerPlayerJoin(player, game.players.indexOf(player) +1)
            Log.packet(logTag) { "Sending $info" }
            Log.traffic(logTag) { info.toByteArray() }
            try {
                socketOut.write(info.toByteArray())
            } catch (e: Exception) {
                Log.server(logTag) { e.toString()}
            }
        }

        override fun gameStarted(game: Game, track: String) {
            val packet = ServerGameStart(track)
            Log.packet(logTag) { "Sending $packet" }
            Log.traffic(logTag) { packet.toByteArray() }
            try {
                socketOut.write(packet.toByteArray())
            } catch (e: Exception) {
                Log.server(logTag) { e.toString()}
            }
        }

        override fun gameFinishedLoading(game: Game) {
            val packet = ServerGameRunning()
            Log.packet(logTag) { "Sending $packet" }
            Log.traffic(logTag) { packet.toByteArray() }
            try {
                socketOut.write(packet.toByteArray())
            } catch (e: Exception) {
                Log.server(logTag) { e.toString()}
            }
        }

        override fun playerFinished(game: Game, player: Player, time: FinishTime) {
            val position = game.players.indexOf(player) + 1;
            val packet = ServerPlayerFinished(position, time)
            Log.packet(logTag) { "Sending $packet" }
            Log.traffic(logTag) { packet.toByteArray() }
            try {
                socketOut.write(packet.toByteArray())
            } catch (e: Exception) {
                Log.server(logTag) { e.toString()}
            }
        }

        override fun gameFinished(game: Game) {
            val packet = ServerGameFinished(connectionId, game.stats)
            Log.packet(logTag) { "Sending $packet" }
            Log.traffic(logTag) { packet.toByteArray() }
            try {
                socketOut.write(packet.toByteArray())
            } catch (e: Exception) {
                Log.server(logTag) { e.toString()}
            }

            val packet2 = ServerGameUnknownA(connectionId)
            Log.packet(logTag) { "Sending $packet2" }
            Log.traffic(logTag) { packet2.toByteArray() }
            try {
                socketOut.write(packet2.toByteArray())
            } catch (e: Exception) {
                Log.server(logTag) { e.toString()}
            }
        }

        override fun menuPause() {
            val packet = MenuPause()
            Log.packet(logTag) { "Sending $packet" }
            Log.traffic(logTag) { packet.toByteArray() }
            try {
                socketOut.write(packet.toByteArray())
            } catch (e: Exception) {
                Log.server(logTag) { e.toString()}
            }
        }

        override fun menuContinue() {
            val packet = MenuContinue()
            Log.packet(logTag) { "Sending $packet" }
            Log.traffic(logTag) { packet.toByteArray() }
            try {
                socketOut.write(packet.toByteArray())
            } catch (e: Exception) {
                Log.server(logTag) { e.toString()}
            }
        }

        override fun gameQuit(game: Game) {
            val packet = Quit()
            Log.packet(logTag) { "Sending $packet" }
            Log.traffic(logTag) { packet.toByteArray() }
            try {
                socketOut.write(packet.toByteArray())
            } catch (e: Exception) {
                Log.server(logTag) { e.toString()}
            }

            currentGame = null
            currentPlayer = null
        }

        override fun playerLeft(game: Game, player: Player, position: Int) {
            val packet = ServerPlayerLeft(position)
            Log.packet(logTag) { "Sending $packet" }
            Log.traffic(logTag) { packet.toByteArray() }
            try {
                socketOut.write(packet.toByteArray())
            } catch (e: Exception) {
                Log.server(logTag) { e.toString()}
            }
        }

    }

    fun run(udpPort:UShort) {

        Log.server(logTag) { "New incoming request from ${clientSocket.inetAddress}:${clientSocket.port}" }

        //Sending hello
        val hello = ServerHello(udpPort, connectionId )
        Log.packet(logTag) { "Sending $hello" }
        Log.traffic(logTag) { hello.toByteArray() }
        socketOut.write(hello.toByteArray())

        try {
            handlePackets()
        }catch (e: Exception) {
            Log.server(logTag) {e.toString()}

            //Remove player, in case we have one
            this.currentGame?.let {
                this.currentPlayer?.let { it1 ->
                    this.gameUseCase.leaveGame(it, it1)
                }
            }

            clientSocket.close()
        }
    }

    @Throws(IOException::class)
    private fun handlePackets() {
        while (clientSocket.isConnected && !clientSocket.isClosed) {

            when (val id = socketIn.read()) {
                ClientAlive.id -> {
                    val buffer = this.readBytes(socketIn, ClientAlive.payloadLength)
                    val packet = ClientAlive()
                    Log.packet(logTag) { "Received $packet" }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()).plus(buffer) }

                    val alive = ServerAlive(this.gameUseCase.games)
                    Log.packet(logTag) { "Sending $alive" }
                    Log.traffic(logTag) { alive.toByteArray() }
                    socketOut.write(alive.toByteArray())
                }
                Quit.id -> {
                    val buffer = this.readBytes(socketIn, Quit.payloadLength)
                    val packet = Quit.fromByteArray(buffer)
                    Log.packet(logTag) { "Received $packet" }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()).plus(buffer) }

                    this.currentGame?.let {
                        this.currentPlayer?.let { it1 ->
                            this.gameUseCase.leaveGame(it, it1)
                        }
                    }

                    Log.server(logTag) { "Closing connection" }
                    clientSocket.close()
                }
                ClientNewGame.id -> {
                    val buffer = this.readBytes(socketIn, ClientNewGame.payloadLength)
                    val packet = ClientNewGame.fromByteArray(buffer)
                    Log.packet(logTag) { "Received $packet" }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()).plus(buffer) }


                    val ack = ServerGameAck()
                    Log.packet(logTag) { "Sending $ack" }
                    Log.traffic(logTag) { ack.toByteArray() }
                    socketOut.write(ack.toByteArray())


                    val game = Game(packet.name)
                    currentGame = game
                    this.gameUseCase.newGame(game)
                }
                ClientPlayerJoin.id -> {
                    val buffer = this.readBytes(socketIn, ClientPlayerJoin.payloadLength)
                    val packet = ClientPlayerJoin.fromByteArray(buffer)
                    Log.packet(logTag) { "Received $packet" }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()).plus(buffer) }


                    val ack = ServerGameAck()
                    Log.packet(logTag) { "Sending $ack" }
                    Log.traffic(logTag) { ack.toByteArray() }
                    socketOut.write(ack.toByteArray())

                    val player = Player(packet.player, packet.character, packet.board)
                    this.currentPlayer = player
                    val game = this.gameUseCase.games.first { g -> g.name == packet.game }
                    this.currentGame = game
                    this.gameUseCase.joinGame(game, player, connectionId, this.listener)

                    val list = ServerPlayerList(game.players)
                    Log.packet(logTag) { "Sending $list" }
                    Log.traffic(logTag) { list.toByteArray() }
                    socketOut.write(list.toByteArray())

                    val unkn = ServerGameUnknownB()
                    Log.packet(logTag) { "Sending $unkn" }
                    Log.traffic(logTag) { unkn.toByteArray() }
                    socketOut.write(unkn.toByteArray())
                }
                ClientGameBoardInfo.id -> {
                    val buffer = this.readBytes(socketIn, ClientGameBoardInfo.payloadLength)
                    val packet = ClientGameBoardInfo.fromByteArray(buffer)
                    Log.packet(logTag) { "Received $packet" }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()).plus(buffer) }
                }
                ClientGameAlive.id -> {
                    val buffer = this.readBytes(socketIn, ClientGameAlive.payloadLength)
                    val packet = ClientGameAlive.fromByteArray(buffer)
                    Log.packet(logTag) { "Received $packet" }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()).plus(buffer) }
                }
                ClientGameUnknownA.id -> {
                    val buffer = this.readBytes(socketIn, ClientGameUnknownA.payloadLength)
                    val packet = ClientGameUnknownA.fromByteArray(buffer)
                    Log.packet(logTag) { "Received $packet" }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()).plus(buffer) }
                }
                ClientGameStart.id -> {
                    val buffer = this.readBytes(socketIn, ClientGameStart.payloadLength)
                    val packet = ClientGameStart.fromByteArray(buffer)
                    Log.packet(logTag) { "Received $packet" }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()).plus(buffer) }


                    this.currentGame?.let {
                        this.gameUseCase.startGame(it, packet.track)
                    }
                }
                ClientGameFinishedLoading.id -> {
                    val buffer = this.readBytes(socketIn, ClientGameFinishedLoading.payloadLength)
                    val packet = ClientGameFinishedLoading.fromByteArray(buffer)
                    Log.packet(logTag) { "Received $packet" }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()).plus(buffer) }

                    this.gameUseCase.gameLoaded(this.currentGame!!, this.currentPlayer!!)
                }
                ClientGameFinished.id -> {
                    val buffer = this.readBytes(socketIn, ClientGameFinished.payloadLength)
                    val packet = ClientGameFinished.fromByteArray(buffer)
                    Log.packet(logTag) { "Received $packet" }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()).plus(buffer) }

                    this.gameUseCase.playerFinished(this.currentGame!!, this.currentPlayer!!, packet.time)
                }
                MenuPause.id -> {
                    val buffer = this.readBytes(socketIn, MenuPause.payloadLength)
                    val packet = MenuPause.fromByteArray(buffer)
                    Log.packet(logTag) { "Received $packet" }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()).plus(buffer) }

                    this.gameUseCase.menuPause(this.currentGame!!)
                }
                MenuContinue.id -> {
                    val buffer = this.readBytes(socketIn, MenuContinue.payloadLength)
                    val packet = MenuContinue.fromByteArray(buffer)
                    Log.packet(logTag) { "Received $packet" }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()).plus(buffer) }

                    this.gameUseCase.menuContinue(this.currentGame!!)
                }
                -1 -> {
                    throw IOException("Connection dropped")
                }
                else -> {
                    Log.packet(logTag) { "Received unknown id: " + String.format("%x", id) }
                    Log.traffic(logTag) { byteArrayOf(id.toByte()) }
                }
            }


        }
    }

    @Throws(IOException::class, SocketException::class)
    private fun readBytes(socketReader: InputStream, len: Int): ByteArray {
        val result = UByteArray(len)
        var nextIndex = 0
        while(nextIndex != len) {
            val byte = socketReader.read()
            if(byte == -1) {
                throw IOException("Connection closed")
            }
            result[nextIndex] = byte.toUByte()
            nextIndex++
        }
        return result.toByteArray()
    }
}