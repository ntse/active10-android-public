package com.flipsidegroup.active10.utils

import java.time.Instant
import java.time.ZoneId

// Correct way to getting all of dates, saved in backend as 00:00:00 in UTC
//fun Long.utcToLocalDateString(): String {
//    if (this == 0L) return ""
//    return Instant.ofEpochSecond(this).atZone(ZoneOffset.UTC).toLocalDate().toString()
//}

// Created a workaround due to dates returned from iOS
fun Long.utcToLocalDateFromLocalZoneString(): String {
    if (this == 0L) return ""
    return Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).toLocalDate().toString()
}

fun Long.utcTimeMillisAndroidToTimeNanoSwift(): Double {
    return this / 1000.0 // convert Android milliseconds to Swift nanoseconds time format
}