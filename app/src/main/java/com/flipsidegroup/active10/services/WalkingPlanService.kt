package com.flipsidegroup.active10.services

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.flipside.briskcounter.BriskCounter
import com.flipside.briskcounter.data.StepData
import com.flipside.briskcounter.internal.PastActivityListener
import com.flipsidegroup.active10.Active10App
import com.flipsidegroup.active10.data.models.HourlyStepData
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.CurrentWalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.PauseResume
import com.flipsidegroup.active10.data.models.dataholders.WalkingPlanEntity
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.WalkingPlanRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.WalkingPlanState
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.buildWalkingPlanDayData
import com.flipsidegroup.active10.utils.filterByDaysForCurrentPlan
import com.flipsidegroup.active10.utils.hasInternetConnection
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.Single
import io.reactivex.disposables.Disposables
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import timber.log.Timber
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private const val SPLIT_INTERVALS_IN_HOURS = 6
private const val DAYS_IN_WEEK = 7

fun Context.WalkingPlanService(): Intent {
    return Intent(this, WalkingPlanService::class.java)
}

data class WalkingPlanServiceData(
    val userPlans: List<CurrentWalkingPlan>,
    val cmsPlans: List<WalkingPlan>,
)

class WalkingPlanService : JobIntentService(), PastActivityListener {

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var localRepository: LocalRepository

    @Inject
    internal lateinit var walkingPlanRepository: WalkingPlanRepository

    @Inject
    internal lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    @Inject
    internal lateinit var preferenceRepository: PreferenceRepository

    private var pastActivityObservable = Disposables.empty()

    private var getDataDisposable = Disposables.empty()

    private var processDataDisposable = Disposables.empty()

    private var saveDataDisposable = Disposables.empty()

    private var planId = -1L

    init {
        Active10App.appComponent.inject(this)
    }

