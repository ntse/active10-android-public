package com.flipsidegroup.active10.presentation.progressBar

import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.NhsSyncRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ProgressBarPresenterImpl(
    private val settingsUtils: SettingsUtils,
    private val nhsSyncRepository: NhsSyncRepository,
    private val preferenceRepository: PreferenceRepository,
    private val localNotificationRepository: LocalNotificationRepository,
    private val screenRepository: ScreenRepository,
) : BasePresenter<ProgressBarView>(), ProgressBarPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun loadContent() {
        presenterScope.launch {
            val screenContent = screenRepository
                .getScreenContentBySlug(ScreenRepository.LOGIN_CREATING_ACCOUNT)
                .await()
            view?.showContent(screenContent)
        }
    }

    override fun startSynchronization() {
    val syncObservable = nhsSyncRepository.syncAllWithProgress(getUserId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext { progress -> view?.showProgress((progress * 100).toInt()) }
        .doOnError { Timber.e(it) }
        .ignoreElements()

    val delayCompletable = Completable.timer(2, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())

    Completable.mergeArray(syncObservable, delayCompletable)
        .subscribe(
            {
                preferenceRepository.lastWeekWithShownHitPopup = -1
                preferenceRepository.lastWeekWithShownExceededPopup = -1
                preferenceRepository.lastWeekWithShownMissedPopup = -1
                preferenceRepository.isUserLoggedIn = true
                localNotificationRepository.setNotClickedOnMWP()
                localNotificationRepository.setNotClickedOnWalksNear()
                view?.navigateToNextScreen()
            },
            { Timber.e(it) }
        ).addToDisposables()
}

    private fun getUserId(): String {
        return settingsUtils.getSettingsHolder().nhsUser?.id
            ?: throw IllegalStateException("User not found")
    }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}