package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.bytearray.getString
import com.github.anvo.sds.extensions.bytearray.getUShort

/**
 * Example:
 * 05 00 00 00                                        ....
 * 69 12 40 00 00 00 70 61  75 6c 27 73 20 47 61 6d   i.@...pa ul's Gam
 * 65 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   e....... ........
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00
 */
@ExperimentalStdlibApi
class ClientNewGame(val name: String, val connectionId: UShort): TcpPackage() {

    companion object {
        const val id = 0x5
        const val payloadLength = 73

        fun fromByteArray(payload:ByteArray): ClientNewGame {
            val name = payload.getString(9, 64)
            val connectionId = payload.getUShort(3)
            return ClientNewGame( name, connectionId);
        }
    }

    override fun toByteArray(): ByteArray {
        TODO("not implemented")
    }

    override fun toString(): String {
        return "ClientNewGame(name='$name', connectionId=$connectionId)"
    }
}