package com.flipsidegroup.active10.presentation.nhsuserdetails

import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
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

class NhsUserDetailsPresenterImpl @Inject constructor(
    private val loginRepository: LoginRepository,
    private val screenRepository: ScreenRepository,
    private val settingsUtils: SettingsUtils,
) : BasePresenter<NhsUserDetailsView>(), NhsUserDetailsPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun getUpdateDialogContent() {
        presenterScope.launch {
            runCatching {
                screenRepository
                    .getScreenContentBySlug(ScreenRepository.LOGIN_UPDATE_DETAILS)
                    .await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess {
                    view?.onUpdateDialogContentReceived(it)
                }
        }
    }

    override fun loadContent() {
        presenterScope.launch {
            runCatching {
                screenRepository
                    .getScreenContentBySlug(ScreenRepository.LOGIN_MY_DETAILS)
                    .await()
            }
                .onFailure {
                    Timber.e(it)
                    view?.showAlert(it)
                }
                .onSuccess { view?.showContent(it) }
        }
    }

    override fun loadData() {
        presenterScope.launch {
            runCatching {
                settingsUtils.getSettingsHolder().nhsUser
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { user ->
                    view?.showData(user)
                }
        }
    }

    override fun updateData() {
        loginRepository.getUserDetails()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.isPresent) {
                        view?.showData(it.get().toEntity())
                        Timber.d("Updated user details are displayed")
                    } else {
                        Timber.e("No user details found")
                    }
                },
                {
                    Timber.e(it)
                    view?.showAlert(it)
                }
            ).addToDisposables()
    }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}