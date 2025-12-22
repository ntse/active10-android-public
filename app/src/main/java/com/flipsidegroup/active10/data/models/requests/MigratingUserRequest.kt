package com.flipsidegroup.active10.data.models.requests

import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.models.DailyStepData
import com.flipsidegroup.active10.utils.Constants
import com.google.gson.annotations.SerializedName


data class MigratingUserRequest(
    @SerializedName("device_id") var deviceId: String = "",
    @SerializedName("app_version") val appVersion: String = BuildConfig.VERSION_NAME,
    @SerializedName("os") val deviceOS: String = Constants.DEVICE_OS,
    @SerializedName("old_app_daily_summary") var oldDailySummary: List<DailyStepData> = ArrayList(),
    @SerializedName("new_app_daily_summary") var newDailySummary: List<DailyStepData> = ArrayList()
)