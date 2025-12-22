package com.flipsidegroup.active10.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

fun LocalDateTime.toUtcMillis(): Long {
    val zonedDateTime = this.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC)
    val timestampMillis = zonedDateTime.toInstant().toEpochMilli()
    return timestampMillis
}

fun LocalDateTime.toUtcSwiftMillisAsDouble(): Double {
    val zonedDateTime = this.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC)
    val timestampMillis = zonedDateTime.toInstant().toEpochMilli().toDouble()
    return timestampMillis / 1000.0
}

// A way to saving all of LocalDate (regardless of time zone)
// as UTC timestamp with time 00:00:00
//fun LocalDate.toUtcSecondsAsLong(): Long {
//    val zonedDateTime = this.atStartOfDay(ZoneOffset.UTC)
//    return zonedDateTime.toEpochSecond()
//}

// Created a workaround due to dates returned from iOS
fun LocalDate.fromLocalZoneToUtcSecondsAsLong(): Long {
    val localZonedDateTime = this.atStartOfDay(ZoneId.systemDefault())
    val utcZonedDateTime = localZonedDateTime.withZoneSameInstant(ZoneOffset.UTC)
    return utcZonedDateTime.toEpochSecond()
}