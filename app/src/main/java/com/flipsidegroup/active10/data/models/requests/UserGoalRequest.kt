package com.flipsidegroup.active10.data.models.requests

import com.flipsidegroup.active10.utils.DateHelper
import com.google.gson.annotations.SerializedName



data class UserGoalRequest(

    @SerializedName("device_id")
    val deviceId: String = "",

    @SerializedName("goals")
    val goalsList: List<String>,

    @SerializedName("created_at")
    val createdAt: String = DateHelper.formatStepDataTimestamp(System.currentTimeMillis())
)