    companion object {
        private const val JOB_ID = 7

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, WalkingPlanService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        if (!hasInternetConnection()) {
            Timber.d("No internet connection")
            stopSelf()
            return
        }

        if (!preferenceRepository.isUserLoggedIn) {
            Timber.d("User is logged out")
            stopSelf()
            return
        }

        getDataDisposable = Single.zip(
            localRepository.getUserWalkingPlans(),
            localRepository.getWalkingPlans(),
            this::createPlanDataObject
        ).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                initializePastActivityObservable(it)
            }, {
                Timber.e(it)
            })

    }

    private fun initializePastActivityObservable(data: WalkingPlanServiceData) {
        with(data) {
            val userPlan = userPlans.firstOrNull {
                it.getEnumState() == WalkingPlanState.PAUSED ||
                it.getEnumState() == WalkingPlanState.ACTIVE
            }

            if (userPlan == null) {
                stopSelf()
                return
            }

            val cmsPlan = cmsPlans.firstOrNull { it.id == userPlan.planId }

            if (cmsPlan == null) {
                stopSelf()
                return
            }

            planId = userPlan.planId

            val daysCount = cmsPlan.planItineraryItems.sortedBy { it.id }.sumOf { DAYS_IN_WEEK }
            val isPlanDone = daysCount == userPlan.days.size

            if (isPlanDone) {
                stopSelf()
                return
            }

            val newestSavedDay = userPlan.days.maxByOrNull { DateTime.parse(it.timestamp).millis }
            val startPlanTimestamp = DateTime.parse(userPlan.startDate)

            val lastWalkingPlanSavedTimestamp = newestSavedDay?.let {
                DateTime.parse(it.timestamp).plusDays(1)
            } ?: startPlanTimestamp
            val endTimestamp = DateTime.now().minusDays(1)

            if (DateHelper.isSameDay(lastWalkingPlanSavedTimestamp.millis)) {
                stopSelf()
                return
            }

            val pauseResumeWithStartPlan = userPlan.pauseResume.toMutableList()
            pauseResumeWithStartPlan.add(
                PauseResume(
                    paused = LocalDateTime.parse(userPlan.startDate).with(LocalTime.MIN).toString(),
                    resumed = userPlan.startDate,
                )
            )

            pastActivityObservable = BriskCounter.retrievePastActivity(
                Date(lastWalkingPlanSavedTimestamp.millis),
                Date(endTimestamp.millis),
                SPLIT_INTERVALS_IN_HOURS,
                applicationContext,
                this@WalkingPlanService,
                pauseResume = pauseResumeWithStartPlan.map { it.toBriskPauseResume() },
            )
            Timber.d("Retrieving data for ${TimeUnit.MILLISECONDS.toDays(endTimestamp.millis - lastWalkingPlanSavedTimestamp.millis)} days")
        }
    }

    private fun createPlanDataObject(
        userPlans: List<CurrentWalkingPlan>, cmsPlans: List<WalkingPlan>
    ): WalkingPlanServiceData = WalkingPlanServiceData(userPlans, cmsPlans)

    override fun onSuccess(stepDataList: List<StepData>) {
        pastActivityObservable.dispose()
        getDataDisposable.dispose()

        if (stepDataList.isEmpty()) {
            Timber.d("Empty data set")
            stopSelf()
            return
        }

        processDataDisposable = Single.zip(
            localRepository.getWalkingPlanEntity(),
            localRepository.getWalkingPlans(),
        ) { planEntities, cmsPlans ->
            Pair(planEntities, cmsPlans)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { (planEntities, cmsPlans) ->
                    saveData(planEntities, cmsPlans, stepDataList)
                },
                {
                    Timber.e(it)
                }
            )

    }

    private fun saveData(
        planEntities: List<WalkingPlanEntity>,
        cmsPlans: List<WalkingPlan>,
        stepDataList: List<StepData>
    ) {
        val hourlySummaryList = ArrayList<HourlyStepData>()

        val planEntity =
            planEntities.firstOrNull { it.currentWalkingPlan?.planId == planId } ?: return
        val currentPlan = planEntity.currentWalkingPlan ?: return
        val cmsPlan = cmsPlans.firstOrNull { it.id == currentPlan.planId } ?: return

        val stepDataFiltered =
            stepDataList.filterByDaysForCurrentPlan(currentPlan, cmsPlan)
        val stepDataMap = stepDataFiltered.groupBy { it.date }

        stepDataMap.forEach { (_, value) ->
            value.map {
                it.prefix = DateHelper.getPrefixTime(it.startTime).take(2).toInt()
            }
            val dailyStepData = value.buildWalkingPlanDayData()
            currentPlan.addDays(cmsPlan, listOf(dailyStepData))

            val hourlyStepDataMap = value.groupBy { it.prefix }
            buildHourlyStepData(hourlyStepDataMap, hourlySummaryList)

            firebaseAnalyticsHelper.sendDailyWalkingPlanEvent(
                planEntity,
                cmsPlan,
                hourlySummaryList,
            )
        }

        saveDataDisposable = walkingPlanRepository.saveWalkingPlanEntity(planEntity)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doFinally {
                stopSelf()
            }
            .subscribeBy(
                onComplete = {
                    Timber.d("Walking Plan data from Google Fit saved successfully")
                },
                onError = {
                    Timber.w("Walking Plan data from Google Fit saving error", it)
                }
            )
    }

    private fun buildHourlyStepData(
        hourlyStepDataMap: Map<Int, List<StepData>>,
        outputList: ArrayList<HourlyStepData>
    ): List<HourlyStepData> {
        hourlyStepDataMap.forEach { (key, value) ->
            val hourlyStepData = HourlyStepData()

            hourlyStepData.hour = key
            val activeSteps = value.filter { it.reachedActiveThreshold }
            hourlyStepData.briskMinute = activeSteps.count { it.isBrisk } / 2
            hourlyStepData.nonBriskMinute = activeSteps.size / 2 - hourlyStepData.briskMinute
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
        getDataDisposable.dispose()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveDataDisposable.dispose()
        processDataDisposable.dispose()
    }
}
