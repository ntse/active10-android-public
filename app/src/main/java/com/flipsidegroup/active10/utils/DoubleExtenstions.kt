package com.flipsidegroup.active10.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun Double.convertSecondsToMinAndHoursString() = if (this >= 3600.0) {
    val hours = (this / 3600).toInt()
    val minutes = ((this % 3600) / 60).toInt()
    "${hours}h ${minutes}min"
} else {
    val minutes = (this / 60).toInt()
    "${minutes}min"
}

fun Double.utcSwiftToLocalDateTimeString(): String {
    if (this == 0.0) return ""
    val seconds = this.toLong()
    val fractionalSeconds = (this - seconds) * 1_000_000_000.0 // convert to nanoseconds

    val instant = Instant.ofEpochSecond(seconds, fractionalSeconds.toLong())

    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toString()
}

fun Double.utcNanoSwiftToUtcMillisAndroid(): Long {
    val seconds = this.toLong()
    val fractionalSeconds = (this - seconds) * 1_000_000_000.0 // convert to nanoseconds

    val instant = Instant.ofEpochSecond(seconds, fractionalSeconds.toLong())

    return instant.toEpochMilli() // convert to milliseconds
}