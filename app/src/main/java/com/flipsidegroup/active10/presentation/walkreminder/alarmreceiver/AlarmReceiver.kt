package com.flipsidegroup.active10.presentation.walkreminder.alarmreceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.flipsidegroup.active10.Active10App
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.Notifications
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.home.adapters.TODAY_WALK_SCREEN_POSITION
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.NotificationHelper
import com.flipsidegroup.active10.utils.PendingIntentCompat
import com.flipsidegroup.active10.utils.UIUtils
import timber.log.Timber
import java.util.Date
import javax.inject.Inject


private const val ALARM_RECEIVER_CODE = 29

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        fun createAlarm(context: Context, timeInMs: Long?, currentTime: Long? = null) {
            val timeForAlarm = timeInMs
                ?.let { DateHelper.getNextHourOccurrence(it, currentTime ?: System.currentTimeMillis()) }
                ?: return

            Timber.d("Create alarm at ${Date(timeForAlarm)}")

            val intent = Intent(context, AlarmReceiver::class.java)

            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    ALARM_RECEIVER_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            try {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    timeForAlarm,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                Timber.e(e)
            }
        }

        fun deleteAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, ALARM_RECEIVER_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }
    }

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var localRepository: LocalRepository

    init {
        Active10App.appComponent.inject(this)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("AlarmReceive onReceive. Load and show notification.")
        val event = UIUtils.getString(R.string.walkin_reminder_tapped_event)
        localRepository.getNotifications(object :
            AppDatabase.OnDataLoadedListener<Notifications?> {
            override fun onDataLoaded(data: Notifications?) {
                val reminder = data?.reminder
                val isForegrounded = if (Active10App.instance.isForegrounded) true else null
                NotificationHelper.showNotification(
                    UIUtils.getString(R.string.brisk_reminder_notification_title),
                    reminder ?: UIUtils.getString(R.string.brisk_reminder_notification_description),
                    HomeActivity.getHomeIntent(
                        UIUtils.getAppContext(),
                        isForegrounded,
                        TODAY_WALK_SCREEN_POSITION,
                        event = event
                    )
                )
            }
        })
        // set the next alarm
        context?.let {
            val isBriskReminderSet = settingsUtils.getSettingsHolder().isBriskReminderSet
            val timestamp = settingsUtils.getSettingsHolder().briskReminderTimestamp
            if (isBriskReminderSet != null && isBriskReminderSet) {
                // Add 30 minutes, because even exact alarm is not totally exact.
                createAlarm(context, timestamp, System.currentTimeMillis().plus(1000 * 60 * 30))
            }
        }
    }
}
