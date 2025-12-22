package com.flipsidegroup.active10.utils


private const val WHITE_SPACE = " "
private const val UNDERSCORE = "_"

object GoalHelper {

    fun formatGoals(goals: List<String>): List<String> {
        val formattedGoals = ArrayList<String>()

        goals.forEach {
            formattedGoals.add(
                it.replace(WHITE_SPACE, UNDERSCORE, true).toUpperCase()
            )
        }
        return formattedGoals
    }
}