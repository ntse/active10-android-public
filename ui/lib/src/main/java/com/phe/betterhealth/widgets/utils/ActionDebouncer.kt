package com.phe.betterhealth.widgets.utils

import android.os.SystemClock
import kotlin.math.abs

class ActionDebouncer {

    private val minimumIntervalMillis: Long = 100

    private var previousClickTimestamp: Long = 0

    fun <T> onAction(defaultValue: T, listener: (() -> T)): T {
        val currentTimestamp = SystemClock.uptimeMillis()
        val isSucceed = abs(currentTimestamp - previousClickTimestamp) > minimumIntervalMillis

        previousClickTimestamp = currentTimestamp

        if (isSucceed) return listener()

        return defaultValue
    }
}
