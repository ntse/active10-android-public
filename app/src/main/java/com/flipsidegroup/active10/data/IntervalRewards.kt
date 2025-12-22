package com.flipsidegroup.active10.data

class IntervalRewards(
    val startTimestamp: Long,
    val endTimestamp: Long,
    val totalWalk: Int,
    val briskWalk: Int,
    val rewards: ArrayList<Reward>,
    val targetCount: Int
)