package com.flipsidegroup.active10.presentation.pacechecker.timer

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class PaceCheckerTimerPresenter @Inject constructor(
    private val screenRepository: ScreenRepository
) : BasePresenter<PaceCheckerTimerView>() {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    private var isPreCountdownActive = false
    private var isPreCountdownPaused = false
    private var currentPreCountdownSecond = INITIAL_COUNTDOWN

    private var isCountdownActive = false
    private var isCountdownPaused = false
    private var currentCountdownSecond = MAIN_COUNTDOWN

    private var exitScreenContent: ScreenContent? = null
    private var pauseScreenContent: ScreenContent? = null

    private var lastRecordedSteps = 0f
    private var totalRecordedSteps = 0f

    fun loadData() {
        view?.showLoading()

        presenterScope.launch {
            runCatching {
                screenRepository.getScreenContentBySlug(ScreenRepository.PACE_CHECKER_ARE_YOU_SURE)
                    .await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { exitScreenContent = it }

            runCatching {
                screenRepository.getScreenContentBySlug(ScreenRepository.PACE_CHECKER_PAUSED)
                    .await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { pauseScreenContent = it }

            view?.hideLoading()
        }
    }

    fun onStartClicked() {
        if (isCountdownActive) {
            pauseMainCountdown()
            view?.showExitDialog(exitScreenContent!!, currentCountdownSecond)
            return
        }

        totalRecordedSteps = 0f
        lastRecordedSteps = 0f
        currentCountdownSecond = MAIN_COUNTDOWN

        view?.showCloseButton(false)

        presenterScope.launch {
            isPreCountdownActive = true

            for (i in currentPreCountdownSecond downTo 1) {
                if (isPreCountdownPaused) return@launch

                view?.showInitialCountdown(i)
                view?.hapticTik()
                currentPreCountdownSecond = i
                delay(1000)
            }

            if (isPreCountdownPaused) return@launch

            isPreCountdownActive = false

            startMainCountdown()
        }
    }

    private fun startMainCountdown() {
        presenterScope.launch {
            isCountdownActive = true

            for (i in currentCountdownSecond downTo 1) {
                if (isCountdownPaused) return@launch

                view?.showMainCountdown(i)
                currentCountdownSecond = i
                delay(1000)
            }

            if (isCountdownPaused) return@launch

            isCountdownActive = false

            view?.showCloseButton(true)
            view?.hapticTik()
            view?.playBellSound()
            view?.navigateToResult(totalRecordedSteps)
        }
    }

    fun resumeMainCountdown() {
        if (isPreCountdownPaused) {
            isPreCountdownPaused = false
            onStartClicked()
        } else if (isCountdownPaused) {
            isCountdownPaused = false
            lastRecordedSteps = 0f
            startMainCountdown()
        }
    }

    private fun pauseMainCountdown() {
        isCountdownPaused = true
        view?.pauseAnimation()
    }

    fun onPauseView() {
        if (isPreCountdownActive && !isPreCountdownPaused) {
            isPreCountdownPaused = true
            view?.showPauseDialog(pauseScreenContent)
        } else if (isCountdownActive && !isCountdownPaused) {
            pauseMainCountdown()
            view?.showPauseDialog(pauseScreenContent)
        }
    }

    fun onBackPressed() {
        if (isCountdownActive) {
            pauseMainCountdown()
            view?.showExitDialog(exitScreenContent, currentCountdownSecond)
        } else {
            view?.popView()
        }
    }

    fun recordSteps(steps: Float?) {
        if (steps == null || !isCountdownActive || isCountdownPaused) return

        if (lastRecordedSteps == 0f) {
            lastRecordedSteps = steps
        } else {
            val stepsDifference = steps - lastRecordedSteps
            totalRecordedSteps += stepsDifference
            lastRecordedSteps = steps
        }
    }

    override fun unbind() {
        isCountdownActive = false
        isPreCountdownActive = false
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }

    companion object {

        private const val INITIAL_COUNTDOWN = 3

        private const val MAIN_COUNTDOWN = 60
    }
}
