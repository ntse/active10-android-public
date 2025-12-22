package com.flipsidegroup.active10.data.models

import com.flipsidegroup.active10.utils.DateHelper
import com.google.gson.annotations.SerializedName


data class DailyStepData(
    @SerializedName("non_brisk_minute") var nonBriskMinutes: Int = 0,
    @SerializedName("brisk_minute") var briskMinutes: Int = 0,
    @SerializedName("no_of_active10") var activeTens: Int = 0,
    @SerializedName("date") var date: String = DateHelper.formatAnalyticsDate(System.currentTimeMillis()),
    @SerializedName("total_steps") var totalSteps: Int = 0,
)