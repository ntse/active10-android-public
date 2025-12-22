package com.flipsidegroup.active10.data.models.requests

import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.models.BDBAnalyticsEvent
import com.flipsidegroup.active10.utils.Constants
import com.google.gson.annotations.SerializedName


data class AppEventRequest(
    @SerializedName("device_id") val deviceId: String = "",
    @SerializedName("os") val deviceOS: String = Constants.DEVICE_OS,
    @SerializedName("app_version") val appVersion: String = BuildConfig.VERSION_NAME,
    @SerializedName("events") val events: ArrayList<BDBAnalyticsEvent>
)