package com.flipsidegroup.active10.services

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ResultReceiver
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
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.RetrieveDataReceiver
import com.flipsidegroup.active10.utils.buildDailyStepData
import com.flipsidegroup.active10.utils.hasInternetConnection
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.disposables.Disposables
import timber.log.Timber
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val IN_START_TIMESTAMP = "IN_START_TIMESTAMP"
private const val IN_END_TIMESTAMP = "IN_END_TIMESTAMP"
private const val IN_RESULT_RECEIVER = "IN_RESULT_RECEIVER"
private const val ACTIVE_TEN = 10
private const val SPLIT_INTERVALS_IN_HOURS = 6

fun Context.MyRecoverLostWalkService(
    startTimestamp: Long,
    endTimestamp: Long,
    resultReceiver: RetrieveDataReceiver?
): Intent {
    return Intent(this, RecoverLostWalkService::class.java).apply {
        putExtra(IN_START_TIMESTAMP, startTimestamp)
        putExtra(IN_END_TIMESTAMP, endTimestamp)
        putExtra(IN_RESULT_RECEIVER, resultReceiver)

    }
}

class RecoverLostWalkService : JobIntentService(), PastActivityListener {

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var localRepository: LocalRepository
    private var startTimestamp: Long = 0L
    private var endTimestamp: Long = 0L
    private var resultReceiver: ResultReceiver? = null

    private var pastActivityObservable = Disposables.empty()

    init {
        Active10App.appComponent.inject(this)
    }

    companion object {
        private const val JOB_ID = 5

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, RecoverLostWalkService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        if (!hasInternetConnection()) {
            Timber.d("No internet connection")
            stopSelf()
        }

        startTimestamp = intent.getLongExtra(IN_START_TIMESTAMP, 0L)
        endTimestamp = intent.getLongExtra(IN_END_TIMESTAMP, 0L)
        resultReceiver = intent.getParcelableExtra(IN_RESULT_RECEIVER)

        if (startTimestamp == 0L || endTimestamp == 0L) {
            stopSelf()
            return
        }

        pastActivityObservable = BriskCounter.retrievePastActivity(
            Date(startTimestamp),
            Date(endTimestamp),
            SPLIT_INTERVALS_IN_HOURS,
            applicationContext,
            this
        )
        Timber.d("Retrieving lost data for ${TimeUnit.MILLISECONDS.toDays(endTimestamp - startTimestamp)} days")
    }

    private fun sendResultData(hadNewData: Boolean) {
        settingsUtils.updateSettings(SettingsDataHolder(hadLostData = hadNewData))
        val bundle = Bundle()
        bundle.putBoolean(RetrieveDataReceiver.KEY_IN_HAD_MISSING_DATA, hadNewData)
        resultReceiver?.send(RetrieveDataReceiver.RETRIEVE_DATA_COMPLETED_CODE, bundle)
    }

    private fun sendErrorResultData() {
        val nrOfTries = settingsUtils.getSettingsHolder().nrOfRetryLostDada ?: 0
        settingsUtils.updateSettings(
            SettingsDataHolder(
                nrOfRetryLostDada = nrOfTries + 1,
                lastRetryLostData = DateHelper.getCurrentTimestamp(),
                shouldSeeRetrieveErrorAlerted = true
            )
        )
        resultReceiver?.send(RetrieveDataReceiver.RETRIEVE_DATA_ERROR_CODE, null)
    }

    override fun onSuccess(stepDataList: List<StepData>) {
        pastActivityObservable.dispose()
        Timber.d("Step lost data received")
        if (settingsUtils.getSettingsHolder().shouldFailRetrieveDataTest == true) {
            sendErrorResultData()
            return
        }

        if (stepDataList.isNullOrEmpty()) {
            sendResultData(false)
            Timber.d("Empty lost data set")
            stopSelf()
            return
        }

        var hadMissingData = false
        val dailySummaryList = ArrayList<DailyStepData>()

        val stepDataMap = stepDataList.groupBy { it.date }
        stepDataMap.forEach { (_, value) ->
            val dailyStepData = value.buildDailyStepData()
            dailySummaryList.add(dailyStepData)
            if (!hadMissingData) {
                hadMissingData = !localRepository.hasActivityOnDay(value.first().startTime)
            }

            localRepository.persistDailyActivity(value.first().startTime, dailyStepData)
        }

        localRepository.getActivitiesDaysNumber(startTimestamp, endTimestamp)
        sendResultData(hadMissingData)

        Timber.d("stopSelf")
        stopSelf()
    }

    override fun onFailure(pastActivityError: String) {
        Timber.d("Error lost data set")
        sendErrorResultData()

        val retryNr = settingsUtils.getSettingsHolder().nrOfRetryLostDada ?: 1
        FirebaseCrashlytics.getInstance().log(pastActivityError)
        val error = Constants.FirebaseAnalytics.RETRIEVE_LOST_ACTIVITY_FAILURE + retryNr
        FirebaseCrashlytics.getInstance()
            .recordException(RuntimeException(error))
        Timber.d(pastActivityError)
        pastActivityObservable.dispose()
        stopSelf()
    }

}
