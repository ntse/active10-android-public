package com.flipsidegroup.active10.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.JobIntentService
import com.flipside.briskcounter.BriskCounter
import com.flipside.briskcounter.data.StepData
import com.flipside.briskcounter.internal.PastActivityListener
import com.flipsidegroup.active10.Active10App
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.MigrationActivity
import com.flipsidegroup.active10.data.models.DailyStepData
import com.flipsidegroup.active10.data.models.HourlyStepData
import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.migration.MigrationRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.utils.*
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.HISTORICAL_RANGE
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import io.reactivex.disposables.Disposables
import timber.log.Timber
import java.util.*
import javax.inject.Inject


private const val SPLIT_INTERVALS_IN_HOURS = 6
private const val STRING_DELIMITER = "T"
private const val ACTIVE_TEN = 10

fun Context.MigrationService(): Intent {
    return Intent(this, MigrationService::class.java)
}

class MigrationService : JobIntentService() {

    @Inject
    internal lateinit var migrationRepository: MigrationRepository

    @Inject
    internal lateinit var localRepository: LocalRepository

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    private var pastActivityObserver = Disposables.empty()

    init {
        Active10App.appComponent.inject(this)
    }

    companion object {
        private const val JOB_ID = 2

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, MigrationService::class.java, JOB_ID, intent)
        }
    }

    @SuppressLint("CheckResult")
    override fun onHandleWork(intent: Intent) {
        Timber.d("MigrationService.onHandleIntent, Has internet connection: ${hasInternetConnection()}")
        if (!hasInternetConnection()) {
            Timber.d("No internet connection")
            stopSelf()
            return
        }

        getMigrationData()
        settingsUtils.updateSettings(SettingsDataHolder(isMigrationDataSent = true))
    }

    @SuppressLint("CheckResult")
    private fun getMigrationData() {
        migrationRepository.getMigrationData()
            .subscribe({ migrationData ->
                Timber.d("MigrationService.getMigrationData, Migration json size: ${migrationData.migrationActivity.size}")

                val unformattedMigrationDataList = migrationData.migrationActivity
                val formattedMigrationDataList = arrayListOf<MigrationActivity>()
                buildFormattedMigrationDataList(
                    unformattedMigrationDataList,
                    formattedMigrationDataList
                )

                val oldSummaryList =
                    buildOldSummaryList(unformattedMigrationDataList, formattedMigrationDataList)

                val interval = buildIntervals()

                pastActivityObserver = BriskCounter.retrievePastActivity(
                    interval.first, interval.second,
                    SPLIT_INTERVALS_IN_HOURS,
                    applicationContext,
                    object : PastActivityListener {

                        override fun onSuccess(stepDataList: List<StepData>) {
                            val activeStepsData = stepDataList.filter { it.reachedActiveThreshold }
                            Timber.d("MigrationService.retrievePastActivity, Array size: ${activeStepsData.size}")
                            Timber.d("Step data received")

                            val newSummaryList = formatGoogleFitHistoricalData(activeStepsData)
                            val dailyList = formatGoogleFitHistoricalDailyData(activeStepsData)
                            sendActivitiesPerHourFirebaseEvent(dailyList)
                            Timber.d("MigrationService.retrievePastActivity, Formatted array size: ${newSummaryList.size}")
                            pastActivityObserver.dispose()
                            stopSelf()
                        }

                        override fun onFailure(pastActivityError: String) {
                            Timber.e(
                                IllegalStateException(pastActivityError),
                                "MigrationService.retrievePastActivity"
                            )
                            pastActivityObserver.dispose()
                            stopSelf()
                        }
                    })

            }, {
                Timber.d(it)
                pastActivityObserver.dispose()
                stopSelf()
            })
    }

    private fun sendActivitiesPerHourFirebaseEvent(
        hourlyActivity: ArrayList<HourlyStepData>
    ) {
        hourlyActivity.groupBy { it.date }.values.forEachIndexed { index, hourlyStepDataList ->
            val briskMinutesStringList = buildWalkingMinutesStringList(hourlyStepDataList, true)
            val nonBriskMinutesStringList =
                buildWalkingMinutesStringList(hourlyStepDataList, false)

            val bundle = Bundle()

            bundle.putInt(HISTORICAL_RANGE, index - 7)
            bundle.putString(
                Constants.FirebaseAnalytics.KEY_NON_BRISK_MINUTE,
                nonBriskMinutesStringList
            )
            bundle.putString(
                Constants.FirebaseAnalytics.KEY_BRISK_MINUTE,
                briskMinutesStringList
            )

            bundle.putFloat(
                Constants.FirebaseAnalytics.KEY_LATITUDE,
                hourlyStepDataList.first().latitude
            )
            bundle.putFloat(
                Constants.FirebaseAnalytics.KEY_LONGITUDE,
                hourlyStepDataList.first().longitude
            )

            bundle.putString(
                Constants.FirebaseAnalytics.KEY_DATE,
                hourlyStepDataList.first().date
            )
            bundle.putString(
                Constants.FirebaseAnalytics.KEY_CREATED_AT,
                DateHelper.formatStepDataTimestamp(System.currentTimeMillis())
            )
            bundle.putString(
                Constants.FirebaseAnalytics.DEVICE_ID,
                DeviceUtils.getDeviceId(settingsUtils)
            )
            bundle.putString(Constants.FirebaseAnalytics.APP_VERSION, BuildConfig.VERSION_NAME)
            bundle.putString(Constants.FirebaseAnalytics.OS, Constants.DEVICE_OS)

            firebaseAnalyticsHelper.sendFirebaseEvent(
                Constants.FirebaseAnalytics.EVENT_HISTORIC_ACTIVITIES_PER_HOUR,
                bundle
            )
        }
    }

    private fun buildWalkingMinutesStringList(
        stepData: List<HourlyStepData>,
        isBrisk: Boolean
    ): String {
        val stepsArray =
            arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

        stepData.forEach {
            if (it.hour in 0..23) {
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

    private fun buildIntervals(): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val endDate = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val startDate = calendar.time
        return Pair(startDate, endDate)
    }

    private fun buildOldSummaryList(
        unformattedMigrationDataList: List<MigrationActivity>,
        formattedMigrationDataList: List<MigrationActivity>
    ): List<DailyStepData> {
        val oldDailySummaryList = ArrayList<DailyStepData>()

        formattedMigrationDataList.forEach { migrationActivity ->
            val stringDate = migrationActivity.timestamp.split(STRING_DELIMITER)
            val timestamp = DateHelper.parseStringDate(migrationActivity.timestamp)

            val matchMigrationActivity =
                unformattedMigrationDataList.firstOrNull {
                    it.timestamp.split(STRING_DELIMITER).first() == stringDate.first()
                }

            matchMigrationActivity?.let {
                migrationActivity.briskMins = it.briskMins
                migrationActivity.totalMins = it.totalMins

                saveDailyEntry(timestamp, migrationActivity)
            }

            oldDailySummaryList.add(buildDailyEntry(timestamp, migrationActivity))
        }

        return oldDailySummaryList
    }

    private fun buildFormattedMigrationDataList(
        migrationDataList: List<MigrationActivity>,
        formattedMigrationDataList: ArrayList<MigrationActivity>
    ) {
        if (!migrationDataList.isNullOrEmpty()) {
            val firstEntry = migrationDataList[0]
            val calendar = Calendar.getInstance()
            calendar.time = Date(DateHelper.parseStringDate(firstEntry.timestamp))

            while (!DateHelper.isSameDay(calendar.timeInMillis)) {
                formattedMigrationDataList.add(
                    MigrationActivity(
                        timestamp = DateHelper.formatStepDataTimestamp(
                            calendar.timeInMillis
                        )
                    )
                )
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        }
    }

    private fun formatGoogleFitHistoricalData(stepDataList: List<StepData>): ArrayList<DailyStepData> {
        val stepDataMap = stepDataList.groupBy { it.date }
        val newSummaryList = ArrayList<DailyStepData>()

        stepDataMap.forEach { (_, value) ->
            val dailyStepData = DailyStepData()

            dailyStepData.briskMinutes = value.filter { it.isBrisk }.count() / 2
            dailyStepData.nonBriskMinutes =
                (value.size - dailyStepData.briskMinutes) / 2
            dailyStepData.date = value.first().date
            dailyStepData.activeTens = dailyStepData.briskMinutes / ACTIVE_TEN

            newSummaryList.add(dailyStepData)
        }
        return newSummaryList
    }

    private fun formatGoogleFitHistoricalDailyData(stepDataList: List<StepData>): ArrayList<HourlyStepData> {
        val stepDataMap = stepDataList.groupBy { it.date }
        val newSummaryList = ArrayList<HourlyStepData>()

        val deviceLocationHolder = settingsUtils.getSettingsHolder().deviceLocationHolder
        val latitude = deviceLocationHolder?.latitude?.toFloat()
        val longitude = deviceLocationHolder?.longitude?.toFloat()

        stepDataMap.forEach { (_, value) ->
            value.map {
                it.prefix = DateHelper.getPrefixTime(it.startTime).take(2).toInt()
            }
            val hourlyStepDataMap = value.groupBy { it.prefix }
            buildHourlyStepData(hourlyStepDataMap, latitude, longitude, newSummaryList)
        }

        return newSummaryList
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

            var totalBriskSeconds = 0
            var totalNonBriskSeconds = 0

            hourlyStepData.hour = key

            val cal = Calendar.getInstance()
            value.map {
                cal.time = Date(it.endTime - it.startTime)
                if (it.isBrisk) {
                    totalBriskSeconds += cal.get(Calendar.SECOND)
                } else {
                    totalNonBriskSeconds += cal.get(Calendar.SECOND)
                }
            }

            hourlyStepData.briskMinute = totalBriskSeconds / 60
            hourlyStepData.nonBriskMinute = totalNonBriskSeconds / 60
            val startTime = value.first().startTime
            hourlyStepData.date = DateHelper.formatAnalyticsDate(startTime)
            outputList.add(hourlyStepData)
        }
        return outputList
    }

    private fun buildDailyEntry(
        timestamp: Long,
        migrationActivity: MigrationActivity
    ): DailyStepData {
        val briskMinutes = migrationActivity.briskMins
        return DailyStepData(
            migrationActivity.totalMins - briskMinutes,
            briskMinutes,
            briskMinutes / ACTIVE_TEN,
            DateHelper.formatAnalyticsDate(timestamp)
        )
    }

    private fun saveDailyEntry(
        timestamp: Long,
        it: MigrationActivity
    ) {
        localRepository.persistActivity(
            StepOverview(
                timestamp,
                it.briskMins,
                it.totalMins
            )
        )
    }
}
