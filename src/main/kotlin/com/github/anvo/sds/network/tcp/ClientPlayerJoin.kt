package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.bytearray.getString
import com.github.anvo.sds.extensions.bytearray.getUShort

/**
 * From client to server after [ClientNewGame]
 *
 * Example:
 * 06 00 00 00                                        ....
 * 69 12 a1 00 00 00 70 61  75 6c 27 73 20 47 61 6d   i.....pa ul's Gam
 * 65 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   e....... ........
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 70 61  75 6c 00 00 00 00 00 00   ......pa ul......
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 4b 61  72 6c 00 00 00 00 00 00   ......Ka rl......
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 42 6f  61 72 64 5f 31 33 00 00   ......Bo ard_13..
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 00 00 00 00 00 00                               .......
 */
@ExperimentalStdlibApi
class ClientPlayerJoin(val connectionId:UShort,
                       val game:String,
                       val player:String,
                       val character: String,
                       val board:String): TcpPackage() {

    companion object {
        const val id = 0x6
        const val payloadLength = 170

        fun fromByteArray(payload: ByteArray): ClientPlayerJoin{
            val connectionId = payload.getUShort(3)

            val game = payload.getString(9, 64);
            val player = payload.getString(73, 32)
            val character = payload.getString(105, 32)
            val board = payload.getString(137, 32)
            return ClientPlayerJoin(connectionId, game, player, character, board)
        }
    }



    override fun toByteArray(): ByteArray {
        TODO("not implemented")
    }

    override fun toString(): String {
        return "ClientPlayerJoin(connectionId=$connectionId, game='$game', player='$player', character='$character', board='$board')"
    }
}