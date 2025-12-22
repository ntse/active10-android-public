package com.flipside.briskcounter.internal

import com.flipside.briskcounter.data.StepData

interface PastActivityListener {
    fun onSuccess(stepDataList: List<StepData>)
    fun onFailure(pastActivityError: String)
}
