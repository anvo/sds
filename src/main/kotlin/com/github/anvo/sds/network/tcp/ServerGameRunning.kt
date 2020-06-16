package com.github.anvo.sds.network.tcp

/**
 * From server to client after [ClientGameFinishedLoading]
 *
 * Example:
 *  0f 00 00 00                                        ....
 *  00 00 00 00 00 00                                  ......
 *
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class ServerGameRunning() : TcpPackage() {

    companion object {
        const val id = 0x0f
        const val payloadLength = 9
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(payloadLength + 1)
        buffer[0] = id.toUByte();
        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ServerGameRunning()"
    }
}