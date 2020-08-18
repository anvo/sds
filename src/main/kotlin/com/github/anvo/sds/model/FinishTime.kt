package com.github.anvo.sds.model

class FinishTime(
    val total: Float,
    val checkpoint1: Float,
    val checkpoint2: Float
) {
    override fun toString(): String {
        return "FinishTime(total=$total, checkpoint1=$checkpoint1, checkpoint2=$checkpoint2)"
    }
}