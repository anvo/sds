package com.github.anvo.sds

import com.github.anvo.sds.network.ClientConnection
import com.github.anvo.sds.logic.GameUseCase
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.nio.charset.Charset
import kotlin.concurrent.thread

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class DedicatedServer {

    private val announcementSender = DatagramSocket();
    private val TCP_PORT = 16211


    fun run(lanMode: Boolean = false, logTraffic: Boolean = false) {

        if(logTraffic) {
            Log.levels.addAll(setOf(Log.Level.TRAFFIC, Log.Level.PACKET))
        }

        thread(isDaemon = true) {
            waitForIncomingTcp()
        }

        if(lanMode) {
            while(true) {
                sendLanAnnouncement()
                Thread.sleep(3000)
            }
        } else {
            waitForIncomingUdp();
        }

    }


    private fun waitForIncomingTcp() {

        val gameUseCase = GameUseCase();

        //Open up TCP socket
        val tcpSocket = ServerSocket(TCP_PORT, 10)
        Log.server("TCP") { "Listening on ${tcpSocket.inetAddress}:${tcpSocket.localPort}" }

        var connectionId:UShort = 0x1268u

        while (true) {
            val clientSocket = tcpSocket.accept()
            thread(isDaemon = true){
                ClientConnection(connectionId++, gameUseCase).handleConnection(clientSocket)
            }
        }
    }

    private fun waitForIncomingUdp() {
        val serverUdp = DatagramSocket(16200)

        Log.server("UDP") {"Listening on ${serverUdp.localAddress}:${serverUdp.localPort}"}

        while(true) {
            val buffer = DatagramPacket(ByteArray(1), 1)
            serverUdp.receive(buffer)
            Log.server("UDP") {"New request from ${buffer.address}"}
            thread(isDaemon = true) {
                repeat(10) {
                    //Prepare packet
                    val msg = "Yes! Me! My name is: \"supreme-server\" $TCP_PORT "
                    val data = msg.toByteArray(Charsets.US_ASCII)

                    val packet = DatagramPacket(data, data.size)
                    packet.port = buffer.port
                    packet.address = buffer.address

                    Log.server("UDP") {"Announcing server to ${packet.address}:${packet.port}"}
                    serverUdp.send(packet)
                    Thread.sleep(2000)
                }
            }
        }

    }

    private fun sendLanAnnouncement() {
        //Prepare packet
        val port = 16220
        val msg = "Yes! Me! My name is: \"supreme-server\" $TCP_PORT "
        val data = msg.toByteArray(Charset.forName("ASCII"))

        val packet = DatagramPacket(data, data.size)
        packet.port = port
        packet.address = InetAddress.getByName("255.255.255.255")

        Log.server("UDP") {"Announcing LAN server to ${packet.address}:${packet.port}"}
        announcementSender.send(packet)
    }

}