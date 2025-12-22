package com.flipsidegroup.active10.utils

import com.flipsidegroup.active10.data.models.StepOverview
import timber.log.Timber
import java.util.Calendar

object WalksUtils {

    fun isTargetSuccess3TimesInAnyOf2WeeksInRow(allData: List<StepOverview>, target: Int): Boolean {
        // we need at least 3 success days in a week, so we need to add 4 days before and after the data (somebody stars week later or earlier)
        val data = fourEmptyStepOverviews().plus(allData).plus(fourEmptyStepOverviews())

        for (startIndex in 0 until (data.size - 13)) {
            val endIndex = startIndex + 14
            val twoWeekSpan = data.subList(startIndex, endIndex)
            if (targetSucceed3TimesIn2Weeks(twoWeekSpan, target)) {
                return true
            }
        }
        return false
    }

    fun isTargetSuccess14TimesInAny2WeeksInRow(allData: List<StepOverview>, target: Int): Boolean {
        if (allData.size < 14) return false

        for (startIndex in 0 until (allData.size - 13)) {
            val endIndex = startIndex + 14
            val twoWeekSpan = allData.subList(startIndex, endIndex)
            if (targetSucceed14TimesIn2Weeks(twoWeekSpan, target)) {
                return true
            }
        }
        return false
    }

    fun fillMissingDaysWithEmptyData(data: List<StepOverview>): List<StepOverview> {
        val result = mutableListOf<StepOverview>()
        val calendar = Calendar.getInstance().apply {
            timeInMillis = data.firstOrNull()?.timestamp ?: 0L
        }
        data.forEach {
            val currentTimestamp = it.timestamp ?: 0L

            while (DateHelper.getStartTimestampOfDay(calendar.timeInMillis) < DateHelper.getStartTimestampOfDay(currentTimestamp)) {
                result.add(StepOverview(calendar.timeInMillis, 0, 0))
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            result.add(it)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return result
    }

    private fun fourEmptyStepOverviews() = listOf(
        StepOverview(0, 0 ,0 ),
        StepOverview(0, 0 ,0 ),
        StepOverview(0, 0 ,0 ),
        StepOverview(0, 0 ,0 ),
    )

    private fun targetSucceed3TimesIn2Weeks(data: List<StepOverview>, target: Int): Boolean {
        if (data.size < 14) return false

        val isFirstWeekSucceed = data.takeLast(14)
            .take(7)
            .filter { (it.totalBriskMin ?: 0) >= target.times(10) }
            .size >= 3

        val isSecondWeekSucceed = data.takeLast(7)
            .filter { (it.totalBriskMin ?: 0) >= target.times(10) }
            .size >= 3

        return isFirstWeekSucceed && isSecondWeekSucceed
    }

    private fun targetSucceed14TimesIn2Weeks(data: List<StepOverview>, target: Int): Boolean {
        if (data.size < 14) return false

        return data.takeLast(14)
            .filter { (it.totalBriskMin ?: 0) >= target.times(10) }
            .size >= 14
    }
}