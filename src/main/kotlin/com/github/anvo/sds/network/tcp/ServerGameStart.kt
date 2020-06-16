package com.github.anvo.sds.network.tcp

/**
 * From server to client after [ClientGameStart]
 *
 * Example:
 * 0d 00 00 00                                        ....
 * 00 00 41 00 00 00 00 41  6c 70 69 6e 65 20 54 72   ..A....A lpine Tr
 * 61 63 6b 73 20 45 61 73  79 20 4e 69 67 68 74 00   acks Eas y Night.
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 00
 *
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class ServerGameStart(val track: String) : TcpPackage() {

    companion object {
        const val id = 0x0d
        const val payloadLength = 74
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(payloadLength + 1)
        buffer[0] = id.toUByte();
        buffer[6] = 0x41u; //TODO: Meaning?

        track.toByteArray().forEachIndexed { index, byte ->
            buffer[11 + index] = byte.toUByte()
        }

        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ServerGameStart(track='$track')"
    }
}