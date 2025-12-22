package com.flipsidegroup.active10.presentation.signIn

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface SignInView : BaseView {

    fun showData(contentMap: Map<String, ScreenContent>)

    fun showTripDialog(contents: List<ScreenContent>)

}