package com.flipsidegroup.active10.presentation.pacechecker.start

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PaceCheckerStartPresenter @Inject constructor(
    private val screenRepository: ScreenRepository,
    preferenceRepository: PreferenceRepository,
    firebaseAnalyticsHelper: FirebaseAnalyticsHelper
) : BasePresenter<PaceCheckerStartView>() {

    private var flatScreenContent: ScreenContent? = null

    init {
        preferenceRepository.isPaceCheckerOpened = true
        firebaseAnalyticsHelper.paceCheckerOpened()
    }

    fun loadData() {
        view?.showLoading()

        screenRepository.getScreenContentBySlug(ScreenRepository.PACE_CHECKER_FLAT_TERRAIN)
            .doOnSuccess { flatScreenContent = it }
            .flatMap {
                screenRepository.getScreenContentBySlug(ScreenRepository.WELCOME_TO_PACE_CHECKER)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { view?.hideLoading() }
            .subscribeBy(
                onSuccess = { view?.showData(it) },
                onError = { view?.showAlert(it) }
            )
            .addToDisposables()
    }

    fun onContinueClicked() {
        view?.navigateToPaceCheckerFlat(flatScreenContent)
    }
}
