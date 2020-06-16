package com.github.anvo.sds.model

class Game(val name:String,
           val players: MutableList<Player>) {

    class StatsEntry(
        val player: Player,
        var time: UByteArray,
        var points: Int,
        var totalPoints: Int
    )

    var stats = listOf<StatsEntry>()

    constructor(name: String) : this(name, mutableListOf())

    override fun toString(): String {
        return "Game(name='$name', players=$players)"
    }
}