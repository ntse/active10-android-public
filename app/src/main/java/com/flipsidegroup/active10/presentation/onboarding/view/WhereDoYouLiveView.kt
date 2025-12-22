package com.flipsidegroup.active10.presentation.onboarding.view

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface WhereDoYouLiveView: BaseView {
    fun onContentReceived(screenContent: ScreenContent?)

}