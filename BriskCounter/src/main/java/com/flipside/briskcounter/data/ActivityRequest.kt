package com.flipside.briskcounter.data

import java.util.Date

data class ActivityRequest(
    var startDate: Date? = null,
    var endDate: Date? = null,
    var days: Int
)
