package com.flipsidegroup.active10.presentation.pacechecker.timer

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface PaceCheckerTimerView : BaseView {

    fun showInitialCountdown(sec: Int)

    fun showMainCountdown(sec: Int)

    fun showExitDialog(screenContent: ScreenContent?, seconds: Int)

    fun showPauseDialog(screenContent: ScreenContent?)

    fun popView()

    fun navigateToHome()

    fun hapticTik()

    fun navigateToResult(steps: Float)

    fun playBellSound()

    fun showCloseButton(show: Boolean)

    fun pauseAnimation()
}
