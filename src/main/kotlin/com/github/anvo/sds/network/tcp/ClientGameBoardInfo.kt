package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.bytearray.getUShort

/**
 * From client to server after [ServerPlayerJoin]
 *
 * Example:
 * 0a 00 00 00                                        ....
 * 69 12 20 00 00 00 42 6f  61 72 64 5f 31 33 00 00   i. ...Bo ard_13..
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 7d
 *
 */
@ExperimentalStdlibApi
class ClientGameBoardInfo(val connectionId: UShort): TcpPackage() {

    companion object {
        const val id = 0x0a
        const val payloadLength = 41

        fun fromByteArray(payload: ByteArray): ClientGameBoardInfo {
            val connectionId = payload.getUShort(3)
            return ClientGameBoardInfo(connectionId)
        }
    }

    override fun toByteArray(): ByteArray {
        TODO("not implemented")
    }

    override fun toString(): String {
        return "ClientGameBoardInfo(connectionId=$connectionId)"
    }
}