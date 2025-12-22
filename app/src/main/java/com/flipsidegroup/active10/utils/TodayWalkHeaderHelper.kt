package com.flipsidegroup.active10.utils

import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.WalkingMessageResponse
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.targets.activities.DEFAULT_TARGET


private const val NO_ACTIVE_10 = 0
private const val ONE_ACTIVE_10 = 10
private const val TWO_ACTIVE_10 = 20
private const val THREE_ACTIVE_10 = 30
private const val FOUR_ACTIVE_10 = 40
private const val FIVE_ACTIVE_10 = 50
private const val EXTRA_ACTIVE_10 = 60

class TodayWalkHeaderHelper(
    private val settingsUtils: SettingsUtils
) {

    fun getHeaderText(briskMinutes: Int, walkingMessages: WalkingMessageResponse?): String {
        val defaultText = UIUtils.getString(R.string.today_walk_default_header)
        walkingMessages?.todayWalkTexts?.let {
            val currentTarget =
                settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: DEFAULT_TARGET

            val almostOneRange = 1..9
            val almostTwoRange = 11..19
            val almostThreeRange = 21..29
            val almostFourRange = 31..39
            val almostFiveRange = 41..49
            val almostSixRange = 51..59

            val myWalkingMessage =
                when (currentTarget) {
                    1 -> it.myWalksTarget1
                    2 -> it.myWalksTarget2
                    3 -> it.myWalksTarget3
                    4 -> it.myWalksTarget4
                    5 -> it.myWalksTarget5
                    else -> return defaultText
                }

            return when {
                briskMinutes == NO_ACTIVE_10 -> {
                    myWalkingMessage?.let { myWalkingMessage.noActive0Mins } ?: run { "" }
                }
                briskMinutes in almostOneRange -> {
                    myWalkingMessage?.let { myWalkingMessage.noActiveXMins } ?: run { "" }
                }
                briskMinutes == ONE_ACTIVE_10 -> {
                    myWalkingMessage?.let { myWalkingMessage.oneTarget0Mins } ?: run { "" }
                }
                briskMinutes in almostTwoRange -> {
                    myWalkingMessage?.let { myWalkingMessage.oneTargetXMins } ?: run { "" }
                }
                briskMinutes == TWO_ACTIVE_10 -> {
                    myWalkingMessage?.let { myWalkingMessage.twoTargets0Mins } ?: run { "" }
                }
                briskMinutes in almostThreeRange -> {
                    myWalkingMessage?.let { myWalkingMessage.twoTargetsXMins } ?: run { "" }
                }
                briskMinutes == THREE_ACTIVE_10 -> {
                    myWalkingMessage?.let { myWalkingMessage.threeTargets0Mins } ?: run { "" }
                }
                briskMinutes in almostFourRange -> {
                    myWalkingMessage?.let { myWalkingMessage.threeTargetsXMins } ?: run { "" }
                }
                briskMinutes == FOUR_ACTIVE_10 -> {
                    myWalkingMessage?.let { myWalkingMessage.fourTargets0Mins } ?: run { "" }
                }
                briskMinutes in almostFiveRange -> {
                    myWalkingMessage?.let { myWalkingMessage.fourTargetsXMins } ?: run { "" }
                }
                briskMinutes == FIVE_ACTIVE_10 -> {
                    myWalkingMessage?.let { myWalkingMessage.fiveTargets0Mins } ?: run { "" }
                }
                briskMinutes in almostSixRange -> {
                    myWalkingMessage?.let { myWalkingMessage.fiveTargetsXMins } ?: run { "" }
                }
                briskMinutes >= FIVE_ACTIVE_10 -> {
                    myWalkingMessage?.let { myWalkingMessage.moreFiveTargets } ?: run { "" }
                }
                else -> defaultText
            }
        }

        return defaultText
    }
}