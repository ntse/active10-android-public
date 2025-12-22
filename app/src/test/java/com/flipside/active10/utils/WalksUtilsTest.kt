package com.flipside.active10.utils

import com.flipside.briskcounter.data.StepData
import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.utils.WalksUtils
import org.junit.Assert
import org.junit.Test
import java.util.Calendar

class WalksUtilsTest {

    companion object {
        private val emptyWalks = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        private val walksWithSucceed3Target = listOf(
            30, 20, 10, 30, 0, 20, 30, // Week 1 - success
            30, 20, 10, 30, 0, 20, 30, // Week 2 - success
            8, 20, 10, 10, 0, 20, 10, // Week 3 - failure
        )
        private val walksWithSucceed3TargetBetweenWeeks = listOf(
            10, 20, 10, 30, 0, 20, 30, // Week 1 - success from 4
            30, 20, 10, 30, 0, 20, 30, // Week 2 - success
            8, 30, 10, 10, 0, 20, 10, // Week 3  - success to 2 day
        )
        private val walksOnlyFor10Days = listOf(
            30, 0, 30, 0, 30, 0, 0, // Week 1 - success 3 days
            30, 30, 30 // Week 2 - success 3 days
        )
        private val successWalksFor6DaysInTheROw = listOf(
            30, 30, // Week 1 - success 3 days
            30, 30, 30, 30// Week 2 - success 3 days
        )
        private val walksWith4SuccessesInFirstWeekAnd2InSecond = listOf(
            30, 30, 30, 30, 0, 0, 0, // Week 1 - success 4 days
            0, 0, 0, 0, 0, 30, 30// Week 2 - success 2 days
        )
        private val walksSuccessIn14DaysInARow = listOf(
            0, 0, 0, 0, 20, 20, 20, // Week 1 - success from 4
            30, 20, 20, 30, 20, 20, 20, // Week 2 - success
            20, 20, 20, 20, 0, 0, 0, // Week 3  - success to 2 day
        )
    }

    @Test
    fun `isTargetSuccess3TimesInAnyOf2WeeksInRow for empty weeks`() {
        val stepOverview = generateDays(emptyWalks)

        val result = WalksUtils.isTargetSuccess3TimesInAnyOf2WeeksInRow(stepOverview, 3)

        Assert.assertEquals(false, result)
    }

    @Test
    fun `isTargetSuccess3TimesInAnyOf2WeeksInRow for success 2 first weeks from 3`() {
        val stepOverview = generateDays(walksWithSucceed3Target)

        val result = WalksUtils.isTargetSuccess3TimesInAnyOf2WeeksInRow(stepOverview, 3)

        Assert.assertEquals(true, result)
    }

    @Test
    fun `isTargetSuccess3TimesInAnyOf2WeeksInRow for success between middle of 1 and middle of 3 weeks`() {
        val stepOverview = generateDays(walksWithSucceed3TargetBetweenWeeks)

        val result = WalksUtils.isTargetSuccess3TimesInAnyOf2WeeksInRow(stepOverview, 3)

        Assert.assertEquals(true, result)
    }

    @Test
    fun `isTargetSuccess3TimesInAnyOf2WeeksInRow for only 10 days`() {
        val stepOverview = generateDays(walksOnlyFor10Days)

        val result = WalksUtils.isTargetSuccess3TimesInAnyOf2WeeksInRow(stepOverview, 3)

        Assert.assertEquals(true, result)
    }

    @Test
    fun `isTargetSuccess3TimesInAnyOf2WeeksInRow for only 6 successes in a row`() {
        val stepOverview = generateDays(successWalksFor6DaysInTheROw)

        val result = WalksUtils.isTargetSuccess3TimesInAnyOf2WeeksInRow(stepOverview, 3)

        Assert.assertEquals(true, result)
    }

    @Test
    fun `isTargetSuccess3TimesInAnyOf2WeeksInRow for 4 successes in first week and only 2 in second`() {
        val stepOverview = generateDays(walksWith4SuccessesInFirstWeekAnd2InSecond)

        val result = WalksUtils.isTargetSuccess3TimesInAnyOf2WeeksInRow(stepOverview, 3)

        Assert.assertEquals(false, result)
    }

    @Test
    fun `isTargetSuccess14TimesInAny2WeeksInRow succeed`() {
        val stepOverview = generateDays(walksSuccessIn14DaysInARow)

        val result = WalksUtils.isTargetSuccess14TimesInAny2WeeksInRow(stepOverview, 2)

        Assert.assertEquals( true, result)
    }

    @Test
    fun `isTargetSuccess14TimesInAny2WeeksInRow failure`() {
        val stepOverview = generateDays(walksWithSucceed3TargetBetweenWeeks)

        val result = WalksUtils.isTargetSuccess14TimesInAny2WeeksInRow(stepOverview, 2)

        Assert.assertEquals(false, result)
    }

    @Test
    fun `isTargetSuccess14TimesInAny2WeeksInRow for only 10 days`() {
        val stepOverview = generateDays(walksOnlyFor10Days)

        val result = WalksUtils.isTargetSuccess14TimesInAny2WeeksInRow(stepOverview, 2)

        Assert.assertEquals(false, result)
    }

    @Test
    fun `fillMissingDaysWithMissingDays`() {
        val firstDay = 1696924800000 // 10 Oct 2023 - 10:00
        val thirdDay = 1697097600000 // 12 Oct 2023 - 10:00
        val stepsWithMissingDay = listOf(
            StepOverview(firstDay, 10, 10),
            StepOverview(thirdDay, 10, 10),
        )
        val updatedSteps = WalksUtils.fillMissingDaysWithEmptyData(stepsWithMissingDay)

        Assert.assertEquals(3, updatedSteps.size)
    }

    @Test
    fun `fillMissingDaysWithMissingDays different hours`() {
        val firstDay = 1696924800000 // 10 Oct 2023 - 10:00
        val thirdDay = 1697101200000 // 12 Oct 2023 - 11:00
        val stepsWithMissingDay = listOf(
            StepOverview(firstDay, 10, 10),
            StepOverview(thirdDay, 10, 10),
        )
        val updatedSteps = WalksUtils.fillMissingDaysWithEmptyData(stepsWithMissingDay)

        Assert.assertEquals(3, updatedSteps.size)
    }

    @Test
    fun `fillMissingDaysWithMissingDays more missing days`() {
        val firstDay = 1696924800000 // 10 Oct 2023 - 10:00
        val thirdDay = 1697101200000 // 12 Oct 2023 - 11:00
        val sixthDay = 1697338800000 // 15 Oct 2023 - 5:00
        val stepsWithMissingDay = listOf(
            StepOverview(firstDay, 10, 10),
            StepOverview(thirdDay, 10, 10),
            StepOverview(sixthDay, 10, 10),
        )
        val updatedSteps = WalksUtils.fillMissingDaysWithEmptyData(stepsWithMissingDay)

        Assert.assertEquals(6, updatedSteps.size)
    }

    private fun generateDays(walks: List<Int>): List<StepOverview> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = 1698134400000  // 24 Oct 2023 - 10:00
        }
        return walks.map {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            StepOverview(
                timestamp = calendar.timeInMillis,
                totalBriskMin = it,
                totalWalkMin = it + 30 // brisk + 30 minutes of normal walk
            )
        }
    }



}