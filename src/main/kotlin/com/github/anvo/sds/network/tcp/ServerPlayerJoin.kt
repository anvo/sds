package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.ubytearray.setString
import com.github.anvo.sds.extensions.ubytearray.setUShort
import com.github.anvo.sds.model.Player

/**
 * From server to client after [ClientPlayerJoin]
 *
 * Examples:
 * 07 00 00 00                                        ....
 * 00 00 62 00 00 00 70 61  75 6c 00 00 00 00 00 00   ..b...pa ul......
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 7c 4b 61  72 6c 00 00 00 00 00 00   .....|Ka rl......
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 42 6f  61 72 64 5f 31 33 00 00   ......Bo ard_13..
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 01 00                            ........
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class ServerPlayerJoin(val player: Player, val position: Int): TcpPackage() {

    companion object {
        const val id = 0x7
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(108)
        buffer[0] = id.toUByte()
        buffer[6] = 0x62u //98

        buffer.setString(10, player.name, Charsets.US_ASCII)
        buffer.setString(10 + 32, player.character, Charsets.US_ASCII)
        buffer.setString(10 + 64, player.board, Charsets.US_ASCII)
        buffer.setUShort(10 + 96, position.toUShort())

        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ServerPlayerInfo(player=$player, position=$position)"
    }
}