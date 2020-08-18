package com.github.anvo.sds.extensions.bytearray

import java.nio.ByteBuffer
import java.nio.ByteOrder

@ExperimentalStdlibApi
fun ByteArray.getUShort(index: Int): UShort {
    return this[index].toUByte().toUShort() or this[index + 1].toUByte().toUShort().rotateLeft(8)
}

@ExperimentalStdlibApi
fun ByteArray.getString(index: Int, maxLength:Int = 32): String {
    return String(this.drop(index).take(maxLength).takeWhile { it.toInt() != 0 }.toByteArray(), Charsets.US_ASCII)
}

fun ByteArray.getFloat(index: Int): Float {
    return ByteBuffer.wrap(this, index, 4).order(ByteOrder.LITTLE_ENDIAN).float
}