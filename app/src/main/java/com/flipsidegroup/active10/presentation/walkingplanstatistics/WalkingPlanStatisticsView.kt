package com.flipsidegroup.active10.presentation.walkingplanstatistics

import com.flipside.briskcounter.data.BriskPauseResume
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanBottomSheetDialog
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanCommonDialog

interface WalkingPlanStatisticsView : BaseView {
    fun showContent(content: List<WalkingPlanStatisticsPart>)
    fun showDialog(dialog: WalkingPlanCommonDialog)
    fun goToArticle(infoPage: InfoPage)
    fun goToWalksNear()
    fun goBack()
    fun onPlanResumed()
    fun doBriskCounter(pauseResumeList: List<BriskPauseResume>)
    fun showDialog(dialog: WalkingPlanBottomSheetDialog, showConfetti: Boolean)
    fun goToCouchAdvert()
    fun goToAnotherPlan(planId: Long)
    fun funcItemAction(actionSlug: String?)
}