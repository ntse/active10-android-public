package com.flipside.briskcounter.data

data class StepData(
    var startTime: Long = 0,
    var endTime: Long = 0,
    var stepCount: Int = 0,
    var prefix: Int = 0,
    var isBrisk: Boolean = false,
    var reachedActiveThreshold: Boolean = false,
    var date: String = ""
)
