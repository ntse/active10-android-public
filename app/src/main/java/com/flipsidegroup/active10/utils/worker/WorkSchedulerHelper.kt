package com.flipsidegroup.active10.utils.worker

import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.flipsidegroup.active10.data.LocalNotificationInfo
import com.flipsidegroup.active10.data.Notifications
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository.Companion.LOCAL_NOTIFICATION_JSON_EXTRA_KEY
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.UIUtils
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Calendar
import java.util.concurrent.TimeUnit

private const val NOTIFICATION_HOUR = 19
const val NOTIFICATION_LAPSED_REPEAT_INTERVAL = 15L
private const val NOTIFICATION_LAPSED_REPEAT_INTERVAL_TEST = 1L
private const val NOTIFICATION_LAPSED_DELAY_TEST = 1L
private const val NOTIFICATION_ONBOARDING_DELAY_TEST = 30L

object WorkSchedulerHelper {

    const val LOCAL_NOTIFICATION_WORKER_TAG = "LocalNotificationWorker"

    fun scheduleOnboardingNotification(timeDelay: Int, isNotificationMin: Boolean) {
        val onboardingTimeDelay = getOnboardingDelayTime(isNotificationMin, timeDelay)
        val delayTimeUnit = if (isNotificationMin) {
            TimeUnit.SECONDS
        } else {
            TimeUnit.MINUTES
        }
        val onboardingWorker = OneTimeWorkRequest.Builder(
            OnboardingWorker::class.java
        ).setInitialDelay(
            onboardingTimeDelay,
            delayTimeUnit
        ).build()
        WorkManager.getInstance(UIUtils.getAppContext())
            .enqueueUniqueWork(
                OnboardingWorker::class.java.name,
                ExistingWorkPolicy.REPLACE,
                onboardingWorker
            )
    }

    fun startLapsedNotificationIfNeeded(
        localRepository: LocalRepository,
        settingsUtils: SettingsUtils,
        isNotificationMin: Boolean
    ) {
        val notificationIndex = settingsUtils.getSettingsHolder().nextOnboardingNotification ?: 0
        var isLastNotification: Boolean

        localRepository.getNotifications(object :
            AppDatabase.OnDataLoadedListener<Notifications?> {
            override fun onDataLoaded(data: Notifications?) {
                isLastNotification = if (data?.onboardingNotifications != null) {
                    notificationIndex == data.onboardingNotifications.size - 1
                } else {
                    true
                }

                if (isLastNotification) {
                    scheduleLapsedNotification(isNotificationMin)
                }
            }
        })
    }

    fun scheduleLapsedNotification(isNotificationMin: Boolean) {
        val lapsedTimeDelay = getLapsedDelayTime(isNotificationMin)
        val repeatInterval = getLapsedRepeatInterval(isNotificationMin)
        val repeatIntervalTimeUnit = getLapsedRepeatIntervalTimeUnit(isNotificationMin)

        val lapsedWorker = PeriodicWorkRequest.Builder(
            LapsedWorker::class.java,
            repeatInterval,
            repeatIntervalTimeUnit
        ).setInitialDelay(
            lapsedTimeDelay,
            TimeUnit.MINUTES
        ).build()
        WorkManager.getInstance(UIUtils.getAppContext())
            .enqueueUniquePeriodicWork(
                LapsedWorker::class.java.name,
                ExistingPeriodicWorkPolicy.REPLACE,
                lapsedWorker
            )
    }

    fun scheduleLocalNotification(notification: LocalNotificationInfo) {
        val data = Data.Builder()
            .putString(LOCAL_NOTIFICATION_JSON_EXTRA_KEY, Gson().toJson(notification))

        val dueDate = notification.timestamp.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli()
        val currentDate = LocalDateTime.now().atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli()

        val timeDiff = dueDate.minus(currentDate)

        val dailyWorkRequest = OneTimeWorkRequest.Builder(
            LocalNotificationWorker::class.java
        ).setInputData(data.build())
            .setInitialDelay(if (timeDiff > 0) timeDiff else 0, TimeUnit.MILLISECONDS)
            .addTag(LOCAL_NOTIFICATION_WORKER_TAG)
            .build()

        WorkManager
            .getInstance(UIUtils.getAppContext())
            .enqueueUniqueWork(
                notification.slug,
                ExistingWorkPolicy.REPLACE,
                dailyWorkRequest
            )
    }

    private fun getOnboardingDelayTime(isNotificationMin: Boolean, timeDelay: Int): Long {
        return if (isNotificationMin) {
            NOTIFICATION_ONBOARDING_DELAY_TEST
        } else {
            val notifDate = Calendar.getInstance()
            notifDate.set(Calendar.MINUTE, 0)
            notifDate.add(Calendar.DATE, timeDelay)
            notifDate.set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR)
            DateHelper.getDateDiffInMinutes(notifDate.time.time)
        }
    }

    private fun getLapsedDelayTime(isNotificationMin: Boolean): Long {
        return if (isNotificationMin) {
            NOTIFICATION_LAPSED_DELAY_TEST
        } else {
            val notifDate = Calendar.getInstance()
            notifDate.set(Calendar.MINUTE, 0)
            if (notifDate.get(Calendar.HOUR_OF_DAY) < NOTIFICATION_HOUR) {
                notifDate.set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR)
            } else {
                notifDate.add(Calendar.DATE, NOTIFICATION_LAPSED_REPEAT_INTERVAL.toInt())
                notifDate.set(Calendar.HOUR_OF_DAY, NOTIFICATION_HOUR)
            }
            return DateHelper.getDateDiffInMinutes(notifDate.time.time)
        }
    }

    private fun getLapsedRepeatInterval(isNotificationMin: Boolean): Long {
        return if (isNotificationMin) {
            NOTIFICATION_LAPSED_REPEAT_INTERVAL_TEST
        } else {
            NOTIFICATION_LAPSED_REPEAT_INTERVAL
        }
    }

    private fun getLapsedRepeatIntervalTimeUnit(isNotificationMin: Boolean): TimeUnit {
        return if (isNotificationMin) {
            TimeUnit.MINUTES
        } else {
            TimeUnit.DAYS
        }
    }
}