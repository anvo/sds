package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.ubytearray.setUShort

/**
 * From server to client after connection start. First message.
 *
 * Example:
 * 58 ce                                              X.
 * 69 12 00 00                                        i...
 */
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class ServerHello(val udpGamePort: UShort, val connectionId: UShort): TcpPackage(){

    companion object {
        const val payloadLength = 6 // Byte
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(payloadLength)
        buffer.setUShort(0, udpGamePort)
        buffer.setUShort(2, connectionId)
        //4-5: 0
        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ServerHello(udpGamePort=$udpGamePort, connectionId=$connectionId)"
    }
}