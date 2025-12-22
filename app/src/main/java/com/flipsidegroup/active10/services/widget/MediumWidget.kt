package com.flipsidegroup.active10.services.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.view.drawToBitmap
import com.flipside.briskcounter.BriskCounter
import com.flipside.briskcounter.data.BriskActivity
import com.flipside.briskcounter.internal.ActivityListener
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.utils.*

/**
 * Implementation of Medium Widget functionality.
 */
class MediumWidget : AppWidgetProvider(), ActivityListener {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        BriskCounter.retrieveTodayActivity(context, this) {
            for (appWidgetId in appWidgetIds) {
                updateMediumWidget(context, appWidgetManager, appWidgetId, it)
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        doubleLet(context, intent) { safeContext, safeIntent ->
            if (safeIntent.hasExtra(Constants.FirebaseAnalytics.UPDATE_MEDIUM_WIDGETS)) {
                safeIntent.extras?.let { bundle ->
                    val ids = bundle.getIntArray(Constants.FirebaseAnalytics.UPDATE_MEDIUM_WIDGETS)
                        ?: intArrayOf()
                    onUpdate(
                        safeContext,
                        AppWidgetManager.getInstance(context),
                        ids
                    )

                    if (!safeIntent.getBooleanExtra("IS_FROM_SMALL", false)) {
                        updateSmallWidgets(AppWidgetManager.getInstance(context), safeContext)
                    }
                }
            } else super.onReceive(context, intent)
        }
    }

    override fun onEnabled(context: Context) {
        // no operation
    }

    override fun onDisabled(context: Context) {
        // no operation
    }

    override fun onSuccess(briskActivity: BriskActivity) {
        // no operation
    }

    override fun onFailure(activityError: String) {
        // no operation
    }

    private fun updateSmallWidgets(appWidgetManager: AppWidgetManager, context: Context) {
        val smallIds =
            appWidgetManager.getAppWidgetIds(ComponentName(context, SmallWidget::class.java))

        val smallUpdateIntent = Intent()
        smallUpdateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        smallUpdateIntent.putExtra(Constants.FirebaseAnalytics.UPDATE_SMALL_WIDGETS, smallIds)
        smallUpdateIntent.putExtra("IS_FROM_MEDIUM", true)
        context.sendBroadcast(smallUpdateIntent)
    }
}

internal fun updateMediumWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    briskActivity: BriskActivity
) {
    val views = RemoteViews(context.packageName, R.layout.active10_medium_widget)
    val horseShoeProgressBar = MyWalkHorseShoeProgressBar(context).apply {
        if (briskActivity.minutesOfBrisk == 0 && briskActivity.minutesOfWalk == 0)
            useGreyBackground = true
        animate = false
        measure(656, 656)
        layout(0, 0, measuredHeight, measuredHeight)
        setUpMyWalks()
        setMaximumProgress(briskActivity.minutesOfWalk)
        updateProgress(briskActivity.minutesOfBrisk)
        views.setTextViewText(R.id.briskMin, briskActivity.minutesOfBrisk.toString())
        views.setTextViewText(R.id.totalMin, briskActivity.minutesOfWalk.toString())
        val numberOfA10 =
            if (briskActivity.minutesOfBrisk / 10 == 0) {
                views.setImageViewResource(R.id.trophy, R.drawable.small_empty_trophy)
                "0"
            } else {
                views.setImageViewResource(R.id.trophy, R.drawable.small_full_trophy)
                "X${briskActivity.minutesOfBrisk / 10}"
            }
        views.setTextViewText(R.id.xTimes, numberOfA10)
        views.setTextViewText(
            R.id.updatedAt,
            context.getString(R.string.updated_at) + DateHelper.getUpdatedAtTime(System.currentTimeMillis())
        )

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntentCompat.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.container, pendingIntent)
    }
    val bitmap = horseShoeProgressBar.drawToBitmap()

    views.setImageViewBitmap(R.id.horseShoe, bitmap)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}