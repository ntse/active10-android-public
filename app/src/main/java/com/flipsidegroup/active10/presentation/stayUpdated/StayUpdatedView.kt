package com.flipsidegroup.active10.presentation.stayUpdated

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface StayUpdatedView : BaseView {

    fun showData(content: ScreenContent)

}