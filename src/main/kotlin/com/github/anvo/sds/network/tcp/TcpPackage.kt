package com.github.anvo.sds.network.tcp

/**
 * Client
 * * 0x01 - [ClientAlive]
 * * 0x05 - [ClientNewGame]
 * * 0x06 - [ClientPlayerJoin]
 * * 0x0a - [ClientGameBoardInfo]
 * * 0x0c - [ClientGameUnknownA]*
 * * 0x0e - [ClientGameFinishedLoading]
 * * 0x10 - [ClientGameStart]
 * * 0x15 - [ClientGameFinished]
 * * 0x62 - [ClientGameAlive]*
 * * 0x63 - [Quit]
 *
 * Both
 * * 0x1b - [MenuContinue]
 * * 0x1a - [MenuPause]
 *
 * Server
 * * - - [ServerHello]
 * * 0x02 - [ServerAlive]
 * * 0x04 - [ServerPlayerList]
 * * 0x07 - [ServerPlayerJoin]
 * * 0x08 - [ServerGameAck]
 * * 0x0c - [ServerGameUnknownB]*
 * * 0x0d - [ServerGameStart]
 * * 0x0f - [ServerGameRunning]
 * * 0x15 - [ServerPlayerFinished]
 * * 0x17 - [ServerGameFinished]
 * * 0x19 - [ServerPlayerLeft]
 * * 0x62 - [ServerGameUnknownA]*
  */
abstract class TcpPackage {
    abstract fun toByteArray():ByteArray
}