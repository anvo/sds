package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.ubytearray.setString
import com.github.anvo.sds.extensions.ubytearray.setUShort
import com.github.anvo.sds.model.Game

/**
 * From server to client after [ClientAlive]
 *
 * Examples:
 *
 * No game available:
 * 02 00 00 00                                        ....
 * 00 00 01 00 00 00 00                               .......
 *
 * Single game available:
 * 02 00 00 00                                        ....
 * 00 00 42 00 00 00 01 00  70 61 75 6c 27 73 20 47   ..B..... paul's G
 * 61 6d 65 00 00 00 00 00  00 00 00 00 00 00 00 00   ame..... ........
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 00 00
 *
 * Please note: The game only supports joining the first game
 */
class ServerAlive(val games: List<Game>): TcpPackage() {

    companion object {
        const val id = 0x2
    }

    @ExperimentalStdlibApi
    @ExperimentalUnsignedTypes
    override fun toByteArray(): ByteArray {

        val buffer = UByteArray( 10 + 2 + games.size*32)
        buffer[0] = id.toUByte();

        buffer.setUShort(6,  ( 2 + games.size*32).toUShort())
        buffer.setUShort(10, games.size.toUShort())

        for (it in games.withIndex()) {
            buffer.setString(12 + (it.index * 32), it.value.name, Charsets.US_ASCII)
        }
        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ServerAlive(games=$games)"
    }
}