package com.github.anvo.sds.network.udp

import java.net.DatagramPacket

/**
 * From client to server. After [server.tcp.ServerHello]
 *
 * Example:
 * *  78                                                 x
 */
class ClientHello {
    companion object {
        const val id = 0x78u
        const val length = 1

        fun fromDatagramPacket(packet: DatagramPacket): ClientHello {
            return ClientHello()
        }
    }

    override fun toString(): String {
        return "ClientHello()"
    }
}