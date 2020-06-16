package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.bytearray.getUShort

/**
 * From client to server after [ServerGameStart]
 *
 * Example:
 * 0e 00 00 00                                        ....
 * 69 12 01 00 00 00 00                               i......
 *
 */
@ExperimentalStdlibApi
class ClientGameFinishedLoading(val connectionId: UShort) : TcpPackage() {

    companion object {
        const val id = 0x0e
        const val payloadLength = 10

        fun fromByteArray(payload: ByteArray): ClientGameFinishedLoading {
            val connectionId = payload.getUShort(3)
            return ClientGameFinishedLoading(connectionId)
        }
    }

    override fun toByteArray(): ByteArray {
        TODO("not implemented")
    }

    override fun toString(): String {
        return "ClientGameFinishedLoading(connectionId=$connectionId)"
    }
}