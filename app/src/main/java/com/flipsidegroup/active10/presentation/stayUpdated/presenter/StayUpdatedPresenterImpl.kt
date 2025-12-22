package com.flipsidegroup.active10.presentation.stayUpdated.presenter

import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.stayUpdated.StayUpdatedView
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
import javax.inject.Inject

class StayUpdatedPresenterImpl @Inject constructor (
    private val screenRepository: ScreenRepository,
    private val settingsUtils: SettingsUtils,
    private val localNotificationRepository: LocalNotificationRepository,
    private val loginRepository: LoginRepository,
) : BasePresenter<StayUpdatedView>(), StayUpdatedPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun loadData() {
        presenterScope.launch {
            runCatching {
                screenRepository
                    .getScreenContentBySlug(ScreenRepository.LOGIN_STAY_UPDATED)
                    .await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { view?.showData(it) }
        }
    }

    override fun saveData(isAllowReceiveEmail: Boolean) {

        view?.showLoading()

        val postRequest = if (isAllowReceiveEmail) {
            loginRepository.postSubscribeEmailPref()
        } else {
            loginRepository.postUnsubscribeEmailPref()
        }

        settingsUtils.getSettingsHolder().nhsUser!!.copy(
            isEmailUpdatesAllowed = isAllowReceiveEmail
        ).also {
            settingsUtils.updateSettings(SettingsDataHolder(nhsUser = it))
        }

        postRequest
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { view?.hideLoading() }
            .subscribe(
                {
                    Timber.d("Receive email: $isAllowReceiveEmail, success")
                },
                {
                    Timber.d("Receive email: $isAllowReceiveEmail, error", it)
                }
            ).addToDisposables()
    }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}