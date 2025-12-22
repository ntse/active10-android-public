package com.flipsidegroup.active10.utils.worker

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.flipsidegroup.active10.Active10App
import com.flipsidegroup.active10.data.LocalNotificationInfo
import com.flipsidegroup.active10.data.LocalNotificationNavInfo
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository.Companion.LOCAL_NOTIFICATION_JSON_EXTRA_KEY
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.home.activities.LOCAL_NOTIFICATION_NAV_INFO
import com.flipsidegroup.active10.presentation.home.activities.LOCAL_NOTIFICATION_TITLE_FOR_EVENT
import com.flipsidegroup.active10.presentation.splash.SplashActivity
import com.flipsidegroup.active10.utils.NotificationHelper
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.google.gson.Gson
import timber.log.Timber
import java.io.IOException
import java.io.InvalidClassException
import javax.inject.Inject

class LocalNotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var localRepository: LocalRepository

    @Inject
    internal lateinit var localNotificationRepository: LocalNotificationRepository

    @Inject
    internal lateinit var preferenceRepository: PreferenceRepository

    @Inject
    internal lateinit var analyticsHelper: FirebaseAnalyticsHelper

    init {
        Active10App.appComponent.inject(this)
    }

    override fun doWork(): Result {
        try {
            val json = inputData.getString(LOCAL_NOTIFICATION_JSON_EXTRA_KEY) ?: return Result.success()
            if (json.isNotEmpty()) {
                Gson().fromJson(json, LocalNotificationInfo::class.java)?.let { local ->
                    showNotification(local)
                }
            }
        } catch (ice: InvalidClassException) {
            Timber.d(ice)
        } catch (ioe: IOException) {
            Timber.d(ioe)
        } catch (e: Throwable) {
            Timber.e(e)
        }
        return Result.success()
    }

    private fun showNotification(data: LocalNotificationInfo) {
        val notificationIntent = Intent(applicationContext, SplashActivity::class.java)
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        notificationIntent.putExtra(LOCAL_NOTIFICATION_TITLE_FOR_EVENT, data.title)
        if (!data.destination.isNullOrBlank()) {
            notificationIntent.putExtra(
                LOCAL_NOTIFICATION_NAV_INFO,
                Gson().toJson(
                    LocalNotificationNavInfo(
                        data.destination,
                        data.planId
                    )
                )
            )
        }

        val userName = settingsUtils.getSettingsHolder().nhsUser?.firstName ?: ""
        val postcode = settingsUtils.getSettingsHolder().nhsUser?.postcode ?: "12 E45"

        val contentTitle = data.title.ifBlank { "Active 10" }
        val contentText = data.description
            .replace("{name}", userName)
            .replace("{postcode}", postcode)

        NotificationHelper.showNotification(
            title = contentTitle,
            message = contentText,
            intent = notificationIntent,
        )

        analyticsHelper.notificationSent(contentTitle)

        if (data.isLapsed && data.intervalInDays > 0 && data.slug.isNotBlank()) {
            localNotificationRepository.setLapsedNotification(
                slug = data.slug,
                time = data.timestamp,
                intervalInDays = data.intervalInDays,
                planId = data.planId,
            )
        }
    }
}