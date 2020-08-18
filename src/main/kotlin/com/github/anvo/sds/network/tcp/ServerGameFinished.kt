package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.ubytearray.setFloat
import com.github.anvo.sds.extensions.ubytearray.setString
import com.github.anvo.sds.extensions.ubytearray.setUByteArray
import com.github.anvo.sds.extensions.ubytearray.setUShort
import com.github.anvo.sds.model.Game

/**
 * From server to client after [ClientGameFinished]
 *
 * Example:
 * 17 00 00 00                                        ....
 * 00 00                                              ..
 * 35 00 00 00                                        5...
 * 01 70 61 75 6c 00 00 00  00 00 00 00 00 00 00 00   .paul... ........
 * 00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00   ........ ........
 * 00 0a 00 00 00 0a 00 00  00 f0 4a 0b 43 5a 3d 11   ........ ..J.CZ=.
 * 42 c5 0a b6 42                                     B...B
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ServerGameFinished(val connectionId:UShort, val stats:List<Game.StatsEntry>) : TcpPackage() {

    companion object {
        const val id = 0x17
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(11 +  (this.stats.size * 52))
        buffer[0] = id.toUByte();

        buffer.setUShort(6, (1+ (this.stats.size * 52)).toUShort()) // Size of variable payload

        buffer[10] = stats.size.toUByte()

        stats.forEachIndexed { index, stat ->
            val base = 11 + (index * 52)

            buffer.setString(base, stat.player.name, Charsets.US_ASCII)

            buffer.setUShort(base + 32, stat.points.toUShort()) // Current points
            buffer.setUShort(base + 36, stat.totalPoints.toUShort()) // Total points from last run
            buffer.setFloat(base + 40, stat.time.total)
            buffer.setFloat(base + 44, stat.time.checkpoint1)
            buffer.setFloat(base + 48, stat.time.checkpoint2)
        }
        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ServerGameFinished(connectionId=$connectionId, stats=$stats)"
    }
}