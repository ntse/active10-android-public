package com.flipsidegroup.active10.presentation.activitylevel

import com.flipsidegroup.active10.data.models.ClassicUser
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface ActivityLevelView : BaseView {

    fun showContent(content: ScreenContent)

    fun showData(classicUser: ClassicUser?)

    fun onBack()

}