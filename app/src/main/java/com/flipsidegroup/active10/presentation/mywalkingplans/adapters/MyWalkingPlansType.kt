package com.flipsidegroup.active10.presentation.mywalkingplans.adapters

import com.flipsidegroup.active10.utils.WalkingPlanState

enum class MyWalkingPlansType {
    PLAN,
    FUNC_ITEM,
}

sealed class MyWalkingPlansPart(
    val type: MyWalkingPlansType,
    val id: Int,
) {
    data class Plan(
        val state: WalkingPlanState,
        val planId: Long,
        val planName: String,
        val planCode: String,
        val planTagline: String,
        val planDuration: String,
        val planDescription: String,
        val image: String,
        val buttonTitle: String,
        val onClickCallback: (Long) -> Unit,
    ) : MyWalkingPlansPart(MyWalkingPlansType.PLAN, -1)

    data class FuncItem(
        val itemId: Long,
        val slug: String,
        val title: String,
        val description: String,
        val actionTitle: String,
        val actionSlug: String,
        val image: String,
        val onClickCallback: (String) -> Unit,
    ) : MyWalkingPlansPart(MyWalkingPlansType.FUNC_ITEM, -2)
}