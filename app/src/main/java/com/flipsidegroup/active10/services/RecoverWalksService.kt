package com.flipsidegroup.active10.services

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.flipside.briskcounter.BriskCounter
import com.flipside.briskcounter.data.StepData
import com.flipside.briskcounter.internal.PastActivityListener
import com.flipsidegroup.active10.Active10App
import com.flipsidegroup.active10.data.models.DailyStepData
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.buildDailyStepData
import com.flipsidegroup.active10.utils.hasInternetConnection
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.disposables.Disposables
import timber.log.Timber
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject



private const val IN_START_TIMESTAMP = "IN_START_TIMESTAMP"
private const val IN_END_TIMESTAMP = "IN_END_TIMESTAMP"
private const val ACTIVE_TEN = 10
private const val SPLIT_INTERVALS_IN_HOURS = 6

fun Context.RecoverWalksService(startTimestamp: Long, endTimestamp: Long): Intent {
    return Intent(this, RecoverWalksService::class.java).apply {
        putExtra(IN_START_TIMESTAMP, startTimestamp)
        putExtra(IN_END_TIMESTAMP, endTimestamp)
    }
}

class RecoverWalksService : JobIntentService(), PastActivityListener {

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var localRepository: LocalRepository

    private var thresholdTimestamp = 0L
    private var finalStartTimestamp = 0L
    private var endTimestamp = 0L

    private var pastActivityObservable = Disposables.empty()

    init {
        Active10App.appComponent.inject(this)
    }

    companion object {
        private const val JOB_ID = 4

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, RecoverWalksService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        if (!hasInternetConnection()) {
            Timber.d("No internet connection")
            stopSelf()
        }

        thresholdTimestamp = intent.getLongExtra(IN_START_TIMESTAMP, 0L)
        endTimestamp = intent.getLongExtra(IN_END_TIMESTAMP, 0L)
        if (thresholdTimestamp == 0L || endTimestamp == 0L) {
            stopSelf()
            return
        }

        retrieveActivity()
    }

    private fun retrieveActivity() {
        val newCalendar = Calendar.getInstance()
        newCalendar.timeInMillis = endTimestamp
        newCalendar.add(Calendar.MINUTE, -2)
        newCalendar.set(Calendar.DAY_OF_MONTH, 1)
        newCalendar.set(Calendar.HOUR_OF_DAY, 0)
        newCalendar.set(Calendar.MINUTE, 0)
        finalStartTimestamp = newCalendar.timeInMillis
        if (finalStartTimestamp < thresholdTimestamp) {
            finalStartTimestamp = thresholdTimestamp
        }

        pastActivityObservable = BriskCounter.retrievePastActivity(
            Date(finalStartTimestamp),
            Date(endTimestamp),
            SPLIT_INTERVALS_IN_HOURS,
            applicationContext,
            this
        )

        Timber.d("Retrieving data for ${TimeUnit.MILLISECONDS.toDays(endTimestamp - finalStartTimestamp)} days")
        Timber.d("thresholdTimestamp $thresholdTimestamp, finalStartTimestamp $finalStartTimestamp, endTimestamp $endTimestamp")
    }

    override fun onSuccess(stepDataList: List<StepData>) {
        pastActivityObservable.dispose()
        Timber.d("Step data received")
        if (stepDataList.isNullOrEmpty()) {
            Timber.d("Empty data set")
            settingsUtils.updateSettings(
                SettingsDataHolder(
                    recoverDataLastTimestamp = finalStartTimestamp,
                    recoverDataLastTimeAccessed = System.currentTimeMillis()
                )
            )
            stopSelf()
            return
        }

        val dailySummaryList = ArrayList<DailyStepData>()

        val stepDataMap = stepDataList.groupBy { it.date }
        stepDataMap.forEach { (_, value) ->
            val dailyStepData = value.buildDailyStepData()
            dailySummaryList.add(dailyStepData)

            localRepository.persistDailyActivity(value.first().startTime, dailyStepData)
        }

        settingsUtils.updateSettings(
            SettingsDataHolder(
                recoverDataLastTimestamp = finalStartTimestamp,
                recoverDataLastTimeAccessed = System.currentTimeMillis()
            )
        )

        Timber.d("stopSelf")
        stopSelf()
    }

    override fun onFailure(pastActivityError: String) {
        FirebaseCrashlytics.getInstance().log(pastActivityError)
        FirebaseCrashlytics.getInstance()
            .recordException(RuntimeException(Constants.FirebaseAnalytics.RETRIEVE_PAST_ACTIVITY_FAILURE))
        Timber.d(pastActivityError)
        pastActivityObservable.dispose()
        stopSelf()
    }

}
