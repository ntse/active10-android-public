package com.flipsidegroup.active10.presentation.classicuserdetails

import com.flipsidegroup.active10.data.models.ClassicUser
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class ClassicUserDetailsPresenterImpl @Inject constructor(
    private val screenRepository: ScreenRepository,
    private val settingsUtils: SettingsUtils,
) : BasePresenter<ClassicUserDetailsView>(), ClassicUserDetailsPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun loadContent() {
        presenterScope.launch {
            runCatching {
                screenRepository
                    .getScreenContentBySlug(ScreenRepository.LOGIN_MY_DETAILS)
                    .await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { view?.showContent(it) }
        }
    }

    override fun loadData() {
        presenterScope.launch {
            runCatching {
                //TODO get info about logged in/out User
                settingsUtils.getSettingsHolder().classicUser
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { view?.showData(it) }
        }
    }

    override fun saveData(user: ClassicUser) {
        presenterScope.launch {
            runCatching {
                //TODO get info about logged in/out User
                settingsUtils.updateSettings(
                    SettingsDataHolder(
                        classicUser = ClassicUser(
                            gender = user.gender,
                            age = user.age,
                            activityLevel = user.activityLevel
                        )
                    )
                )
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { view?.onUserDetailsSaved() }
        }
    }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}