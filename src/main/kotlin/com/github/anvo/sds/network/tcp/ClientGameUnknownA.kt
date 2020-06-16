package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.bytearray.getUShort

/**
 * From client to server after [ClientGameBoardInfo]
 * Example:
 * 0c 00 00 00                                        ....
 * a8 0e                                              ..
 * 01 00 00 00                                        ....
 * 00
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class ClientGameUnknownA (val connectionId: UShort) : TcpPackage()  {

    companion object {
        const val id = 0x0c
        const val payloadLength = 10


        fun fromByteArray(payload: ByteArray): ClientGameUnknownA {
            val connectionId = payload.getUShort(3)
            return ClientGameUnknownA(connectionId)
        }
    }

    override fun toByteArray(): ByteArray {
        TODO("not implemented")
    }

    override fun toString(): String {
        return "ClientGameUnknownA(connectionId=$connectionId)"
    }
}