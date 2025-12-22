package com.flipsidegroup.active10.services

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.JobIntentService
import com.flipside.briskcounter.BriskCounter
import com.flipside.briskcounter.data.StepData
import com.flipside.briskcounter.internal.PastActivityListener
import com.flipsidegroup.active10.Active10App
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.models.DailyStepData
import com.flipsidegroup.active10.data.models.HourlyStepData
import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.models.requests.DailySummaryRequest
import com.flipsidegroup.active10.data.models.requests.HourlySummaryRequest
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.home.activities.MY_WALKS_UPDATED
import com.flipsidegroup.active10.presentation.targets.activities.DEFAULT_TARGET
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.EVENT_ACTIVITIES_PER_HOUR
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_BRISK_MINUTE
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_CREATED_AT
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_DATE
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_LATITUDE
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_LONGITUDE
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.KEY_NON_BRISK_MINUTE
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.DeviceUtils
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.asLong
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
private const val ACTIVE_TEN = 10
private const val SPLIT_INTERVALS_IN_HOURS = 6
private const val DAYS_THRESHOLD = 10
private const val FIRST_POSITION = 0

fun Context.MyWalksService(
    startTimestamp: Long,
    endTimestamp: Long
): Intent {
    return Intent(this, MyWalksService::class.java).apply {
        putExtra(IN_START_TIMESTAMP, startTimestamp)
        putExtra(IN_END_TIMESTAMP, endTimestamp)
    }
}

class MyWalksService : JobIntentService(), PastActivityListener {

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var localRepository: LocalRepository

    @Inject
    internal lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    private var pastActivityObservable = Disposables.empty()

    init {
        Active10App.appComponent.inject(this)
    }

    companion object {
        private const val JOB_ID = 1

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, MyWalksService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        if (!hasInternetConnection()) {
            Timber.d("No internet connection")
            stopSelf()
        }

        val startTimestamp = intent.getLongExtra(IN_START_TIMESTAMP, 0L)
        val endTimestamp = intent.getLongExtra(IN_END_TIMESTAMP, 0L)
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
        Timber.d("Retrieving data for ${TimeUnit.MILLISECONDS.toDays(endTimestamp - startTimestamp)} days")
    }

    override fun onSuccess(stepDataList: List<StepData>) {
        pastActivityObservable.dispose()

        if (stepDataList.isNullOrEmpty()) {
            Timber.d("Empty data set")
            stopSelf()
            return
        }

        val dailySummaryList = ArrayList<DailyStepData>()
        val hourlySummaryList = ArrayList<HourlyStepData>()

        val deviceLocationHolder = settingsUtils.getSettingsHolder().deviceLocationHolder
        val latitude = deviceLocationHolder?.latitude?.toFloat()
        val longitude = deviceLocationHolder?.longitude?.toFloat()

        val stepDataMap = stepDataList.groupBy { it.date }

        stepDataMap.forEach { (_, value) ->
            value.map {
                it.prefix = DateHelper.getPrefixTime(it.startTime).take(2).toInt()
            }
            val dailyStepData = value.buildDailyStepData()
            dailySummaryList.add(dailyStepData)

            localRepository.persistDailyActivity(value.first().startTime, dailyStepData)

            val hourlyStepDataMap = value.groupBy { it.prefix }
            buildHourlyStepData(hourlyStepDataMap, latitude, longitude, hourlySummaryList)
        }

        val walksUpdatedIntent = Intent()
        walksUpdatedIntent.action = MY_WALKS_UPDATED
        sendBroadcast(walksUpdatedIntent)


        val requestsPair = buildRequests(hourlySummaryList, dailySummaryList)

        Timber.d("Sent steps data to firebase")
        sendActivitiesPerHourFirebaseEvent(requestsPair?.first)

        requestsPair?.let {
            settingsUtils.updateSettings(SettingsDataHolder(lastMyWalksSavedTimestamp = System.currentTimeMillis()))
        }

        stopSelf()
    }

