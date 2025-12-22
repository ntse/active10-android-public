package com.flipsidegroup.active10.presentation.targets.presenter

import android.os.Bundle
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.models.dataholders.TargetHolder
import com.flipsidegroup.active10.data.models.requests.UserTargetRequest
import com.flipsidegroup.active10.data.persistance.newapi.NhsSyncRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.targets.activities.DEFAULT_TARGET
import com.flipsidegroup.active10.presentation.targets.view.SetTargetView
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.DEVICE_ID
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.EVENT_DAILY_TARGET
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_CREATED_AT
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_TARGET
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.DeviceUtils
import com.flipsidegroup.active10.utils.EarnBadgeHelper
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import javax.inject.Inject


class SetTargetPresenterImpl @Inject constructor(
    private val settingsUtils: SettingsUtils,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val preferenceRepository: PreferenceRepository,
    private val nhsSyncRepository: NhsSyncRepository,
) : BasePresenter<SetTargetView>(),
    SetTargetPresenter {


    private val initialTarget: Int? = settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target
    private var selectedTarget: Int =
        settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: DEFAULT_TARGET

    override fun onSelectTarget(target: Int) {
        selectedTarget = target
    }

    override fun onContinue() {
        if (initialTarget == selectedTarget) view?.onSendTargetCompleted()

        val userTargetRequest = UserTargetRequest(
            target = selectedTarget,
            createdAt = DateHelper.formatStepDataTimestamp(DateHelper.getCurrentTimestamp()),
            deviceId = DeviceUtils.getDeviceId(settingsUtils)
        )

        view?.showLoading()

        view?.hideLoading()
        updateSettings(userTargetRequest)
        sendFirebaseDailyTargetEvent(userTargetRequest)
        view?.onSendTargetCompleted()
    }

    private fun updateSettings(targetRequest: UserTargetRequest) {
        val savedTargets = settingsUtils.getSettingsHolder().targetList ?: ArrayList()
        savedTargets.add(TargetHolder(targetRequest.target, DateHelper.getCurrentTimestamp()))
        settingsUtils.updateSettings(SettingsDataHolder(targetList = savedTargets))
        settingsUtils.updateSettings(SettingsDataHolder(showSetTarget = false))
        if (settingsUtils.getSettingsHolder().earnedBadges?.find { it.id == RewardBadgeEnum.HIGH_ACHIEVER.id } != null
            && (initialTarget ?: DEFAULT_TARGET) < targetRequest.target
            && targetRequest.target > 3) {
            EarnBadgeHelper.saveEarnedBadge(
                settingsUtils = settingsUtils,
                badge = RewardBadgeEnum.AIMING_HIGH,
                preferenceRepository = preferenceRepository
            )
        }
        nhsSyncRepository.postDailyTarget(DateTime.now(DateTimeZone.UTC).millis, targetRequest.target)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun sendFirebaseDailyTargetEvent(targetRequest: UserTargetRequest) {
        val bundle = Bundle()
        bundle.putString(DEVICE_ID, DeviceUtils.getDeviceId(settingsUtils))
        bundle.putString(KEY_CREATED_AT, targetRequest.createdAt)
        bundle.putInt(KEY_TARGET, targetRequest.target)

        firebaseAnalyticsHelper.sendFirebaseEvent(EVENT_DAILY_TARGET, bundle)
    }
}
