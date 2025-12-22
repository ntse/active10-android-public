package com.flipsidegroup.active10.services.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.core.view.drawToBitmap
import com.flipside.briskcounter.BriskCounter
import com.flipside.briskcounter.data.BriskActivity
import com.flipside.briskcounter.internal.ActivityListener
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.utils.*

/**
 * Implementation of Small Widget functionality.
 */
class SmallWidget : AppWidgetProvider(), ActivityListener {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        BriskCounter.retrieveTodayActivity(context, this) {
            for (appWidgetId in appWidgetIds) {
                updateSmallWidget(context, appWidgetManager, appWidgetId, it)
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        doubleLet(context, intent) { safeContext, safeIntent ->
            if (safeIntent.hasExtra(Constants.FirebaseAnalytics.UPDATE_SMALL_WIDGETS)) {
                safeIntent.extras?.let { bundle ->
                    val ids = bundle.getIntArray(Constants.FirebaseAnalytics.UPDATE_SMALL_WIDGETS)
                        ?: intArrayOf()
                    onUpdate(
                        safeContext,
                        AppWidgetManager.getInstance(context),
                        ids
                    )

                    if (!safeIntent.getBooleanExtra("IS_FROM_MEDIUM", false)) {
                        updateMediumWidgets(AppWidgetManager.getInstance(context), safeContext)
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

    private fun updateMediumWidgets(appWidgetManager: AppWidgetManager, context: Context) {
        val mediumIds =
            appWidgetManager.getAppWidgetIds(ComponentName(context, MediumWidget::class.java))

        val mediumUpdateIntent = Intent()
        mediumUpdateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        mediumUpdateIntent.putExtra(Constants.FirebaseAnalytics.UPDATE_MEDIUM_WIDGETS, mediumIds)
        mediumUpdateIntent.putExtra("IS_FROM_SMALL", true)
        context.sendBroadcast(mediumUpdateIntent)
    }
}

internal fun updateSmallWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    briskActivity: BriskActivity
) {
    val views = RemoteViews(context.packageName, R.layout.active10_small_widget)
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
                views.setViewVisibility(R.id.xTimes, View.GONE)
                "0"
            } else {
                views.setViewVisibility(R.id.xTimes, View.VISIBLE)
                views.setImageViewResource(R.id.trophy, R.drawable.small_full_trophy)
                "X${briskActivity.minutesOfBrisk / 10}"
            }
        views.setTextViewText(R.id.xTimes, numberOfA10)
        views.setTextViewText(
            R.id.updatedAt,
            context.getString(R.string.updated_at) + DateHelper.getUpdatedAtTime(System.currentTimeMillis())
        )

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntentCompat.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.container, pendingIntent)
    }

    val bitmap = horseShoeProgressBar.drawToBitmap()

    views.setImageViewBitmap(R.id.horseShoe, bitmap)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}