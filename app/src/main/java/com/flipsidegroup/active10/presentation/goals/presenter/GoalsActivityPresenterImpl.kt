package com.flipsidegroup.active10.presentation.goals.presenter

import android.os.Bundle
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.goals.view.GoalsActivityView
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.CUSTOM_GOAL
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.DEVICE_ID
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.EVENT_USER_GOAL
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_CREATED_AT
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_GOAL
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_GOALS
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.DeviceUtils
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.shortenLogs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class GoalsActivityPresenterImpl @Inject constructor(
    private val settingsUtils: SettingsUtils,
    private val loginRepository: LoginRepository,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val preferenceRepository: PreferenceRepository,
) : BasePresenter<GoalsActivityView>(),
    GoalsActivityPresenter {

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
            bundle.putString(DEVICE_ID, DeviceUtils.getDeviceId(settingsUtils))
            bundle.remove(KEY_GOAL)
            bundle.putString(KEY_GOALS, shortenGoals.map { it.goalId }.joinToString(","))
            bundle.putString(
                KEY_CREATED_AT,
                DateHelper.formatStepDataTimestamp(System.currentTimeMillis())
            )
            firebaseAnalyticsHelper.sendFirebaseEvent(EVENT_USER_GOAL, bundle)
        }
        if (customGoals.isNotEmpty()) {
            customGoals.forEach {
                bundle.putString(DEVICE_ID, DeviceUtils.getDeviceId(settingsUtils))
                bundle.putString(KEY_GOAL, it.goal)
                bundle.remove(KEY_GOALS)
                bundle.putString(
                    KEY_CREATED_AT,
                    DateHelper.formatStepDataTimestamp(System.currentTimeMillis())
                )
                firebaseAnalyticsHelper.sendFirebaseEvent(CUSTOM_GOAL, bundle)
            }
        }
    }

    override fun saveGoals(goals: List<Goal>) {
        view?.showLoading()

        if (goals.isEmpty()) return
        settingsUtils.updateSettings(SettingsDataHolder(goalsList = goals))

        val selectedGoals = goals.filter { it.isSelected }

        sendFirebaseGoalsEvent(selectedGoals)

        if (preferenceRepository.isUserLoggedIn) {
            loginRepository.postMotivations(selectedGoals)
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
}