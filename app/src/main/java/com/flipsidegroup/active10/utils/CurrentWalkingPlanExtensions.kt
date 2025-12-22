package com.flipsidegroup.active10.utils

import com.flipside.briskcounter.data.StepData
import com.flipsidegroup.active10.data.models.dataholders.CurrentWalkingPlanDay

fun List<StepData>.buildWalkingPlanDayData(): CurrentWalkingPlanDay {
    val activeSteps = this.filter { it.reachedActiveThreshold }
    val dailyStepData = CurrentWalkingPlanDay()
    val briskBucket = activeSteps.count { it.isBrisk }
    dailyStepData.totalBriskMin = briskBucket / 2
    dailyStepData.totalNonBriskMin = (activeSteps.size - briskBucket) / 2
    dailyStepData.timestamp = DateHelper.formatAnalyticsDate(this.first().startTime)
    dailyStepData.totalSteps = this.sumOf { it.stepCount }
    return dailyStepData
}