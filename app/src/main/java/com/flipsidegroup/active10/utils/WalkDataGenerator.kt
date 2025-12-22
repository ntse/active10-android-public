package com.flipsidegroup.active10.utils

import com.flipsidegroup.active10.data.IntervalRewards
import com.flipsidegroup.active10.data.IntervalWalk
import com.flipsidegroup.active10.data.Reward
import com.flipsidegroup.active10.data.models.StepOverview
import java.util.*
import java.util.concurrent.TimeUnit

private const val MAX_DAY_PROGRESS = 100

object WalkDataGenerator {

    private var intervalList = ArrayList<IntervalRewards>()
    private var stepDataList = ArrayList<StepOverview>()
    private var installationDate: Calendar? = null
    private var minimumActive10: Int = 0

    init {
        installationDate = generateInstalledDate()
        stepDataList.addAll(generateStepDataList())
    }

    fun setMinimumActive10Walks(minActive10: Int) {
        this.minimumActive10 = minActive10
        stepDataList.clear()
        stepDataList.addAll(generateStepDataList())
    }

    fun generateInstalledDate(): Calendar {
        if (installationDate != null) {
            return installationDate!!
        }

        val installedDate = Calendar.getInstance()
        installedDate.add(Calendar.YEAR, -1)
        installedDate.set(Calendar.MONTH, Calendar.JANUARY)
        installedDate.set(Calendar.DAY_OF_MONTH, 1)
        return DateHelper.getStartOfDay(installedDate)
    }

    fun getStepData(): ArrayList<StepOverview> {
        if (stepDataList.isEmpty()) {
            stepDataList.addAll(generateStepDataList())
        }

        return stepDataList
    }

    fun getStepDataByInterval(interval: IntervalWalk): IntervalRewards {
        var stepData = getSavedIntervalData(interval)
        if (stepData == null) {
            stepData = getIntervalRewards(interval)
            intervalList.add(stepData)
        }

        return stepData
    }

    fun getGeneratedDataByInterval(start: Long, end: Long): MutableList<StepOverview> {
        val dataInInterval = ArrayList<StepOverview>()
        if (stepDataList.isEmpty()) {
            stepDataList.addAll(generateStepDataList())
        }
        for (step in stepDataList) {
            if (step.timestamp != null) {
                if (step.timestamp!! in start..end) {
                    dataInInterval.add(step)
                }
            }

        }

        return dataInInterval.toMutableList()
    }

    private fun generateStepDataList(): MutableList<StepOverview> {
        val data: MutableList<StepOverview> = mutableListOf()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        installationDate?.timeInMillis?.let {
            calendar.timeInMillis = it
        }

        val dayDiff: Int =
            TimeUnit.MILLISECONDS.toDays((System.currentTimeMillis() - calendar.timeInMillis))
                .toInt()

        var stepData: StepOverview
        var totalWalk: Int
        var totalBrisk: Int
        for (i in 0..dayDiff) {
            totalWalk = (minimumActive10.times(10)..MAX_DAY_PROGRESS).random()
            totalBrisk = (minimumActive10.times(10)..totalWalk).random()
            stepData = StepOverview(calendar.timeInMillis, totalBrisk, totalWalk, totalWalk.times(70))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            data.add(stepData)
        }
        setTargetMonth(data)

        return data
    }

    private fun setTargetMonth(data: List<StepOverview>) {
        val startDay = (0..(data.size - 100)).random()
        val end = startDay + 30
        var newBrisk: Int
        var newWalk: Int
        for (i in startDay..end) {
            newWalk = (30..MAX_DAY_PROGRESS).random()
            newBrisk = (30..newWalk).random()
            data[i].totalWalkMin = newWalk
            data[i].totalBriskMin = newBrisk
        }
    }

    private fun getIntervalRewards(interval: IntervalWalk): IntervalRewards {
        var totalWalk = 0
        var briskWalk = 0
        val rewards = ArrayList<Reward>()
        for (i in 10 downTo 0) {
            rewards.add(Reward(i, 0))
        }

        var active10Count: Int
        var remainCount: Int
        var targetDays = 0

        var start =
            stepDataList.indexOfFirst {
                DateHelper.isSameDay(
                    it.timestamp!!,
                    interval.startTimestamp
                )
            }
        if (start == -1) {
            start = 0
        }

        val dayDiff: Int =
            TimeUnit.MILLISECONDS.toDays((interval.endTimestamp - interval.startTimestamp)).toInt()
        val end = if (start + dayDiff + 1 > stepDataList.size) {
            stepDataList.size
        } else {
            start + dayDiff + 1
        }

        val subList = stepDataList.subList(start, end)

        var dayTargetCount: Int
        for (stepOverview in subList) {
            totalWalk += stepOverview.totalWalkMin ?: 0
            briskWalk += stepOverview.totalBriskMin ?: 0
            active10Count = (stepOverview.totalBriskMin ?: 0) / 10
            remainCount = (stepOverview.totalBriskMin ?: 0) - active10Count * 10
            if (remainCount != 0) rewards.find { it.id == remainCount }?.let { it.count++ }
            rewards.find { it.id == 10 }?.let { it.count += active10Count }
            dayTargetCount = (1..3).random()
            targetDays += if (active10Count >= dayTargetCount) 1 else 0
        }

        return IntervalRewards(
            startTimestamp = interval.startTimestamp,
            endTimestamp = interval.endTimestamp,
            totalWalk = totalWalk,
            briskWalk = briskWalk,
            rewards = rewards,
            targetCount = targetDays
        )
    }

    private fun getSavedIntervalData(interval: IntervalWalk) =
        intervalList.firstOrNull {
            it.startTimestamp == interval.startTimestamp && it.endTimestamp == interval.endTimestamp
        }
}