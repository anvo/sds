package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.bytearray.getUShort

/**
 * From client to server after [ClientGameBoardInfo]
 *
 * Example:
 * 62 00 00 00 69 12 01 00  00 00 7a                  b...i... ..z
 *
 */
@ExperimentalStdlibApi
class ClientGameAlive(val connectionId: UShort) : TcpPackage() {

    companion object {
        const val id = 0x62
        const val payloadLength = 10

        fun fromByteArray(payload: ByteArray): ClientGameAlive {
            val connectionId = payload.getUShort(3)
            return ClientGameAlive(connectionId)
        }
    }

    override fun toByteArray(): ByteArray {
        TODO("not implemented")
    }

    override fun toString(): String {
        return "ClientGameAlive(connectionId=$connectionId)"
    }
}