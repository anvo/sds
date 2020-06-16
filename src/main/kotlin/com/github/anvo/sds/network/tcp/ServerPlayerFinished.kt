package com.github.anvo.sds.network.tcp

import com.github.anvo.sds.extensions.ubytearray.setUByteArray
import com.github.anvo.sds.extensions.ubytearray.setUShort

/**
 * From server to client after [ClientGameFinished]
 *
 * Example:
 * 15 00 00 00                                        ....
 * 03 00 0c 00 00 00 b0 5f  09 43 f2 46 20 42 97 8f   ......._ .C.F B..
 * b0 42
 *
 * Format:
 * AA 00 00 00
 * BB 00
 * CC 00 00 00
 * DD DD DD DD DD DD DD DD DD DD DD DD
 *
 * AA: Type id
 * BB: Player position (1, 2 ,3 ...)
 * CC: Size
 * DD: Timestamp
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class ServerPlayerFinished(val position: Int,
                           val time: UByteArray) : TcpPackage() {

    companion object {
        const val id = 0x15
    }

    override fun toByteArray(): ByteArray {
        val buffer = UByteArray(22)
        buffer[0] = id.toUByte();

        buffer.setUShort(4, position.toUShort())
        buffer[6] = 0xcu // Size

        buffer.setUByteArray(10, time)

        return buffer.toByteArray()
    }

    override fun toString(): String {
        return "ServerPlayerFinished(position=$position, time=$time)"
    }
}