package com.github.anvo.sds.network.tcp

/**
 * From client to server.
 * From server to client after [MenuPause]
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class MenuContinue: TcpPackage() {

    companion object {
        const val id = 0x1b
        const val payloadLength = 9

        fun fromByteArray(payload: ByteArray): MenuContinue {
            return MenuContinue()
        }
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(payloadLength +1)
        buffer[0] = id.toUByte();
        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "MenuContinue()"
    }
}