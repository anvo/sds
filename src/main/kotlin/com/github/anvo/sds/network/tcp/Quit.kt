package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.bytearray.getUShort
import com.github.anvo.sds.extensions.ubytearray.setUShort

/**
 * From client to server or
 * From server to client
 *
 * Example:
 *  Client -> Server
 *  63 00 00 00 69 12 00 00  00 00                     c...i... ..
 *
 *  Server -> client
 *  63 00 00 00 00 00 00 00  00 00                     c....... ..
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class Quit(val connectionId: UShort): TcpPackage() {

    constructor () : this(0u) {
    }

    companion object {
        const val id = 0x63
        const val payloadLength = 9

        fun fromByteArray(payload: ByteArray): Quit {
            val connectionId = payload.getUShort(3)
            return Quit(connectionId)
        }
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(payloadLength + 1)
        buffer[0] = id.toUByte();
        buffer.setUShort(4, connectionId)
        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ClientQuit(connectionId=$connectionId)"
    }
}