package com.flipside.briskcounter

private const val BRISK_THRESHOLD_IN_STEPS = 100
private const val TO_MINUTES = 60

object BriskHelper {

    fun getCadence(totalSteps: Double, durationInSeconds: Double): Double {
        return totalSteps / (durationInSeconds / TO_MINUTES)
    }

    fun isBrisk(cadence: Double): Boolean = cadence >= BRISK_THRESHOLD_IN_STEPS
}
