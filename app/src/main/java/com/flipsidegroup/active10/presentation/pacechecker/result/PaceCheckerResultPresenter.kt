package com.flipsidegroup.active10.presentation.pacechecker.result

import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlin.math.roundToInt

class PaceCheckerResultPresenter @Inject constructor(
    private val screenRepository: ScreenRepository,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val preferenceRepository: PreferenceRepository,
) : BasePresenter<PaceCheckerResultView>() {

    private val briskContent = PaceCheckerResultContent(
        title = "You're walking briskly!",
        description = "Your brisk walking is great, keep up the good work!",
        continueButtonTitle = "Start again",
        captionText = "Brisk walking is 100 steps per minute",
        captionIconRes = R.drawable.ic_todayswalks_teal,
        averageImgRes = R.drawable.img_pace_checker_result_positive,
    )

    private val slightlyUnderBriskContent = PaceCheckerResultContent(
        title = "You’re slightly under brisk",
        description = "You are almost there, keep up the good work!",
        continueButtonTitle = "Let’s give it another go",
        captionText = "Brisk walking is 100 steps per minute",
        captionIconRes = R.drawable.ic_todayswalks_teal,
        averageImgRes = R.drawable.img_pace_checker_result_negative,
    )

    private val underBriskContent = PaceCheckerResultContent(
        title = "You're under brisk pace",
        description = "But don't give up! You can find tips for success in our Discover section.",
        continueButtonTitle = "Let’s give it another go",
        captionText = "Brisk walking is 100 steps per minute",
        captionIconRes = R.drawable.ic_todayswalks_teal,
        averageImgRes = R.drawable.img_pace_checker_result_negative,
    )

    private val errorContent = PaceCheckerResultContent(
        title = "Activity not captured",
        description = "Oops! We weren't able to capture any of your walking activity.",
        continueButtonTitle = "Let’s give it another go",
        captionText = "The app experienced an unexpected error and could not track your walking. Please try again.",
        cadence = "no data",
        captionIconRes = R.drawable.ic_pace_checker_error,
        averageImgRes = R.drawable.img_pace_checker_result_error,
        isError = true,
    )

    private var usefulScreenContent: ScreenContent? = null
    private var exploreScreenContent: ScreenContent? = null
    private var isError = false

    fun loadData(realSteps: Float?) {
        val steps = preferenceRepository.fakePaceCheckerSteps.takeIf { it > 0 } ?: realSteps
        view?.showLoading()

        loadUsefulScreenContent()
        loadExploreScreenContent()

        if (steps == null) {
            view?.showData(errorContent)
            isError = true
            return
        }

        val content = when {
            steps >= 100 -> {
                firebaseAnalyticsHelper.paceCheckerCompleted("brisk")
                briskContent
            }

            steps >= 90 -> {
                firebaseAnalyticsHelper.paceCheckerCompleted("slightly_under")
                slightlyUnderBriskContent
            }

            steps < 90 && steps > 10 -> {
                firebaseAnalyticsHelper.paceCheckerCompleted("under")
                underBriskContent
            }

            else -> {
                firebaseAnalyticsHelper.paceCheckerCompleted("no data")
                errorContent
            }
        }

        isError = content.isError

        view?.showData(
            content.copy(
                cadence = content.cadence.ifBlank { steps.roundToInt().toString() })
        )
    }

    private fun loadUsefulScreenContent() {
        screenRepository.getScreenContentBySlug(ScreenRepository.PACE_CHECKER_WAS_THIS_USEFUL)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { usefulScreenContent = it },
                onError = { view?.showAlert(it) }
            )
            .addToDisposables()
    }

    private fun loadExploreScreenContent() {
        screenRepository.getScreenContentBySlug(ScreenRepository.PACE_CHECKER_MORE_TO_EXPLORE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { view?.hideLoading() }
            .subscribeBy(
                onSuccess = { exploreScreenContent = it },
                onError = { view?.showAlert(it) }
            )
            .addToDisposables()
    }

    fun onCloseClicked() {
        if (isError) {
            view?.closePaceChecker()
        } else {
            view?.showUsefulDialog(usefulScreenContent)
        }
    }

    fun onAgainClicked() {
        view?.popViewToTimer()
    }

    fun onUsefulDialogClickedNo() {
        view?.showExploreDialog(exploreScreenContent)
    }
}
