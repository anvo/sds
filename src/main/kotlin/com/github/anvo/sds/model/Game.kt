package com.github.anvo.sds.model

class Game(val name:String,
           val players: MutableList<Player>) {

    class StatsEntry(
        val player: Player,
        var time: FinishTime,
        var points: Int,
        var totalPoints: Int
    )

    var stats = HashMap<Player, StatsEntry>()

    constructor(name: String) : this(name, mutableListOf())

    override fun toString(): String {
        return "Game(name='$name', players=$players)"
    }
}