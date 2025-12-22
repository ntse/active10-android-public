package com.flipsidegroup.active10.data.models.api

import com.google.gson.annotations.SerializedName

data class NhsDailyTargetRequest (
    @SerializedName("date")
    val timestampInSeconds: Long,
    @SerializedName("daily_target")
    val dailyTarget: Int
)

data class NhsDailyTargetResponse (
    val id: String,
    @SerializedName("date")
    val timestampInSeconds: Long,
    @SerializedName("daily_target")
    val dailyTarget: Int
)
