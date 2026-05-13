package com.flipsidegroup.active10.presentation.onboarding.nhs

import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

class NhsLoginWebViewPresenter(
    private val settingsUtils: SettingsUtils,
    private val loginRepository: LoginRepository,
    private val localNotificationRepository: LocalNotificationRepository,
    @Suppress("unused") private val screenRepository: ScreenRepository,
) : BasePresenter<NhsLoginWebViewContract.View>(), NhsLoginWebViewContract.Presenter {

    override fun completeLogin(accessToken: String) {
        settingsUtils.updateSettings(SettingsDataHolder(nhsToken = accessToken))
        getUserDetails()
    }

    private fun getUserDetails() {
        loginRepository.getUserDetails()
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { userDetails ->
                    userDetails.ifPresent {
                        localNotificationRepository.cancelNoAccountUser()
                        view?.loginSuccess()
                    }
                    if (userDetails.isEmpty) {
                        view?.loginFailure()
                    }
                },
                {
                    Timber.e(it)
                    view?.loginFailure()
                }
            ).addToDisposables()
    }

    override fun bind(view: NhsLoginWebViewContract.View) {
        this.view = view
    }

    override fun unbind() {
        this.view = null
    }
}
