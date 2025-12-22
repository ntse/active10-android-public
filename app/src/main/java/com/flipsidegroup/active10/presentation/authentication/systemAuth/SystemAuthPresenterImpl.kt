package com.flipsidegroup.active10.presentation.authentication.systemAuth

import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.usecases.RemoveNhsUserDataUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class SystemAuthPresenterImpl @Inject constructor(
    private val loginRepository: LoginRepository,
    private val removeNhsUserDataUseCase: RemoveNhsUserDataUseCase,
) : BasePresenter<SystemAuthView>(), SystemAuthPresenter {

    override fun logout() {
        view?.showLoading()

        loginRepository.logout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("User logged out")
                removeNhsUserDataUseCase()
                view?.hideLoading()
                view?.onSuccessLogout()
            }, { error ->
                Timber.e("Error logging out, but we still log out user from the app. $error")
                removeNhsUserDataUseCase()
                view?.hideLoading()
                view?.onSuccessLogout()
            }).addToDisposables()
    }

}