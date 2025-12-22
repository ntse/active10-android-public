package com.flipsidegroup.active10.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class LocalNotificationInfo(
    val slug: String,
    val title: String,
    val description: String,
    val timestamp: LocalDateTime,
    val destination: String?,
    val planId: Long = -1,
    val isLapsed: Boolean = false,
    val intervalInDays: Int = 0,
): Parcelable