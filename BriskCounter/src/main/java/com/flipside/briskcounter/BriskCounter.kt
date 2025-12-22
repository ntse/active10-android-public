package com.flipside.briskcounter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.flipside.briskcounter.data.BriskActivity
import com.flipside.briskcounter.data.BriskPauseResume
import com.flipside.briskcounter.data.Constants
import com.flipside.briskcounter.data.StepData
import com.flipside.briskcounter.internal.*
import com.google.android.gms.fitness.data.Bucket
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataType
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import java.util.concurrent.TimeUnit

private const val STEP_DATA_DATE_FORMAT = "yyyy-MM-dd"
private const val SPLIT_THRESHOLD_IN_HOURS = 12
private const val DAY_IN_HOURS = 24
private const val DAY_MIN_SPLIT_AMOUNT = 6
private const val DAY_MAX_SPLIT_AMOUNT = 12

object BriskCounter {

    private var pendingIntent: PendingIntent? = null
    private var sensorListener: SensorStepListener? = null
    private var initListener: InitListener? = null

    fun initialize(listener: InitListener) {
        initListener = listener
        if (listener is Activity) {
            PermissionHelper.requestOAuthPermission(listener)
        }
    }

    fun resubscribeToGoogleFit() {
        GoogleFitHelper.tryRecordingStepCount(initListener ?: return)
    }

    fun removeInitListener() {
        initListener = null
    }

    fun addInitListener(listener: InitListener) {
        initListener = listener
    }

