package com.flipsidegroup.active10.utils

import com.flipside.briskcounter.data.StepData
import com.flipsidegroup.active10.data.models.DailyStepData
import com.flipsidegroup.active10.data.models.HourlyStepData
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.CurrentWalkingPlan
import java.time.LocalDate

private const val ACTIVE_TEN = 10

fun List<StepData>.buildDailyStepData(): DailyStepData {
    val activeSteps = this.filter { it.reachedActiveThreshold }
    val dailyStepData = DailyStepData()
    dailyStepData.briskMinutes = activeSteps.filter { it.isBrisk }.count() / 2
    dailyStepData.nonBriskMinutes = activeSteps.size / 2 - dailyStepData.briskMinutes
    dailyStepData.date = DateHelper.formatAnalyticsDate(this.first().startTime)
    dailyStepData.activeTens = dailyStepData.briskMinutes / ACTIVE_TEN
    dailyStepData.totalSteps = this.sumOf { it.stepCount }
    return dailyStepData
}

fun List<StepData>.filterByDaysForCurrentPlan(
    currentPlan: CurrentWalkingPlan,
    cmsPlan: WalkingPlan
): List<StepData> {
    if (currentPlan.planId != cmsPlan.id) throw Exception("Plan ID mismatch")

    val newStepsData = mutableListOf<StepData>()

    val stepDataByDays = groupBy { LocalDate.parse(it.date) }.toSortedMap()
    val requiredDaysSize = cmsPlan.planItineraryItems.size.times(7)
    val daysDiff = requiredDaysSize - currentPlan.days.size
    val filteredStepData = stepDataByDays.entries
        .take(daysDiff)
        .flatMap { it.value }
    newStepsData.addAll(filteredStepData)

    return newStepsData
}

fun List<HourlyStepData>.buildWalkingMinutesArrayList(
    isBrisk: Boolean
): ArrayList<Int> {
    val stepsArray =
        arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

    forEach {
        if (it.hour in 0..23) {
            stepsArray[it.hour] = if (isBrisk) {
                it.briskMinute
            } else {
                it.nonBriskMinute
            }
        }
    }

    return stepsArray
}