package com.flipsidegroup.active10.data.models.requests

import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.models.HourlyStepData
import com.flipsidegroup.active10.utils.Constants
import com.google.gson.annotations.SerializedName


data class HourlySummaryRequest(
    @SerializedName("app_version") val appVersion: String = BuildConfig.VERSION_NAME,
    @SerializedName("os") val deviceOS: String = Constants.DEVICE_OS,
    @SerializedName("device_id") val deviceId: String = "",
    @SerializedName("activities") var activities: List<HourlyStepData>
)