package com.flipside.briskcounter

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import com.flipside.briskcounter.internal.Error
import com.flipside.briskcounter.internal.InitListener
import com.flipside.briskcounter.internal.State
import com.flipside.briskcounter.internal.SubscriptionCheckListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.fitness.request.SensorRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import timber.log.Timber
import java.util.Date
import java.util.concurrent.TimeUnit


private const val GOOGLE_FIT_SAMPLING_RATE = 15L

object GoogleFitHelper {

    fun disconnectGoogleFit(context: Context, onSuccess: () -> Unit) {
        val account = GoogleSignIn.getLastSignedInAccount(context)!!

        Fitness.getConfigClient(context, account)
            .disableFit()
            .addOnFailureListener { Timber.e(it) }
            .addOnSuccessListener { onSuccess() }
    }

    fun queryGoogleFit(
        context: Context,
        startTime: Date,
        endTime: Date,
        period: Int,
        timeUnit: TimeUnit
    ): DataReadResponse {
        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_STEP_COUNT_DELTA)
            .bucketByTime(period, timeUnit)
            .setTimeRange(startTime.time, endTime.time, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build()

        val account = GoogleSignIn.getLastSignedInAccount(context)!!

        return Tasks.await(
            Fitness.getHistoryClient(context, account)
                .readData(readRequest)
        )
    }

    fun checkForSubscriptions(listener: SubscriptionCheckListener) {
        val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(listener as Activity)
        lastSignedInAccount ?: return

        Fitness.getRecordingClient(
            listener as Activity,
            lastSignedInAccount
        )
            .listSubscriptions(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { subscriptions ->
                if (subscriptions.isNullOrEmpty()) {
                    listener.isNotSubscribed()
                }
            }
            .addOnFailureListener {
                listener.isNotSubscribed()
            }
    }

    fun tryRecordingStepCount(initListener: InitListener) {
        if (initListener is Activity) {
            Fitness.getRecordingClient(
                initListener as Activity,
                GoogleSignIn.getLastSignedInAccount(initListener as Activity)!!
            ).subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener {
                    initListener.onSuccess(State.GRANTED)
                }
                .addOnFailureListener {
                    initListener.onFailure(Error.NOT_INITIALIZED)
                }
        }
    }

    fun findFitnessDataSources(context: Context): Task<MutableList<DataSource>> {
        val dataSourcesRequest = DataSourcesRequest.Builder()
            .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
            .setDataSourceTypes(DataSource.TYPE_DERIVED)
            .build()

        return Fitness.getSensorsClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .findDataSources(dataSourcesRequest)
    }

    fun registerStepCountSensor(
        dataSource: DataSource,
        context: Context,
        pendingIntent: PendingIntent
    ): Task<Void> {

        val sensorRequest = SensorRequest.Builder()
            .setDataSource(dataSource)
            .setDataType(dataSource.dataType)
            .setSamplingRate(GOOGLE_FIT_SAMPLING_RATE, TimeUnit.SECONDS)
            .build()

        return Fitness.getSensorsClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
            .add(sensorRequest, pendingIntent)
    }

    fun unregisterLiveCadence(
        context: Context,
        pendingIntent: PendingIntent
    ): Void {
        return Tasks.await(
            Fitness.getSensorsClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
                .remove(pendingIntent)
        )
    }
}
