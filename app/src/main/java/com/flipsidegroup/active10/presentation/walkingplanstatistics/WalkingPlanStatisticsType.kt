package com.flipsidegroup.active10.presentation.walkingplanstatistics

import androidx.annotation.DrawableRes
import com.flipsidegroup.active10.utils.WalkingPlanState

enum class WalkingPlanStatisticsType {
    STATISTICS,
    PRIMARY_BUTTON,
    SECONDARY_BUTTON,
    ADVICE_ITEM,
    FUNC_ITEM,
}

data class StatisticsWeek(
    val briskData: StatisticsInfo,
    val nonBriskData: StatisticsInfo,
)

data class StatisticsInfo(
    val currentWeekMinutes: Int,
    val totalWeekMinutes: Int,
)

sealed class WalkingPlanStatisticsPart(
    val type: WalkingPlanStatisticsType,
    val id: Int,
) {

    data class Statistics(
        val state: WalkingPlanState,
        val planName: String,
        val currentDayIndex: Int,
        val currentWeekIndex: Int,
        val totalDays: Int,
        val totalWeeks: List<StatisticsWeek>,
        val currentDayBriskMin: Int,
        val totalDayBriskMin: Int,
        val currentDayNonBriskMin: Int,
        val totalDayNonBriskMin: Int,
        val showCurrentWeek: Boolean,
    ) : WalkingPlanStatisticsPart(WalkingPlanStatisticsType.STATISTICS, -1)

    data class PrimaryButton(
        @DrawableRes val icon: Int? = null,
        val title: String,
        val onClickCallback: () -> Unit,
    ) : WalkingPlanStatisticsPart(WalkingPlanStatisticsType.PRIMARY_BUTTON, -2)

    data class SecondaryButton(
        val title: String,
        val onClickCallback: () -> Unit,
    ) : WalkingPlanStatisticsPart(WalkingPlanStatisticsType.SECONDARY_BUTTON, -3)

    data class AdviceItem(
        val title: String,
        val description: String,
    ) : WalkingPlanStatisticsPart(WalkingPlanStatisticsType.ADVICE_ITEM, -4)

    data class FuncItem(
        val itemId: Long,
        val slug: String,
        val title: String,
        val description: String,
        val actionTitle: String,
        val actionSlug: String,
        val image: String,
        val onClickCallback: (String) -> Unit,
    ) : WalkingPlanStatisticsPart(WalkingPlanStatisticsType.FUNC_ITEM, -5)
}