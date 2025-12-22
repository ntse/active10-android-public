package com.flipsidegroup.active10.presentation.circularwalk

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface CircularWalkDetailsView : BaseView {
    fun showContent(content: ScreenContent)
}