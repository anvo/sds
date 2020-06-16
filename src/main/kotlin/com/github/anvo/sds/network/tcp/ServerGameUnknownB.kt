package com.github.anvo.sds.network.tcp

/**
 * From server to client after [ServerPlayerList]
 *
 * Same id as [ClientGameUnknownA]
 *
 * Example:
 * 0c 00 00 00 00 00 00 00  00 00 ........ ..
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ServerGameUnknownB() : TcpPackage() {

    companion object {
        const val id = 0xc
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(10)
        buffer[0] = id.toUByte();


        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ServerGameUnknownB()"
    }
}