    fun checkForSubscriptions(listener: SubscriptionCheckListener) {
        GoogleFitHelper.checkForSubscriptions(listener)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED && requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            initListener?.onFailure(Error.MISSING_PERMISSION)
        } else if (resultCode == Activity.RESULT_OK && requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            GoogleFitHelper.tryRecordingStepCount(initListener ?: return)
        }
    }

    @SuppressLint("CheckResult")
    fun retrieveTodayActivity(
        context: Context,
        listener: ActivityListener,
        pauseResume: List<BriskPauseResume>? = null,
        widgetCallback: (BriskActivity) -> Unit = {},
    ): Disposable {
        val cal = getInstance()
        val endTime = Date()
        cal.time = endTime
        val startTime = getStartOfDay(cal.time)

        val msDiff = endTime.time - startTime.time
        val hoursDiff = TimeUnit.MILLISECONDS.toHours(msDiff).toInt()

        val shouldSplitInterval = hoursDiff > SPLIT_THRESHOLD_IN_HOURS
        Timber.d("TODAY ACTIVITY SPLIT: $shouldSplitInterval")

        if (shouldSplitInterval) {
            val intervals = splitTodayDateIntervals(startTime, endTime)
            val observables = ArrayList<Observable<List<Bucket>>>()

            intervals.forEachIndexed { index, date ->
                val nextIndex = index + 1
                if (nextIndex >= intervals.size) {
                    return@forEachIndexed
                }

                val nextDate = intervals[nextIndex]

                observables.add(createDataObservable(context, date, nextDate))
            }

            return mergeTodayActivity(observables, listener, widgetCallback, pauseResume)

        } else {
            return getBriskActivity(context, startTime, endTime, pauseResume).subscribe(
                {
                    Timber.d("TODAY ACTIVITY SUCCESS")
                    listener.onSuccess(it)
                    widgetCallback(it)
                },
                {
                    Timber.e(it.message.orEmpty())
                    listener.onFailure(it.message ?: "Retrieving today's activity not working")
                }
            )
        }
    }

    @SuppressLint("CheckResult")
    fun registerLiveCadence(context: Context, listener: SensorStepListener) {
        val observable = Observable.create<BriskLiveData> { emitter ->
            GoogleFitHelper.findFitnessDataSources(context)
                .addOnSuccessListener { dataSources ->
                    dataSources.forEach { dataSource ->
                        if (dataSource.dataType == DataType.TYPE_STEP_COUNT_DELTA) {

                            sensorListener = listener
                            val intent = Intent(context, SensorBroadcast::class.java)
                            val flag = if (Build.VERSION.SDK_INT > 30) {
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            } else {
                                PendingIntent.FLAG_UPDATE_CURRENT
                            }
                            pendingIntent = PendingIntent.getBroadcast(context, 1, intent, flag)

                            GoogleFitHelper.registerStepCountSensor(
                                dataSource,
                                context,
                                pendingIntent!!
                            )
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Timber.d("Sensors API register completed")
                                    } else {
                                        Timber.d("Sensors API register issue: " + it.exception?.message)
                                        emitter.onError(Throwable(it.exception))
                                    }
                                }
                        } else {
                            emitter.onError(Throwable("No data source available"))
                        }
                    }
                }
                .addOnFailureListener {
                    Timber.d("No available data source found: " + it.message)
                    emitter.onError(it)
                }
        }

        observable.retry()
            .subscribe({
                listener.onSuccess(it.cadence, it.isBrisk)
            }, {
                listener.onFailure(it.message ?: "Registering not working")
            })
    }

    fun unregisterLiveCadence(context: Context) {
        sensorListener = null
        pendingIntent?.let {
            val observable = Observable.fromCallable {
                GoogleFitHelper.unregisterLiveCadence(context, it)
            }

            observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        Timber.d("Sensors API unregister: $it")
                    },
                    {
                        it.message?.let { it1 -> Timber.d(it1) }
                    })
        }
    }

    private fun isEndOfDay(cal: Calendar): Boolean {
        return cal.get(HOUR_OF_DAY) == 23 && cal.get(MINUTE) == 59 &&
                cal.get(SECOND) == 59 && cal.get(MILLISECOND) == 999
    }

    private fun isStartOfDay(cal: Calendar): Boolean {
        return cal.get(HOUR_OF_DAY) == 0 && cal.get(MINUTE) == 0 &&
                cal.get(SECOND) == 0 && cal.get(MILLISECOND) == 0
    }

    private fun getPastIntervals(
        cal: Calendar, amountOfSplitHours: Int, hoursDiff: Int,
        context: Context
    ): ArrayList<Observable<List<Bucket>>> {

        val observables = arrayListOf<Observable<List<Bucket>>>()
        val iterations = hoursDiff / amountOfSplitHours

        var date = cal.time
        var prevDate: Date
        for (i in 0 until iterations) {
            if (isEndOfDay(cal)) {
                cal.add(MILLISECOND, 1)
            }
            cal.add(HOUR_OF_DAY, -amountOfSplitHours)
            prevDate = cal.time

            observables.add(
                createDataObservable(
                    context,
                    prevDate,
                    date
                )
            )

            if (isStartOfDay(cal)) {
                cal.add(MILLISECOND, -1)
            }
            date = cal.time
        }

        return observables
    }

    private fun getSplitAmountDivisibleByDay(amountOfSplitHours: Int): Int {
        return when {
            amountOfSplitHours <= DAY_MIN_SPLIT_AMOUNT -> DAY_MIN_SPLIT_AMOUNT
            amountOfSplitHours >= DAY_MAX_SPLIT_AMOUNT -> DAY_MAX_SPLIT_AMOUNT
            DAY_IN_HOURS % amountOfSplitHours == 0 -> amountOfSplitHours
            else -> {
                var divider = amountOfSplitHours - 1
                while (DAY_IN_HOURS % divider != 0) {
                    divider--
                }

                divider
            }
        }
    }

    //split the hours difference in equal intervals and retrieve the past activities for this intervals.
