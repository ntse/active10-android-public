package com.flipsidegroup.active10.utils.worker

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.flipsidegroup.active10.Active10App
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.Notifications
import com.flipsidegroup.active10.data.OnboardingNotifications
import com.flipsidegroup.active10.data.PeriodTypeEnum
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.home.adapters.MY_WALK_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.tips.activities.TipsIntent
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.NotificationHelper
import com.flipsidegroup.active10.utils.UIUtils
import java.util.*
import javax.inject.Inject

private const val ACTION_SHOW_TIPS = "showTips"
private const val ACTION_SHOW_MY_WALKS_DAYS = "showMyWalksDays"

class OnboardingWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var localRepository: LocalRepository

    init {
        Active10App.appComponent.inject(this)
    }

    override fun doWork(): Result {
        val notificationIndex = settingsUtils.getSettingsHolder().nextOnboardingNotification ?: 0
        var isLastNotification: Boolean

        var onboardingNotification: OnboardingNotifications?
        var nextOnboardingNotification: OnboardingNotifications? = null

        localRepository.getNotifications(object :
            AppDatabase.OnDataLoadedListener<Notifications?> {
            override fun onDataLoaded(data: Notifications?) {
                isLastNotification = if (data?.onboardingNotifications != null) {
                    notificationIndex >= data.onboardingNotifications.size - 1
                } else {
                    true
                }

                if (data != null && !isLastNotification) {
                    nextOnboardingNotification =
                        data.onboardingNotifications[notificationIndex + 1]
                }

                onboardingNotification =
                    if (notificationIndex + 1 > data?.onboardingNotifications?.size ?: 0) null
                    else data?.onboardingNotifications?.get(notificationIndex)
                onboardingNotification?.let {
                    val isNotificationMin =
                        settingsUtils.getSettingsHolder().notificationMin ?: false
                    if (shouldShowNotification(
                            isNotificationMin,
                            it.userInfo?.notSameDay == null
                        )
                    ) {
                        showNotification(it)
                    }
                    scheduleNextNotification(
                        it,
                        nextOnboardingNotification,
                        isNotificationMin,
                        isLastNotification,
                        notificationIndex
                    )
                }
            }
        })

        return Result.success()
    }

    private fun shouldShowNotification(
        isNotificationMin: Boolean,
        shouldCheckToday: Boolean
    ): Boolean {
        val lastTimestampInApp = settingsUtils.getSettingsHolder().lastTimeInApp ?: return true
        val nextLapsedCalendar = Calendar.getInstance()
        nextLapsedCalendar.timeInMillis = lastTimestampInApp
        nextLapsedCalendar.add(Calendar.DATE, NOTIFICATION_LAPSED_REPEAT_INTERVAL.toInt())

        val wasTodayInApp = shouldCheckToday && DateHelper.isSameDay(lastTimestampInApp)
        return !DateHelper.isSameDay(nextLapsedCalendar) || !wasTodayInApp || isNotificationMin
    }

    private fun scheduleNextNotification(
        notification: OnboardingNotifications,
        nextNotification: OnboardingNotifications?,
        isNotificationMin: Boolean,
        isLastNotification: Boolean,
        notificationIndex: Int
    ) {
        if (!isLastNotification) {
            nextNotification?.let {
                val timeDiff = it.day - notification.day
                WorkSchedulerHelper.scheduleOnboardingNotification(timeDiff, isNotificationMin)

                settingsUtils.updateSettings(SettingsDataHolder(nextOnboardingNotification = notificationIndex + 1))
            }

        } else {
            WorkSchedulerHelper.scheduleLapsedNotification(isNotificationMin)
        }
    }

    private fun showNotification(notification: OnboardingNotifications) {
        val message = notification.copy
        NotificationHelper.showNotification(
            UIUtils.getString(R.string.notification_title),
            message,
            getNotificationLink(notification.userInfo?.action!!, notification.day)
        )
    }

    private fun getNotificationLink(action: String, day: Int): Intent {
        val event = UIUtils.getString(R.string.onboarding_notification_tapped_event, day)

        return when (action) {
            ACTION_SHOW_TIPS -> UIUtils.getAppContext().TipsIntent(false, event)
            ACTION_SHOW_MY_WALKS_DAYS ->
                HomeActivity.getHomeIntent(
                    UIUtils.getAppContext(),
                    null,
                    screenPosition = MY_WALK_SCREEN_POSITION,
                    myWalkType = PeriodTypeEnum.DAYS,
                    event = event
                )
            else -> HomeActivity.getHomeIntent(
                UIUtils.getAppContext(),
                null,
                screenPosition = MY_WALK_SCREEN_POSITION,
                myWalkType = PeriodTypeEnum.WEEKS,
                event = event
            )
        }
    }
}
