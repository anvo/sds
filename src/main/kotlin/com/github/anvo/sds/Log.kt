package com.github.anvo.sds

object Log {

    enum class Level {
        TRAFFIC, PACKET, GAME, SERVER
    }

    val levels = mutableSetOf(Level.SERVER, Level.GAME)

    fun traffic(tag: String, buffer: () -> ByteArray) {
        if(!levels.contains(Level.TRAFFIC)) {
            return;
        }

        val values = buffer()
        val builder = StringBuilder()
        val numBytes = 16
        val numLines = (values.size / numBytes) +1
        (0 until  numLines).forEach { lineIndex ->
            val range = lineIndex * numBytes until kotlin.math.min((lineIndex + 1) * numBytes, values.size)
            if (range.isEmpty())
                return@forEach
            builder.append("[$tag] ")
            range.forEach { byteIndex ->
                builder.append(String.format("%02x ", values[byteIndex]))
            }
            repeat(numBytes - range.count()) {
                builder.append("   ") // Fill line
            }
            builder.append(" ")
            range.forEach { byteIndex ->
                if(Character.isValidCodePoint(values[byteIndex].toInt()) &&
                    !Character.isISOControl(values[byteIndex].toInt())) {
                    builder.append(String.format("%c", values[byteIndex]))
                } else {
                    builder.append(".")
                }
            }
            builder.append("\n")
        }
        print(builder.toString())
    }

    fun packet(tag: String, msg: () -> String) {
        log(Log.Level.PACKET, tag, msg)
    }

    fun game(msg: () -> String) {
        log(Level.GAME, "Game", msg)
    }

    fun server(tag: String, msg: () -> String) {
        log(Level.SERVER, tag, msg)
    }

    private fun log(level: Level, tag: String, msg: () -> String) {
        if(!levels.contains(level)) {
            return;
        }
        val content = msg();
        println("[$tag] $content")
    }

}