package com.flipsidegroup.active10.presentation.pacechecker.start

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface PaceCheckerStartView : BaseView {

    fun showData(screen: ScreenContent)

    fun navigateToPaceCheckerFlat(screen: ScreenContent?)
}
