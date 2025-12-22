package com.flipsidegroup.active10.data

data class IntervalWalk(
    val interval: String,
    val year: Int? = null,
    val startTimestamp: Long,
    val endTimestamp: Long
)