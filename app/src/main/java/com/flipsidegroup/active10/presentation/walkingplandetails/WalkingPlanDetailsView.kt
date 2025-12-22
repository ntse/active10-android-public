package com.flipsidegroup.active10.presentation.walkingplandetails

import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanCommonDialog
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanDebugDialog

interface WalkingPlanDetailsView : BaseView {
    fun showContent(content: List<WalkingPlanDetailsPart>)
    fun showDialog(dialog: WalkingPlanCommonDialog)
    fun showDialog(dialog: WalkingPlanDebugDialog)
    fun goToArticle(infoPage: InfoPage)
    fun goToWalksNear()
    fun goToWalkingPlanStatistics(planId: Long)
    fun funcItemAction(actionSlug: String?)
    fun pop()
}