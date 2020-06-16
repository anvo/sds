package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.bytearray.getString
import com.github.anvo.sds.extensions.bytearray.getUShort

/**
 * From client to server
 *
 * Example:
 * 10 00 00 00                                        ....
 * a8 66 40 00 00 00 41 6c  70 69 6e 65 20 54 72 61   .f@...Al pine Tra
 * 63 6b 73 20 45 61 73 79  20 4e 69 67 68 74 00 00   cks Easy  Night..
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 7d                                  .....}
 */
@ExperimentalStdlibApi
class ClientGameStart(val connectionId: UShort, val track: String) : TcpPackage() {

    companion object {
        const val id = 0x10
        const val payloadLength = 73

        fun fromByteArray(payload: ByteArray): ClientGameStart {
            val connectionId = payload.getUShort(3)
            val track = payload.getString(9, 64)
            return ClientGameStart(connectionId, track)
        }
    }

    override fun toByteArray(): ByteArray {
        TODO("not implemented")
    }

    override fun toString(): String {
        return "ClientGameStart(connectionId=$connectionId, track='$track')"
    }
}