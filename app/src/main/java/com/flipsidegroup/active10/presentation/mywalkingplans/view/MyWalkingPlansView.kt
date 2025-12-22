package com.flipsidegroup.active10.presentation.mywalkingplans.view

import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.mywalkingplans.adapters.MyWalkingPlansPart

interface MyWalkingPlansView : BaseView {
    fun showData(content: List<MyWalkingPlansPart>)
    fun navigateToWalkingPlan(planId: Long)
    fun funcItemAction(actionSlug: String?)
}