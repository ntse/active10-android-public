package com.flipsidegroup.active10.presentation.onboarding.presenter

import android.os.Bundle
import com.flipsidegroup.active10.data.Onboarding
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.onboarding.view.PermissionView
import com.flipsidegroup.active10.utils.*
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class PermissionPresenterImpl @Inject constructor(
    private val settingsUtils: SettingsUtils,
    private val localRepository: LocalRepository,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val preferenceRepository: PreferenceRepository,
    private val loginRepository: LoginRepository
) : BasePresenter<PermissionView>(), PermissionPresenter {

    override fun getOnboarding() {
        localRepository.getOnboarding(object : AppDatabase.OnDataLoadedListener<Onboarding?> {
            override fun onDataLoaded(data: Onboarding?) {
                view?.onOnboardingReceived(data)
            }
        })
    }

    override fun sendGoals() {
        val selectedGoalsList =
            settingsUtils.getSettingsHolder().goalsList?.filter { it.isSelected }
        if (selectedGoalsList.isNullOrEmpty()) {
            view?.onSendGoalsCompleted()
            return
        }

        view?.showLoading()

        sendFirebaseGoalsEvent(selectedGoalsList)

        if (preferenceRepository.isUserLoggedIn) {
            loginRepository.postMotivations(selectedGoalsList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    view?.hideLoading()
                    view?.onSendGoalsCompleted()
                }
                .subscribe(
                    {
                        Timber.d("Post latest goals/motivations, success")
                    },
                    {
                        Timber.d("Post latest goals/motivations, error", it)
                    }
                ).addToDisposables()
        } else {
            view?.hideLoading()
            view?.onSendGoalsCompleted()
        }
    }

    private fun sendFirebaseGoalsEvent(goals: List<Goal>) {
        val shortenGoals: MutableList<Goal> = mutableListOf()
        val customGoals: MutableList<Goal> = mutableListOf()
        val bundle = Bundle()

        goals.forEach { currentGoal ->
            if (!currentGoal.isCustomGoal) {
                shortenGoals.add(currentGoal)
            } else {
                customGoals.add(currentGoal)
            }
        }
        if (shortenGoals.isNotEmpty()) {
            bundle.putString(
                Constants.FirebaseAnalytics.DEVICE_ID,
                DeviceUtils.getDeviceId(settingsUtils)
            )
            bundle.putString(
                Constants.FirebaseAnalytics.KEY_GOALS,
                shortenGoals.map { it.goalId }.joinToString(",")
            )
            bundle.putString(
                Constants.FirebaseAnalytics.KEY_CREATED_AT,
                DateHelper.formatStepDataTimestamp(System.currentTimeMillis())
            )
            firebaseAnalyticsHelper.sendFirebaseEvent(
                Constants.FirebaseAnalytics.EVENT_USER_GOAL,
                bundle
            )
        }
        if (customGoals.isNotEmpty()) {
            customGoals.forEach {
                bundle.putString(
                    Constants.FirebaseAnalytics.DEVICE_ID,
                    DeviceUtils.getDeviceId(settingsUtils)
                )
                bundle.putString(Constants.FirebaseAnalytics.KEY_GOAL, it.goal)
                bundle.putString(
                    Constants.FirebaseAnalytics.KEY_CREATED_AT,
                    DateHelper.formatStepDataTimestamp(System.currentTimeMillis())
                )
                firebaseAnalyticsHelper.sendFirebaseEvent(
                    Constants.FirebaseAnalytics.CUSTOM_GOAL,
                    bundle
                )
            }
        }
    }
}