package com.flipsidegroup.active10.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalNotificationNavInfo(
    val destination: String? = "null",
    val planId: Long = -1,
): Parcelable