package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.ubytearray.setUShort

/**
 * From server to client after [ServerGameFinished]
 *
 * Example:
 * 62 00 00 00                                        b...
 * 6a 12 01 00 00 00 00                               j......
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ServerGameUnknownA(val connectionId: UShort) : TcpPackage() {

    companion object {
        const val id = 0x62
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(11)
        buffer[0] = id.toUByte();


        buffer.setUShort(4, connectionId)

        buffer[6] = 0x1u

        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ServerGameUnknownA(connectionId=$connectionId)"
    }
}