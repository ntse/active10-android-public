package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName


data class MigrationActivity(
    @SerializedName("brisk_mins") var briskMins: Int = 0,
    @SerializedName("total_mins") var totalMins: Int = 0,
    @SerializedName("date") val timestamp: String = ""
)