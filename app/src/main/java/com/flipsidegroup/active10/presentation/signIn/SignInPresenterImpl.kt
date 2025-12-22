package com.flipsidegroup.active10.presentation.signIn

import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.usecases.RemoveNhsUserDataUseCase
import com.flipsidegroup.active10.utils.Constants
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

class SignInPresenterImpl @Inject constructor(
    private val screenRepository: ScreenRepository,
    private val loginRepository: LoginRepository,
    private val removeNhsUserDataUseCase: RemoveNhsUserDataUseCase,
) : BasePresenter<SignInView>(), SignInPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun loadData() {
        view?.showLoading()

        presenterScope.launch {
            runCatching {
                val signInMain = screenRepository
                    .getScreenContentBySlug(ScreenRepository.LOGIN_CREATE_ACCOUNT)
                    .await()
                val signInInfo = screenRepository
                    .getScreenContentBySlug(ScreenRepository.LOGIN_HOW_WE_USE)
                    .await()
                signInMain to signInInfo
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { (signInMain, signInInfo) ->
                    val contentMap = mapOf(
                        Constants.SIGN_IN_MAIN_CONTENT to signInMain,
                        Constants.SIGN_IN_INFO_CONTENT to signInInfo
                    )
                    view?.showData(contentMap)
                }

            view?.hideLoading()
        }
    }

    override fun loadTripDialogContent() {
        view?.showLoading()

        presenterScope.launch {
            runCatching {
                val walkingPlans = screenRepository
                    .getScreenContentBySlug(ScreenRepository.LOGIN_NEW_FEATURE_WALKING_PLANS)
                    .await()
                val monthlyReport = screenRepository
                    .getScreenContentBySlug(ScreenRepository.LOGIN_NEW_FEATURE_MONTHLY_REPORT)
                    .await()

                buildList {
                    if (walkingPlans.slug.isNotBlank()) add(walkingPlans)
                    if (monthlyReport.slug.isNotBlank()) add(monthlyReport)
                }
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { contents ->
                    view?.showTripDialog(contents)
                }

            view?.hideLoading()
        }
    }

    override fun logout() {
        view?.showLoading()

        loginRepository.logout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("User logged out")
                removeNhsUserDataUseCase()
                view?.hideLoading()
            }, { error ->
                Timber.e("Error logging out, but we still log out user from the app. $error")
                removeNhsUserDataUseCase()
                view?.hideLoading()
            }).addToDisposables()
    }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}