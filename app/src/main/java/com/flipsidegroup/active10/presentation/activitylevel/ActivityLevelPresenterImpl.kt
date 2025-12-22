package com.flipsidegroup.active10.presentation.activitylevel

import com.flipsidegroup.active10.data.ActivityLevelEnum
import com.flipsidegroup.active10.data.models.ClassicUser
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
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
import javax.inject.Inject

class ActivityLevelPresenterImpl @Inject constructor(
    private val screenRepository: ScreenRepository,
    private val settingsUtils: SettingsUtils,
    private val loginRepository: LoginRepository,
    private val preferenceRepository: PreferenceRepository,
) : BasePresenter<ActivityLevelView>(), ActivityLevelPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun loadContent() {
        presenterScope.launch {
            runCatching {
                screenRepository
                    .getScreenContentBySlug("about_you")
                    .await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { view?.showContent(it) }
        }
    }

    override fun loadData() {
        presenterScope.launch {
            runCatching {
                settingsUtils.getSettingsHolder().classicUser
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess { view?.showData(it) }
        }
    }

    override fun saveData(activityLevel: ActivityLevelEnum?) {
        if (activityLevel == null) return

        view?.showLoading()

        presenterScope.launch {
            runCatching {
                settingsUtils.updateSettings(
                    SettingsDataHolder(
                        classicUser = ClassicUser(
                            activityLevel = activityLevel.value
                        )
                    )
                )
                if (preferenceRepository.isUserLoggedIn) {
                    loginRepository.postActivityLevel(activityLevel)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .await()
                }
            }
                .onFailure {
                    view?.hideLoading()
                    view?.showAlert(it)
                }
                .onSuccess {
                    view?.hideLoading()
                    view?.onBack()
                }

        }
    }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}