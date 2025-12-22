package com.flipside.briskcounter.internal


interface SensorStepListener {

    fun onSuccess(cadence: Double, isBrisk: Boolean)
    fun onFailure(sensorStepError: String)
}