    private fun buildHourlyStepData(
        hourlyStepDataMap: Map<Int, List<StepData>>,
        latitude: Float?,
        longitude: Float?,
        outputList: ArrayList<HourlyStepData>
    ): List<HourlyStepData> {

        hourlyStepDataMap.forEach { (key, value) ->
            val hourlyStepData = HourlyStepData()

            if (latitude != null && longitude != null) {
                hourlyStepData.latitude = latitude
                hourlyStepData.longitude = longitude
            }

            hourlyStepData.hour = key
            hourlyStepData.briskMinute = value.filter { it.isBrisk }.count() / 2
            hourlyStepData.nonBriskMinute = value.size / 2 - hourlyStepData.briskMinute
            val startTime = value.first().startTime
            hourlyStepData.date = DateHelper.formatAnalyticsDate(startTime)
            outputList.add(hourlyStepData)
        }

        return outputList
    }


    override fun onFailure(pastActivityError: String) {
        FirebaseCrashlytics.getInstance().log(pastActivityError)
        FirebaseCrashlytics.getInstance()
            .recordException(RuntimeException(Constants.FirebaseAnalytics.RETRIEVE_PAST_ACTIVITY_FAILURE))
        Timber.d(pastActivityError)
        pastActivityObservable.dispose()
        stopSelf()
    }

    private fun buildRequests(
        hourlyStepDataList: List<HourlyStepData>,
        dailyStepDataList: List<DailyStepData>
    ): Pair<HourlySummaryRequest, DailySummaryRequest>? {
        val deviceId = settingsUtils.getSettingsHolder().deviceId
        deviceId?.let {
            val hourlySummaryRequest =
                HourlySummaryRequest(deviceId = it, activities = hourlyStepDataList)
            val dailySummaryRequest =
                DailySummaryRequest(deviceId = it, summaries = dailyStepDataList)

            return Pair(hourlySummaryRequest, dailySummaryRequest)
        }

        return null
    }

    private fun sendActivitiesPerHourFirebaseEvent(
        hourlySummaryRequest: HourlySummaryRequest?
    ) {
        if (hourlySummaryRequest == null) {
            return
        }

        val target = settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: DEFAULT_TARGET

        hourlySummaryRequest.activities.groupBy { it.date }.values.forEach { hourlyStepDataList ->

            val briskMinutesStringList = buildWalkingMinutesStringList(hourlyStepDataList, true)
            val nonBriskMinutesStringList = buildWalkingMinutesStringList(hourlyStepDataList, false)

            val bundle = Bundle()

            bundle.putString(KEY_NON_BRISK_MINUTE, nonBriskMinutesStringList)
            bundle.putString(KEY_BRISK_MINUTE, briskMinutesStringList)

            bundle.putFloat(KEY_LATITUDE, hourlyStepDataList.first().latitude)
            bundle.putFloat(KEY_LONGITUDE, hourlyStepDataList.first().longitude)

            bundle.putString(KEY_DATE, hourlyStepDataList.first().date)
            bundle.putString(
                KEY_CREATED_AT,
                DateHelper.formatStepDataTimestamp(System.currentTimeMillis())
            )
            bundle.putString(
                Constants.FirebaseAnalytics.DEVICE_ID,
                DeviceUtils.getDeviceId(settingsUtils)
            )
            bundle.putString(Constants.FirebaseAnalytics.APP_VERSION, BuildConfig.VERSION_NAME)
            bundle.putString(Constants.FirebaseAnalytics.OS, Constants.DEVICE_OS)
            bundle.putLong("target", target.toLong())
            bundle.putLong(
                "hit_target",
                ((target * 10) <= hourlyStepDataList.sumOf { it.briskMinute }).asLong()
            )

            firebaseAnalyticsHelper.sendFirebaseEvent(EVENT_ACTIVITIES_PER_HOUR, bundle)
        }
    }

    private fun buildWalkingMinutesStringList(
        stepData: List<HourlyStepData>,
        isBrisk: Boolean
    ): String {
        val stepsArray =
            arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        stepData.forEach {
            if (isWalkingHourValid(it.hour)) {
                stepsArray[it.hour] = if (isBrisk) {
                    it.briskMinute
                } else {
                    it.nonBriskMinute
                }
            }
        }

        return stepsArray.toString()
            .replace(" ", "")
            .replace("[", "")
            .replace("]", "")
    }

    private fun isWalkingHourValid(walkingHour: Int): Boolean {
        return walkingHour in 0..23
    }
}
