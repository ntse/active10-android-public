package com.flipsidegroup.active10.data.persistance.newapi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.work.WorkManager
import com.flipsidegroup.active10.data.LocalNotification
import com.flipsidegroup.active10.data.LocalNotificationInfo
import com.flipsidegroup.active10.data.Notifications
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.worker.WorkSchedulerHelper
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject


class LocalNotificationRepository @Inject constructor(
    private val context: Context,
    private val localRepository: LocalRepository,
    private val preferenceRepository: PreferenceRepository,
    private val settingsUtils: SettingsUtils,
) {

    fun setNoAccountUser() {
        val time = LocalDateTime.now()
            .plusDays(3)
            .withHour(14)
            .withMinute(0)
            .withSecond(0)

        setNotification(
            NO_ACCOUNT_USER_SLUG,
            time,
        )

        preferenceRepository.isUserNotSelectedPlanNotified = false
    }

    fun cancelNoAccountUser() {
        cancelNotification(NO_ACCOUNT_USER_SLUG)
    }

    fun setNotClickedOnMWP() {
        val time = LocalDateTime.now()
            .plusDays(3)
            .withHour(14)
            .withMinute(0)
            .withSecond(0)

        setNotification(
            EXTRA_FEATURES_AVAILABLE_SLUG,
            time,
            intervalInDays = 28,
        )
    }

    fun cancelNotClickedOnMWP() {
        cancelNotification(EXTRA_FEATURES_AVAILABLE_SLUG)
    }

    fun setNotSelectedMWP() {
        val time = LocalDateTime.now()
            .plusDays(1)
            .withHour(8)
            .withMinute(0)
            .withSecond(0)

        setNotification(
            MWP_NOT_SELECTED_SLUG,
            time,
        )
    }

    fun cancelNotSelectedMWP() {
        cancelNotification(MWP_NOT_SELECTED_SLUG)
    }

    fun setUserPausedPlan(planId: Long) {
        val time = LocalDateTime.now()
            .plusDays(2)
            .withHour(8)
            .withMinute(0)
            .withSecond(0)

        cancelNotification(MWP_CHECK_PROGRESS_SLUG)

        setNotification(
            MWP_PAUSED_SLUG,
            time,
            planId = planId,
        )
    }

    fun cancelUserPausedPlan() {
        cancelNotification(MWP_PAUSED_SLUG)
    }

    fun setUserNoWalkingActivePlan(planId: Long) {
        val time = LocalDateTime.now()
            .plusDays(2)
            .withHour(8)
            .withMinute(0)
            .withSecond(0)

        cancelNotification(MWP_PAUSED_SLUG)

        setNotification(
            MWP_CHECK_PROGRESS_SLUG,
            time,
            planId = planId,
        )
    }

    fun cancelUserNoWalkingActivePlan() {
        cancelNotification(MWP_CHECK_PROGRESS_SLUG)
    }

    fun setUserCompletedPlan() {
        val time = LocalDateTime.now()

        cancelNotification(MWP_PAUSED_SLUG)
        cancelNotification(MWP_CHECK_PROGRESS_SLUG)

        setNotification(
            MWP_COMPLETED_SLUG,
            time,
        )
    }

    fun cancelUserCompletedPlan() {
        cancelNotification(MWP_COMPLETED_SLUG)
    }

    fun setNotClickedOnWalksNear() {
        val time = LocalDateTime.now()
            .plusDays(5)
            .withHour(8)
            .withMinute(0)
            .withSecond(0)

        setNotification(
            WALKS_NEAR_NOT_EXPLORED_SLUG,
            time,
            intervalInDays = 28,
        )
    }

    fun cancelNotClickedOnWalksNear() {
        cancelNotification(WALKS_NEAR_NOT_EXPLORED_SLUG)
    }

    fun setMonthlyEmailsAreOff() {
        val time = LocalDateTime.now()
            .plusDays(10)
            .withHour(14)
            .withMinute(0)
            .withSecond(0)

        setNotification(
            TURNED_OFF_EMAILS_SLUG,
            time,
            intervalInDays = 25
        )
    }

    fun cancelMonthlyEmailsAreOff() {
        cancelNotification(TURNED_OFF_EMAILS_SLUG)
    }

    fun setLapsedNotification(
        slug: String,
        time: LocalDateTime,
        intervalInDays: Int = 0,
        planId: Long = -1,
    ) {
        setNotification(
            slug,
            time
                .plusDays(if (preferenceRepository.isLocalNotificationsTest) 0 else intervalInDays.toLong())
                .plusSeconds(if (preferenceRepository.isLocalNotificationsTest) 30 else 0),
            intervalInDays,
            planId
        )
    }

    private fun setNotification(
        slug: String,
        time: LocalDateTime,
        intervalInDays: Int = 0,
        planId: Long = -1,
    ) {

        cancelNotification(slug)

        if (!preferenceRepository.isNotificationsEnabled || !isNotificationsPermissionGranted()) return

        val finalTime = if (preferenceRepository.isLocalNotificationsTest) {
            LocalDateTime
                .now()
                .plusSeconds(30)
        } else {
            time
        }

        localRepository.getNotifications(object :
            AppDatabase.OnDataLoadedListener<Notifications?> {
            override fun onDataLoaded(data: Notifications?) {
                try {
                    val ln = data?.localNotifications?.firstOrNull { it.slug == slug } ?: return
                    setupLocalNotificationWorker(ln, finalTime, intervalInDays, planId)
                } catch (e: Throwable) {
                    Timber.e(e)
                }
            }
        })
    }

    private fun setupLocalNotificationWorker(
        ln: LocalNotification,
        time: LocalDateTime,
        intervalInDays: Int,
        planId: Long,
    ) {
        val data = LocalNotificationInfo(
            slug = ln.slug,
            title = ln.title,
            description = ln.description,
            timestamp = time,
            destination = ln.destination,
            planId = planId,
            isLapsed = ln.isLapsed,
            intervalInDays = intervalInDays
        )

        WorkSchedulerHelper.scheduleLocalNotification(data)
    }

    private fun isNotificationsPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            ActivityCompat.checkSelfPermission(
                UIUtils.getAppContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED else true
    }

    private fun cancelNotification(slug: String) {
        WorkManager.getInstance(context).cancelUniqueWork(slug)
    }

    fun cancelAllNotifications() {
        WorkManager.getInstance(context)
            .cancelAllWorkByTag(WorkSchedulerHelper.LOCAL_NOTIFICATION_WORKER_TAG)
    }

    companion object {
        const val NO_ACCOUNT_USER_SLUG = "user_no_account"
        const val EXTRA_FEATURES_AVAILABLE_SLUG = "extra_features_available"
        const val MWP_NOT_SELECTED_SLUG = "my_walking_plan_not_selected"
        const val MWP_PAUSED_SLUG = "my_walking_plan_paused"
        const val MWP_CHECK_PROGRESS_SLUG = "my_walking_plan_check_progress"
        const val MWP_COMPLETED_SLUG = "my_walking_plan_completed"
        const val WALKS_NEAR_NOT_EXPLORED_SLUG = "walks_near_me_not_explored"
        const val TURNED_OFF_EMAILS_SLUG = "user_with_turned_off_emails"

        const val LOCAL_NOTIFICATION_JSON_EXTRA_KEY = "local_notification_json_extra_key"
    }

}