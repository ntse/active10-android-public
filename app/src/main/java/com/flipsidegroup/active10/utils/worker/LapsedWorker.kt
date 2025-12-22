package com.flipsidegroup.active10.utils.worker

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.flipsidegroup.active10.Active10App
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.Notifications
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.home.adapters.TODAY_WALK_SCREEN_POSITION
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.NotificationHelper
import com.flipsidegroup.active10.utils.UIUtils
import javax.inject.Inject

class LapsedWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var localRepository: LocalRepository

    init {
        Active10App.appComponent.inject(this)
    }

    override fun doWork(): Result {
        val lastTimeInApp = settingsUtils.getSettingsHolder().lastTimeInApp
        val isNotificationMin = settingsUtils.getSettingsHolder().notificationMin
        lastTimeInApp?.let {
            if (!DateHelper.isSameDay(it) || isNotificationMin != null && isNotificationMin) {
                showNotification()
            }
        }

        return Result.success()
    }

    private fun showNotification() {
        localRepository.getNotifications(object :
            AppDatabase.OnDataLoadedListener<Notifications?> {
            override fun onDataLoaded(data: Notifications?) {
                val lapsedNotifications = data?.lapsedNotifications

                lapsedNotifications?.let {
                    val lapsedCount = settingsUtils.getSettingsHolder().lapsedCount ?: 0
                    val index = if (it.isNotEmpty()) lapsedCount % it.size else 0
                    val notification = it[index]

                    NotificationHelper.showNotification(
                        UIUtils.getString(R.string.notification_title),
                        notification?.copy!!,
                        getNotificationLink(lapsedCount)
                    )

                    settingsUtils.updateSettings(SettingsDataHolder(lapsedCount = lapsedCount + 1))
                }
            }
        })
    }

    private fun getNotificationLink(day: Int): Intent {
        val event = UIUtils.getString(R.string.lapsed_notification_tapped_event, day)
        return HomeActivity.getHomeIntent(
            UIUtils.getAppContext(),
            null,
            TODAY_WALK_SCREEN_POSITION,
            event = event
        )
    }
}
