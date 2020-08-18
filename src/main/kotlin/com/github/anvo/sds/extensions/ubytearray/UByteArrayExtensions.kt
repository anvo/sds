package com.github.anvo.sds.extensions.ubytearray

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset

fun UByteArray.setString(index: Int, string: String, charset: Charset) {
    string.toByteArray(charset).forEachIndexed { inner, byte ->
        this.set(index + inner, byte.toUByte())
    }
}

fun UByteArray.setUByteArray(index: Int, array: UByteArray) {
    array.forEachIndexed { inner, uByte -> this.set(index + inner, uByte ) }
}

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
fun UByteArray.setUShort(index: Int, value: UShort) {
    this[index] = value.toUByte()
    this[index + 1] = value.rotateLeft(8).toUByte()
}

fun UByteArray.setFloat(index: Int, value: Float) {
    ByteBuffer.wrap(this.asByteArray(), index, 4).order(ByteOrder.LITTLE_ENDIAN).putFloat(value)
}


