package com.github.anvo.sds.network.tcp

/**
 * From server to client after [ClientNewGame]
 *
 * Example:
 * 08 00 00 00 00 00 00 00 00 00
 */
@ExperimentalUnsignedTypes
class ServerGameAck(): TcpPackage() {

    companion object {
        const val id = 0x8
        const val payloadLength = 9
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(ServerGameAck.payloadLength + 1)
        buffer[0] = id.toUByte()
        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ServerGameAck()"
    }
}