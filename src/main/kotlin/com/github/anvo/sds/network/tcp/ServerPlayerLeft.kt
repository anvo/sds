package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.ubytearray.setUShort

/**
 * From server to client after [Quit]
 *
 * Examples:
 * 19 00 00 00                                        ....
 * 02 00                                              ..
 * 00 00 00 00                                        ....
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class ServerPlayerLeft(val position: Int): TcpPackage() {

    companion object {
        const val id = 0x19
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(10)
        buffer[0] = id.toUByte()
        buffer.setUShort(4, position.toUShort())
        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ServerPlayerLeft(position=$position)"
    }
}