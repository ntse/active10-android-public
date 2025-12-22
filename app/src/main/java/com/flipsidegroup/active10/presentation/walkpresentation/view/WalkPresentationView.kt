package com.flipsidegroup.active10.presentation.walkpresentation.view

import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface WalkPresentationView : BaseView {

    fun onWalkDataReceived(stepList: MutableList<StepOverview>)
}