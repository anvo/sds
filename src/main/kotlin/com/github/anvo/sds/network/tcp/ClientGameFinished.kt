package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.bytearray.getUShort

/**
 * From client to server after [ServerGameStart]
 *
 * Example:
 *  15 00 00 00                                        ....
 *  68 12 0c 00 00 00 f0 4a  0b 43 5a 3d 11 42 c5 0a   h......J .CZ=.B..
 *  b6 42
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ClientGameFinished(val connectionId: UShort, val time: UByteArray): TcpPackage() {

    companion object {
        const val id = 0x15
        const val payloadLength = 21

        fun fromByteArray(payload: ByteArray): ClientGameFinished {
            val connectionId = payload.getUShort(3)
            val time = payload.sliceArray(9 .. 20).asUByteArray()
            return ClientGameFinished(connectionId, time)
        }
    }

    override fun toByteArray(): ByteArray {
        TODO("not implemented")
    }

    override fun toString(): String {
        return "ClientGameFinished(connectionId=$connectionId, time=$time)"
    }
}