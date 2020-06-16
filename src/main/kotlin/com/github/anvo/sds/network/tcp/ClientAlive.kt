package com.github.anvo.sds.network.tcp

/**
 * From client to server after [ServerHelo]
 *
 * Example:
 * 00000000  01 00 00 00 00 00 00 00  00 00                                        ........ ..
 */
@ExperimentalUnsignedTypes
class ClientAlive(): TcpPackage() {

    companion object {
        const val id = 0x1
        const val payloadLength = 9
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(payloadLength + 1)
        buffer[0] = id.toUByte();
        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ClientAlive()"
    }
}