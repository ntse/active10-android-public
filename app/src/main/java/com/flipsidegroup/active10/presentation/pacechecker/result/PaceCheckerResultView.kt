package com.flipsidegroup.active10.presentation.pacechecker.result

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface PaceCheckerResultView : BaseView {

    fun showData(content: PaceCheckerResultContent)

    fun showUsefulDialog(screenContent: ScreenContent?)

    fun showExploreDialog(screenContent: ScreenContent?)

    fun closePaceChecker()

    fun popViewToTimer()
}
