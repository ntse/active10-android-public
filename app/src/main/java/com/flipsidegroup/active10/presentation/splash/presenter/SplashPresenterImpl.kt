package com.flipsidegroup.active10.presentation.splash.presenter

import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.usecases.RemoveNhsUserDataUseCase
import com.flipsidegroup.active10.utils.UIUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SplashPresenterImpl constructor(
    private val settingsUtils: SettingsUtils,
    private val localRepository: LocalRepository,
    private val preferenceRepository: PreferenceRepository,
    private val localNotificationRepository: LocalNotificationRepository,
    private val loginRepository: LoginRepository,
    private val removeNhsUserDataUseCase: RemoveNhsUserDataUseCase,
) : BasePresenter<BaseView>(), SplashPresenter {



    override fun registerDevice() {
        val isDeviceRegistered = settingsUtils.getSettingsHolder().isDeviceRegistered
        if (isDeviceRegistered == true) {
            Timber.d("Device is already registered")
            return
        }

        settingsUtils.updateSettings(SettingsDataHolder(isDeviceRegistered = true))
    }

    override fun checkForGoalsBackwardsCompatibility() {
        val goalsList = settingsUtils.getSettingsHolder().goalsList

        if (goalsList.isNullOrEmpty()) {
            return
        }

        val isHandlingRequired = !goalsList.any { it.isSelected }
        if (isHandlingRequired) {
            localRepository.getGoalsContent(object : AppDatabase.OnDataLoadedListener<List<Goal>> {
                override fun onDataLoaded(data: List<Goal>) {
                    val apiData = data.toMutableList()
                    val cachedData = settingsUtils.getSettingsHolder().goalsList?.toMutableList() ?: mutableListOf()
                    val goalReason = settingsUtils.getSettingsHolder().goalReason ?: ""

                    if (goalReason.isNotEmpty()) {
                        apiData.firstOrNull { it.goal == UIUtils.getString(R.string.goal_item_6) }?.goal = goalReason
                        cachedData.firstOrNull { it.goal == UIUtils.getString(R.string.goal_item_6) }?.goal = goalReason
                    } else {
                        apiData.removeAll { it.goal == UIUtils.getString(R.string.goal_item_6) }
                        cachedData.removeAll { it.goal == UIUtils.getString(R.string.goal_item_6) }
                    }

                    apiData.forEach { goal ->
                        if (cachedData.firstOrNull { it.goal == goal.goal } != null) {
                            goal.isSelected = true

                            if (goal.goal == goalReason) {
                                goal.isCustomGoal = true
                            }
                        }
                    }

                    apiData.sortBy { it.isCustomGoal }

                    settingsUtils.updateSettings(SettingsDataHolder(goalReason = null))
                    settingsUtils.updateSettings(SettingsDataHolder(goalsList = apiData))
                }
            })
        }
    }

    override fun checkNhsLoginStatus() {
        val nhsToken = settingsUtils.getSettingsHolder().nhsToken
        if (nhsToken.isNullOrBlank()) {
            Timber.d("Check NHS login status: Not logged in")
            if (preferenceRepository.isUserLoggedIn) {
                preferenceRepository.isUserLoggedIn = false
                preferenceRepository.showLogoutPopupAttemptsCount = 3
            }
            return
        }
        if (!preferenceRepository.isOnboardingCompleted) {
            Timber.d("Check NHS login status: Onboarding not completed - logging out")
            removeNhsUserDataUseCase()
            return
        }
        loginRepository.getUserDetails()
            .subscribeOn(Schedulers.io())
            .subscribe(
                { userDetails ->
                    userDetails.ifPresent {
                        Timber.d("Check NHS login status: Logged in")
                        settingsUtils.updateSettings(SettingsDataHolder(nhsUser = it.toEntity()))
                        preferenceRepository.isUserLoggedIn = true
                        localNotificationRepository.cancelNoAccountUser()
                        checkMWPCompletedNotification()
                    }
                    if (userDetails.isEmpty) {
                        Timber.d("Check NHS login status: Cannot load user - logging out")
                        removeNhsUserDataUseCase()
                    }
                },
                {
                    Timber.e(it, "Check NHS login status: Error checking user data - logging out")
                    removeNhsUserDataUseCase()
                }
            ).addToDisposables()
    }

    private fun checkMWPCompletedNotification() {
        if (preferenceRepository.isUserLoggedIn && preferenceRepository.showMWPCompletedNotification) {
            localNotificationRepository.setUserCompletedPlan()
            preferenceRepository.showMWPCompletedNotification = false
        }
    }
}