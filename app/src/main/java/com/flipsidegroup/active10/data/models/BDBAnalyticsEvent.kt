package com.flipsidegroup.active10.data.models

import com.google.gson.annotations.SerializedName


data class BDBAnalyticsEvent(
    @SerializedName("event_value") val eventValue: String,
    @SerializedName("event") val event: String,
    @SerializedName("created_at") val createdAt: String
)