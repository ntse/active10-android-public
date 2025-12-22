package com.flipsidegroup.active10.presentation.walkingplandetails

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface WalkingPlanDetailsPresenter : LifecycleAwarePresenter<WalkingPlanDetailsView> {

    fun checkIfUserIsLoggedIn()

    fun loadContent(planId: Long)
}