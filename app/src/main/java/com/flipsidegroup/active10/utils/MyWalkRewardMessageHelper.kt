package com.flipsidegroup.active10.utils

import android.content.Intent
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.IntervalWalk
import com.flipsidegroup.active10.data.MyWalksMessages
import com.flipsidegroup.active10.data.PeriodTypeEnum
import com.flipsidegroup.active10.data.WalkingMessageResponse
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.targets.activities.SetTargetMode
import com.flipsidegroup.active10.presentation.targets.activities.TargetIntent
import com.flipsidegroup.active10.presentation.tips.activities.TipsIntent
import org.joda.time.DateTime


private const val DISCOVER_TIP_150_MIN_ID = 6
private const val MINIMUM_TARGET = 1
private const val WEEKLY_RECOMMENDATION_BRISK = 150
private const val ONE_DAY_TARGET = 1

private const val NO_TARGET = 0
private const val ONE_TARGET = 1
private val MISSED_TARGET = 2..3
private val ALMOST_TARGET = 4..6
private const val WEEK_TARGET = 7

enum class MyWalksRewardsDestination {
    TIPS, TARGETS, ARTICLE150, NOTHING
}

class MyWalkRewardMessageHelper(
    private val settingsUtils: SettingsUtils,
    private val todayWalkHeaderHelper: TodayWalkHeaderHelper
) {

    private var walkingMessages: WalkingMessageResponse? = null

    fun getBreakdownAccessibilityText(): String {
        val breakDownAccessibilityText = walkingMessages?.myWalksTexts?.breakdownAccessibilityText
        return if (breakDownAccessibilityText.isNullOrEmpty()) {
            UIUtils.getString(R.string.chart_container_content_description)
        } else {
            breakDownAccessibilityText
        }
    }


    fun getNoBadgesMessage(
        currentPeriodType: PeriodTypeEnum,
        currentInterval: IntervalWalk
    ): String {
        walkingMessages?.myWalksTexts?.let {
            return when (currentPeriodType) {
                PeriodTypeEnum.DAYS -> if (currentInterval.endTimestamp > DateTime.now().millis){
                    it.noRewardsToday
                } else {
                    it.noRewardsThisDay
                }
                PeriodTypeEnum.WEEKS -> it.noRewardsThisWeek
                PeriodTypeEnum.MONTHS -> it.noRewardsThisMonth
            }
        }

        return ""
    }

    fun getRewardMessage(
        currentPeriodType: PeriodTypeEnum,
        startTimestamp: Long,
        targetsHit: Int,
        brisk: Int,
        walkingMessages: WalkingMessageResponse?
    ): String {
        this.walkingMessages = walkingMessages

        walkingMessages?.myWalksTexts?.let {
            return when (currentPeriodType) {
                PeriodTypeEnum.DAYS -> getDayRewardMessage(startTimestamp, targetsHit, brisk, it)
                PeriodTypeEnum.WEEKS -> getWeekRewardMessage(startTimestamp, targetsHit, brisk, it)
                PeriodTypeEnum.MONTHS -> getMonthlyRewardMessage(targetsHit)
            }
        }

        return ""
    }

    fun getRewardMessageDestination(
        currentPeriodType: PeriodTypeEnum,
        startTimestamp: Long,
        targetsHit: Int,
        brisk: Int,
    ): MyWalksRewardsDestination {

        return when (currentPeriodType) {
            PeriodTypeEnum.DAYS -> getDayRewardDestination(startTimestamp, targetsHit)
            PeriodTypeEnum.WEEKS -> getWeekRewardDestination(startTimestamp, targetsHit, brisk)
            PeriodTypeEnum.MONTHS -> MyWalksRewardsDestination.NOTHING
        }
    }

    private fun getMonthlyRewardMessage(targetsHit: Int): String =
        UIUtils.getQuantityString(R.plurals.my_walks_rewards_count, targetsHit)

    private fun getDayRewardMessage(
        startTimestamp: Long,
        targetsHit: Int,
        todayBrisk: Int,
        myWalksTexts: MyWalksMessages
    ): String {

        val noBriskWalking = 0
        val targetHitDay = 1

        return when {
            DateHelper.isSameDay(startTimestamp) -> {
                if (todayBrisk == noBriskWalking) {
                    myWalksTexts.todayNoWalking
                } else {
                    todayWalkHeaderHelper.getHeaderText(todayBrisk, walkingMessages)
                }
            }
            todayBrisk == noBriskWalking -> myWalksTexts.daysNoBrisk
            targetsHit == targetHitDay -> myWalksTexts.daysTargetHit
            else -> myWalksTexts.daysTargetNoHit.format(todayBrisk)
        }
    }

    private fun getDayRewardDestination(
        startTimestamp: Long,
        todayBrisk: Int,
    ): MyWalksRewardsDestination {
        return when {
            !DateHelper.isSameDay(startTimestamp) && todayBrisk == 0 -> MyWalksRewardsDestination.TARGETS
            else -> MyWalksRewardsDestination.NOTHING
        }
    }

    private fun getWeekRewardMessage(
        startTimestamp: Long,
        targetsHit: Int,
        brisk: Int,
        myWalksTexts: MyWalksMessages
    ): String {

        val currentTarget = settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: 1

        return when {
            DateHelper.isSameWeek(startTimestamp) ->
                myWalksTexts.weekCurrent
            DateHelper.isLastWeek(startTimestamp) ->
                getLastWeekMessage(targetsHit, currentTarget, myWalksTexts)
            else ->
                getPreviousWeeksMessage(targetsHit, brisk, myWalksTexts)
        }
    }

    private fun getWeekRewardDestination(
        startTimestamp: Long,
        targetsHit: Int,
        brisk: Int,
    ): MyWalksRewardsDestination {

        val currentTarget = settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: 1

        return when {
            DateHelper.isSameWeek(startTimestamp) -> MyWalksRewardsDestination.NOTHING
            DateHelper.isLastWeek(startTimestamp) ->
                getLastWeekDestination(targetsHit, currentTarget)
            else ->
                getPreviousWeeksDestination(targetsHit, brisk)
        }
    }

    private fun getLastWeekMessage(
        targetsHit: Int,
        currentTarget: Int,
        myWalksTexts: MyWalksMessages
    ): String {
        return when {
            targetsHit == WEEK_TARGET && canIncreaseTarget(currentTarget) ->
                myWalksTexts.lastWeekTargetHitIncreaseTarget
            targetsHit == WEEK_TARGET ->
                myWalksTexts.weeksDays7
            targetsHit in ALMOST_TARGET && canIncreaseTarget(currentTarget) ->
                myWalksTexts.lastWeek4_6IncreaseTarget.format(targetsHit)
            targetsHit in ALMOST_TARGET ->
                myWalksTexts.weeksDays4_6.format(targetsHit)
            targetsHit == ONE_DAY_TARGET && currentTarget == MINIMUM_TARGET ->
                myWalksTexts.lastWeekTargetOneDaysOne
            targetsHit == ONE_DAY_TARGET ->
                myWalksTexts.lastWeekTargetXDaysOne
            currentTarget == MINIMUM_TARGET ->
                myWalksTexts.lastWeekTargetOneDaysX.format(targetsHit)
            else ->
                myWalksTexts.lastWeekTargetXDaysX.format(targetsHit)
        }
    }

    private fun canIncreaseTarget(currentTarget: Int): Boolean {
        val canIncreaseToFive = (settingsUtils.getSettingsHolder().earnedHighAchieversBadgeTarget ?: 0) >= 3
        val maxTarget = if (canIncreaseToFive) 5 else 3
        return currentTarget < maxTarget
    }

    private fun getLastWeekDestination(
        targetsHit: Int,
        currentTarget: Int,
    ): MyWalksRewardsDestination {
        return when {
            targetsHit == WEEK_TARGET && canIncreaseTarget(currentTarget) -> MyWalksRewardsDestination.TARGETS
            targetsHit in 4..6 && canIncreaseTarget(currentTarget) -> MyWalksRewardsDestination.NOTHING
            targetsHit in 1..3 && currentTarget > 1 -> MyWalksRewardsDestination.TARGETS
            targetsHit in 1..3 && currentTarget == 1 -> MyWalksRewardsDestination.TIPS
            else -> MyWalksRewardsDestination.NOTHING
        }
    }

    private fun getPreviousWeeksMessage(
        targetsHit: Int,
        brisk: Int,
        myWalksTexts: MyWalksMessages
    ): String {
        return when {
            brisk >= WEEKLY_RECOMMENDATION_BRISK ->
                myWalksTexts.weekBrisk150
            targetsHit == NO_TARGET ->
                myWalksTexts.weekDays0
            targetsHit == ONE_TARGET ->
                myWalksTexts.weekDays1
            targetsHit in MISSED_TARGET ->
                myWalksTexts.weekDays2_3.format(targetsHit)
            targetsHit in ALMOST_TARGET ->
                myWalksTexts.weeksDays4_6.format(targetsHit)
            else ->
                myWalksTexts.weeksDays7
        }
    }

    private fun getPreviousWeeksDestination(
        targetsHit: Int,
        brisk: Int,
    ): MyWalksRewardsDestination {
        return when {
            brisk >= 150 -> MyWalksRewardsDestination.ARTICLE150
            targetsHit == 0 -> MyWalksRewardsDestination.TARGETS
            else -> MyWalksRewardsDestination.NOTHING
        }
    }

    fun getRewardAction(
        currentPeriodType: PeriodTypeEnum,
        startTimestamp: Long,
        targetsHit: Int,
        brisk: Int
    ): Intent? {

        val currentTarget = settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: 1

        if (currentPeriodType == PeriodTypeEnum.DAYS &&
            !DateHelper.isSameDay(startTimestamp) && brisk == 0
        ) {
            return UIUtils.getAppContext().TipsIntent()
        } else if (currentPeriodType == PeriodTypeEnum.WEEKS) {
            when {
                DateHelper.isLastWeek(startTimestamp) ->
                    return lastWeekRewardAction(targetsHit, currentTarget)
                !DateHelper.isSameWeek(startTimestamp) ->
                    return previousWeekRewardAction(targetsHit, brisk)
            }
        }

        return null
    }

    private fun lastWeekRewardAction(targetsHit: Int, currentTarget: Int): Intent? {
        when {
            targetsHit == WEEK_TARGET && canIncreaseTarget(currentTarget) ->
                return UIUtils.getAppContext().TargetIntent(SetTargetMode.SETTINGS)
            targetsHit in ALMOST_TARGET && currentTarget > MINIMUM_TARGET ->
                return UIUtils.getAppContext().TargetIntent(SetTargetMode.SETTINGS)
            targetsHit in MISSED_TARGET && currentTarget == MINIMUM_TARGET ->
                return UIUtils.getAppContext().TipsIntent()
        }

        return null
    }

    private fun previousWeekRewardAction(targetsHit: Int, brisk: Int): Intent? {
        when {
            brisk >= WEEKLY_RECOMMENDATION_BRISK ->
                return UIUtils.getAppContext().TipsIntent()
            targetsHit == NO_TARGET ->
                return UIUtils.getAppContext().TargetIntent(SetTargetMode.SETTINGS)
        }

        return null
    }
}