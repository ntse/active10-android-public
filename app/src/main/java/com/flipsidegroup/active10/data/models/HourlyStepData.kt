package com.flipsidegroup.active10.data.models

import com.flipsidegroup.active10.utils.DateHelper
import com.google.gson.annotations.SerializedName


data class HourlyStepData(
    @SerializedName("lng") var longitude: Float = 0f,
    @SerializedName("lat") var latitude: Float = 0f,
    @SerializedName("hour") var hour: Int = 0,
    @SerializedName("brisk_minute") var briskMinute: Int = 0,
    @SerializedName("non_brisk_minute") var nonBriskMinute: Int = 0,
    @SerializedName("date") var date: String = DateHelper.formatAnalyticsDate(System.currentTimeMillis()),
    @SerializedName("created_at") var createdAt: String = DateHelper.formatStepDataTimestamp(System.currentTimeMillis())
)