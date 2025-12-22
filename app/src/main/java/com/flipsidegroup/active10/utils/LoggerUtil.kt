package com.flipsidegroup.active10.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class DebugLogTree : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "Active10", message, t)
    }
}

class ProductionLogTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t == null) {
            FirebaseCrashlytics.getInstance().log("${priorityName(priority)} :: $message")
        } else if (priority < Log.ERROR){
            FirebaseCrashlytics.getInstance().log("${priorityName(priority)} :: $message :: ${t.message}")
        } else {
            FirebaseCrashlytics.getInstance().recordException(t)
        }
    }
}

private fun Bundle?.checkSavedState() =
    if (this == null) "(STATE IS NULL)" else "(STATE IS NOT NULL)"

private fun priorityName(priority: Int): String = when (priority) {
    Log.VERBOSE -> "VERBOSE"
    Log.DEBUG -> "DEBUG"
    Log.INFO -> "INFO"
    Log.WARN -> "WARN"
    Log.ERROR -> "ERROR"
    else -> ""
}

class ActivityLifecycleLogger : Application.ActivityLifecycleCallbacks {

    override fun onActivityPaused(activity: Activity) {
        Timber.d("${activity::class.java.simpleName} PAUSED")
    }

    override fun onActivityResumed(activity: Activity) {
        Timber.d("${activity::class.java.simpleName} RESUMED")
    }

    override fun onActivityStarted(activity: Activity) {
        Timber.d("${activity::class.java.simpleName} STARTED")
    }

    override fun onActivityDestroyed(activity: Activity) {
        Timber.d("${activity::class.java.simpleName} DESTROYED")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        Timber.d("${activity::class.java.simpleName} SAVED INSTANCE STATE")
    }

    override fun onActivityStopped(activity: Activity) {
        Timber.d("${activity::class.java.simpleName} STOPPED")
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Timber.d("${activity::class.java.simpleName} CREATED ${savedInstanceState.checkSavedState()}")
    }
}

