package com.flipsidegroup.active10.utils

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.utils.UIUtils.getColor
import java.util.Date
import java.util.Random

object NotificationHelper {

    fun showNotification(title: String, message: String, intent: Intent) {
        if (!PreferenceRepository(UIUtils.getAppContext()).isNotificationsEnabled) return

        val notificationId = (Date().time / 1000L % Integer.MAX_VALUE).toInt() + Random().nextInt()
        val channelId = UIUtils.getAppContext().getString(R.string.default_notification_channel_id)

        val pendingIntent = PendingIntent.getActivity(
            UIUtils.getAppContext(),
            notificationId,
            intent,
            PendingIntentCompat.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(UIUtils.getAppContext(), channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(getColor(R.color.icon_background_pink))
            .setContentTitle(title)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)

        if (ActivityCompat.checkSelfPermission(
                UIUtils.getAppContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(UIUtils.getAppContext())
                .notify(notificationId, builder.build())
        }
    }
}
