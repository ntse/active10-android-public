package com.flipsidegroup.active10.data.models.requests

import com.google.gson.annotations.SerializedName


data class UserTargetRequest(
    @SerializedName("device_id") val deviceId: String = "",
    @SerializedName("target") val target: Int,
    @SerializedName("created_at") val createdAt: String
)