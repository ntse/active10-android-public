package com.flipsidegroup.active10.data

enum class ActivityLevelEnum(val value: String) {
    // Don't change this values, they are used in the API
    INACTIVE("Inactive"),
    MODERATELY_ACTIVE("Moderately Active"),
    ACTIVE("Active");

    companion object {
        fun fromValue(value: String): ActivityLevelEnum? {
            return entries.find { it.value.equals(value, ignoreCase = true) }
        }
    }
}