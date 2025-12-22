package com.flipsidegroup.active10.presentation.walkingplandetails

import androidx.annotation.DrawableRes
import com.flipsidegroup.active10.data.models.api.PlanItineraryItem
import com.flipsidegroup.active10.utils.WalkingPlanState

enum class WalkingPlanDetailsType {
    TAG_IMAGE,
    OVERVIEW,
    PRIMARY_BUTTON,
    SECONDARY_BUTTON,
    CHEVRON_ITEM,
    FUNC_ITEM,
}

sealed class WalkingPlanDetailsPart(
    val type: WalkingPlanDetailsType,
    val id: Int,
) {

    data class TagImage(
        val planTagline: String,
        val image: String,
    ) : WalkingPlanDetailsPart(WalkingPlanDetailsType.TAG_IMAGE, -1)

    data class Overview(
        val state: WalkingPlanState,
        val planName: String,
        val planDescription: String,
        val findWalksBtnTitle: String,
        val planDuration: String,
        val planGoal: String,
        val planDifficultyLevel: String,
        val planItineraryTitle: String,
        val planItineraryItems: List<PlanItineraryItem>,
    ) : WalkingPlanDetailsPart(WalkingPlanDetailsType.OVERVIEW, -2)

    data class PrimaryButton(
        val title: String,
        val onClickCallback: () -> Unit,
    ) : WalkingPlanDetailsPart(WalkingPlanDetailsType.PRIMARY_BUTTON, -3)

    data class SecondaryButton(
        val title: String,
        val onClickCallback: () -> Unit,
    ) : WalkingPlanDetailsPart(WalkingPlanDetailsType.SECONDARY_BUTTON, -4)

    data class ChevronItem(
        @DrawableRes val image: Int,
        val title: String,
        val onClickCallback: () -> Unit,
    ) : WalkingPlanDetailsPart(WalkingPlanDetailsType.CHEVRON_ITEM, -5)

    data class FuncItem(
        val itemId: Long,
        val slug: String,
        val title: String,
        val description: String,
        val actionTitle: String,
        val actionSlug: String,
        val image: String,
        val onClickCallback: (String) -> Unit,
    ) : WalkingPlanDetailsPart(WalkingPlanDetailsType.FUNC_ITEM, -6)
}