//amountOfSplitHours is set to the small or equal divider of 24(hours in day)
    @SuppressLint("CheckResult")
    fun retrievePastActivity(
        startTime: Date,
        endTime: Date,
        amountOfSplitHours: Int,
        context: Context,
        listener: PastActivityListener,
        pauseResume: List<BriskPauseResume>? = null,
    ): Disposable {
        val startTimeTimestamp = getStartOfDay(startTime).time
        val endTimeTimestamp = getEndOfDay(endTime).time

        val cal = getInstance()
        cal.timeInMillis = endTimeTimestamp

        val hoursDiff =
            TimeUnit.MILLISECONDS.toHours(endTimeTimestamp - startTimeTimestamp).toInt() + 1
        val divisibleAmountSplit = getSplitAmountDivisibleByDay(amountOfSplitHours)
        val observables = getPastIntervals(cal, divisibleAmountSplit, hoursDiff, context)

        return Observable.merge(
            observables, 1
        )
            .subscribeOn(Schedulers.io())
            .reduce { t1: List<Bucket>, t2: List<Bucket> -> t1 + t2 }
            .subscribe({
                Timber.d("MERGE COMPLETED")
                val processedBuckets = processBuckets(it, pauseResume)
                listener.onSuccess(processedBuckets)
            }, {
                Timber.d("MERGE ERROR")
                listener.onFailure(it.message ?: "Retrieving past activity not working")
            })
    }

    @SuppressLint("CheckResult")
    private fun mergeTodayActivity(
        observables: List<Observable<List<Bucket>>>,
        listener: ActivityListener,
        widgetCallback: (BriskActivity) -> Unit = {},
        pauseResume: List<BriskPauseResume>?,
    ): Disposable {
        return Observable.merge(
            observables, 1
        )
            .reduce { t1: List<Bucket>, t2: List<Bucket> -> t1 + t2 }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ buckets ->
                Timber.d("MERGE TODAY ACTIVITY SUCCESS")
                val totalSteps = buckets.sumOf { getStepCount(it) }
                val processedBuckets = processBuckets(buckets, pauseResume)

                val totalMinutes = processedBuckets.count { it.reachedActiveThreshold } / 2
                val totalBriskCount = processedBuckets.count { it.isBrisk } / 2
                val briskActivity = BriskActivity(totalMinutes, totalBriskCount, totalSteps)
                listener.onSuccess(briskActivity)
                widgetCallback(briskActivity)
            }, {
                Timber.d("MERGE TODAY ACTIVITY ERROR: " + it.message)
                listener.onFailure(it.message ?: "Merging today's activity not working")
            })
    }

    private fun splitTodayDateIntervals(startTime: Date, endTime: Date): List<Date> {
        val intervals = ArrayList<Date>()
        intervals.add(startTime)

        val split = SPLIT_THRESHOLD_IN_HOURS / 2
        val calendar = getInstance()
        calendar.time = endTime
        calendar.add(HOUR_OF_DAY, -split)
        calendar.set(MINUTE, 0)
        calendar.set(SECOND, 0)
        calendar.set(MILLISECOND, 0)

        val splitDate = calendar.time
        intervals.add(splitDate)
        intervals.add(endTime)

        return intervals
    }

    private fun createDataObservable(
        context: Context,
        startTime: Date,
        endTime: Date
    ): Observable<List<Bucket>> {
        return try {
            Timber.d("CALLS MADE")

            Observable.fromCallable {
                val response = GoogleFitHelper.queryGoogleFit(
                    context,
                    startTime,
                    endTime,
                    30,
                    TimeUnit.SECONDS
                )

                response.buckets
            }
        } catch (exception: InterruptedException) {
            Timber.w("UndeliverableException caught while querying Google fit: $exception")
            Observable.never()
        }
    }

    private fun getStartOfDay(date: Date): Date {
        val cal = getInstance()
        cal.time = date
        cal.set(HOUR_OF_DAY, 0)
        cal.set(MINUTE, 0)
        cal.set(SECOND, 0)
        cal.set(MILLISECOND, 0)
        return cal.time
    }

    private fun getEndOfDay(date: Date): Date {
        val cal = getInstance()
        cal.time = date
        cal.set(HOUR_OF_DAY, 23)
        cal.set(MINUTE, 59)
        cal.set(SECOND, 59)
        cal.set(MILLISECOND, 999)
        return cal.time
    }

    private fun getBriskActivity(
        context: Context,
        startTime: Date,
        endTime: Date,
        pauseResume: List<BriskPauseResume>?,
    ): Observable<BriskActivity> {
        val observable = Observable.fromCallable {
            try {
                val response = GoogleFitHelper.queryGoogleFit(
                    context,
                    startTime,
                    endTime,
                    30,
                    TimeUnit.SECONDS
                )
                val totalSteps = response.buckets.sumOf { getStepCount(it) }
                val processedBuckets = processBuckets(response.buckets, pauseResume)
                val totalMinutes = processedBuckets.count { it.reachedActiveThreshold } / 2
                val totalBriskCount = processedBuckets.count { it.isBrisk } / 2
                BriskActivity(totalMinutes, totalBriskCount, totalSteps)
            } catch (exception: InterruptedException) {
                Timber.w("UndeliverableException caught while querying Google fit: $exception")
                BriskActivity(0, 0, 0)
            }
        }
        return observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun processBuckets(buckets: List<Bucket>, pauseResume: List<BriskPauseResume>?): List<StepData> {
        val stepDataList = ArrayList<StepData>()

        if (buckets.isEmpty()) return stepDataList

        var previousStepData: StepData? = null
        buckets.forEach { bucket ->
            val startTime = bucket.getStartTime(TimeUnit.MILLISECONDS)
            val endTime = bucket.getEndTime(TimeUnit.MILLISECONDS)

            val isBucketFitIntoPausePeriod = pauseResume?.any {
                startTime in it.paused..<it.resumed || endTime in it.paused..<it.resumed
            }

            if (isBucketFitIntoPausePeriod == true) return@forEach

            val stepData = StepData(
                startTime = startTime,
                endTime = endTime,
                prefix = SimpleDateFormat("hh", Locale.getDefault())
                    .format(bucket.getStartTime(TimeUnit.MILLISECONDS)).toInt(),
                date = SimpleDateFormat(STEP_DATA_DATE_FORMAT, Locale.getDefault())
                    .format(bucket.getStartTime(TimeUnit.MILLISECONDS))
            )

            stepData.stepCount = getStepCount(bucket)

            previousStepData?.let {
                val isBrisk = checkForBrisk(it, stepData)
                stepData.isBrisk = isBrisk
                it.isBrisk = isBrisk
                it.reachedActiveThreshold = it.isBrisk
            }

            stepData.reachedActiveThreshold = stepData.stepCount >= Constants.activeThreshold || stepData.isBrisk

            stepDataList.add(stepData)

            previousStepData = if (stepData.isBrisk) null else stepData
        }

        return stepDataList
    }

    private fun getStepCount(bucket: Bucket): Int {
        val dataSet = bucket.getDataSet(DataType.TYPE_STEP_COUNT_DELTA)
        dataSet ?: return 0

        var stepCount = 0
        dataSet.dataPoints.forEach { dataPoint ->
            dataPoint.dataType.fields.forEach {
                stepCount += dataPoint.getValue(it).asInt()
            }
        }

        return stepCount
    }

    private fun checkForBrisk(previousStepData: StepData, stepData: StepData): Boolean {
        var isValidPreviousStepData = false
        if (previousStepData.endTime == stepData.startTime) {
            isValidPreviousStepData = true
        }
        if (isValidPreviousStepData) {
            return stepData.stepCount + previousStepData.stepCount >=
                    (Constants.briskThreshold - Constants.briskOffset)
        }
        return false
    }

    private fun processLiveCadence(
        dataPoint: DataPoint,
        emitter: ObservableEmitter<BriskLiveData>
    ) {
        val startTime = dataPoint.getStartTime(TimeUnit.SECONDS)
        val endTime = dataPoint.getEndTime(TimeUnit.SECONDS)

        var stepCount = 0

        dataPoint.dataType.fields.forEach { field ->
            stepCount += dataPoint.getValue(field).asInt()
        }

        val stepCountDuration = endTime - startTime

        if (stepCountDuration == 0L) return

        val cadence = BriskHelper.getCadence(
            stepCount.toDouble(),
            stepCountDuration.toDouble()
        )

        val isBrisk = BriskHelper.isBrisk(cadence)
        Timber.d("Sensor data processed: $isBrisk")
        stepCount = 0
        emitter.onNext(BriskLiveData(cadence, isBrisk))
    }


    class SensorBroadcast : BroadcastReceiver() {

        @SuppressLint("CheckResult")
        override fun onReceive(context: Context?, intent: Intent) {
            val observable = Observable.create { emitter ->
                val dataPoint = DataPoint.extract(intent)
                dataPoint?.let {
                    processLiveCadence(it, emitter)
                }
            }

            observable.retry()
                .subscribe({
                    sensorListener?.onSuccess(it.cadence, it.isBrisk)
                }, {
                    sensorListener?.onFailure(it.message ?: "Registering not working")
                })
        }
    }

    data class BriskLiveData(val cadence: Double, val isBrisk: Boolean)
}
