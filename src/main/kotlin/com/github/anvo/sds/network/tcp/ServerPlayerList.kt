package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.ubytearray.setString
import com.github.anvo.sds.extensions.ubytearray.setUShort
import com.github.anvo.sds.model.Player

/**
 * From server to client after [ServerPlayerJoin]
 *
 * Example:
 * Two players:
 *                          04 00 00 00 00 00 cd 00            ........
 * 00 00 02 55 6c 72 69 6b  61 00 00 00 00 00 00 00   ...Ulrik a.......
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 55 6c 72 69 6b  61 00 00 00 00 00 00 00   ...Ulrik a.......
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 42 6f 61 72 64  5f 31 33 00 00 00 00 00   ...Board _13.....
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 01 00 00 00 00  00 70 61 75 6c 00 00 00   ........ .paul...
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 00 00  00 4b 61 72 6c 00 00 00   ........ .Karl...
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 00 00  00 42 6f 61 72 64 5f 31   ........ .Board_1
 * 33 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   3....... ........
 * 00 00 00 00 00 00 00 00  00 02 01 00 00 00 00      ........ .......
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ServerPlayerList(val players: List<Player>) : TcpPackage() {

    companion object {
        const val id = 0x4
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(10 + (players.size * 102))
        buffer[0] = id.toUByte();

        buffer.setUShort(6, (players.size * 102).toUShort()) // Size

        buffer[10] = players.size.toUByte()

        players.forEachIndexed { index, player ->
            val base = 11 + (index * 102)
            buffer.setString(base, player.name, Charsets.US_ASCII)
            buffer.setString(base + 32, player.character, Charsets.US_ASCII)
            buffer.setString(base + 64, player.board, Charsets.US_ASCII)
            buffer.setUShort(base + 96, (index +1).toUShort())
        }
        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ServerPlayerList(players=$players)"
    }
}