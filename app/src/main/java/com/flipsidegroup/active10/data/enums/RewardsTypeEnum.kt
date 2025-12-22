package com.flipsidegroup.active10.data.enums

import com.flipsidegroup.active10.R

enum class RewardsTypeEnum(
    val id: String,
    val position: Int,
    val title: String,
    val color: Int
) {

    GOAL_GETTER("goalGetter", 0, "Goal Getter", R.color.colorPrimary),
    TARGET_CHASER("targetChaser", 1, "Target Chaser", R.color.colorPrimary),
    STEPPING_UP("steppingUp", 2, "Stepping Up", R.color.colorPrimary),
    BRISK_MINUTES("briskMinutes", 3, "Brisk Minutes", R.color.colorPrimary);

    companion object {
        fun getHeaderTitle(id: String) = values().find { it.id == id }?.title ?: ""

        fun getTypeById(id: String) = values().find { it.id == id }
    }
}