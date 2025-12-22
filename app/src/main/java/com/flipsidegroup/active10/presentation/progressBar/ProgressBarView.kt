package com.flipsidegroup.active10.presentation.progressBar

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface ProgressBarView : BaseView {

    fun showProgress(progressPercent: Int)

    fun navigateToNextScreen()

    fun showErrorMessage(message: String)

    fun showContent(screenContent: ScreenContent?)

}