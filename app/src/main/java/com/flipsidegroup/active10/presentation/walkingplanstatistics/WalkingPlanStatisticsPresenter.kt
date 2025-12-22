package com.flipsidegroup.active10.presentation.walkingplanstatistics

import com.flipside.briskcounter.data.BriskActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface WalkingPlanStatisticsPresenter : LifecycleAwarePresenter<WalkingPlanStatisticsView> {
    fun loadContent(planId: Long)
    fun onTodayActivity(briskActivity: BriskActivity)
    fun loadBriskData()
}