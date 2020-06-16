package com.github.anvo.sds.network.udp

import com.github.anvo.sds.extensions.bytearray.getUShort
import com.github.anvo.sds.extensions.ubytearray.setUByteArray
import com.github.anvo.sds.extensions.ubytearray.setUShort
import java.net.DatagramPacket

/**
 * Player info from client to server and from server to client
 *
 * Example:
 *
 * From client to server:
 * 12 00 00 00 69 12 70 00  26 00 00 00 69 00 00 00   ....i.p. &...i...
 * a6 ab 20 44 00 33 52 c4  e3 a5 c8 42 78 fd e9 ff   .. D.3R. ...Bx...
 * f9 ff 96 07 80 80 81 00  7c 7c 7f 7f 7f 7f 00 01   ........ ||......
 * [... 1036 bytes]
 *
 * From server to client (player 02):
 * 12 00 00 00 02 00 70 00  26 00 00 00 07 00 00 00   ......p. &.......
 * a6 ab 20 44 00 33 52 c4  e3 a5 c8 42 78 fd e9 ff   .. D.3R. ...Bx...
 * f9 ff 96 07 80 80 85 00  7c 7c 7f 7f 7f 7f 00 01   ........ ||......
 * [... 1036 bytes]
 */

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class PlayerGameState(val identifier: UShort, val data: ByteArray) {
    companion object {
        const val id = 0x12u
        const val length = 1036

        fun fromDatagramPacket(packet: DatagramPacket): PlayerGameState {
            val buffer = packet.data
            return PlayerGameState(buffer.getUShort(4), buffer.sliceArray(6 until length-1))
        }
    }

    fun toByteArray(): ByteArray {
        val buffer = UByteArray(length)
        buffer[0] = PlayerGameState.id.toUByte()
        buffer.setUShort(4, identifier)
        buffer.setUByteArray(6, data.asUByteArray())
        return buffer.asByteArray()
    }


    override fun toString(): String {
        return "PlayerInfo(identifier=$identifier, data=[...])"
    }
}