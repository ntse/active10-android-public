package com.flipsidegroup.active10

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.jsonstorage.JsonRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.di.components.AppComponent
import com.flipsidegroup.active10.di.components.DaggerAppComponent
import com.flipsidegroup.active10.services.widget.MediumWidget
import com.flipsidegroup.active10.services.widget.SmallWidget
import com.flipsidegroup.active10.utils.ActivityLifecycleLogger
import com.flipsidegroup.active10.utils.CheckAuthManager
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.UPDATE_MEDIUM_WIDGETS
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.UPDATE_SMALL_WIDGETS
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.DebugLogTree
import com.flipsidegroup.active10.utils.DeviceUtils
import com.flipsidegroup.active10.utils.ProductionLogTree
import com.flipsidegroup.active10.utils.isBiometricAvailable
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.reactivex.Observable
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class Active10App : Application(), HasAndroidInjector {

    companion object {
        lateinit var instance: Active10App
        lateinit var appComponent: AppComponent
    }

    var isForegrounded = false

    @Inject
    internal lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    internal lateinit var jsonRepository: JsonRepository

    @Inject
    lateinit var appWidgetManager: AppWidgetManager

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var preferenceRepository: PreferenceRepository

    @Inject
    internal lateinit var checkAuthManager: CheckAuthManager

    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var lastUserInteractionTime = System.currentTimeMillis()

    override fun androidInjector() = activityDispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        instance = this

        initializeLogging()
        initializeRxJavaErrorHandler()

        setUpDependencyInjection()
        setUpNotificationChannel()
        setUpRealm()
        setUpJoda()
        persistJsonContent()
        setupFirebaseAnalyticsUserID()

        updateContinuousUsageNhsTime()
        checkAuthOnAppOpen()
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                isForegrounded = false
                updateWidgets()
                scheduleAuthScreen()
            }

            override fun onStart(owner: LifecycleOwner) {
                isForegrounded = true
                updateWidgets()
                scheduleAuthScreen()
            }
        })
    }

    private fun updateWidgets() {
        val mediumIds =
            appWidgetManager.getAppWidgetIds(ComponentName(this, MediumWidget::class.java))
        val smallIds =
            appWidgetManager.getAppWidgetIds(ComponentName(this, SmallWidget::class.java))
        val mediumUpdateIntent = Intent()
        val smallUpdateIntent = Intent()
        smallUpdateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        mediumUpdateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        smallUpdateIntent.putExtra(UPDATE_SMALL_WIDGETS, smallIds)
        mediumUpdateIntent.putExtra(UPDATE_MEDIUM_WIDGETS, mediumIds)
        sendBroadcast(smallUpdateIntent)
        sendBroadcast(mediumUpdateIntent)
    }

    private fun setUpRealm() {
        Realm.init(this)
    }

    private fun initializeLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugLogTree())
        } else {
            Timber.plant(ProductionLogTree())
        }

        registerActivityLifecycleCallbacks(ActivityLifecycleLogger())
    }

    // https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
    private fun initializeRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler {
            when (it) {
                is UndeliverableException -> Timber.e(it.cause)
                is IOException, is InterruptedException -> return@setErrorHandler
                is NullPointerException, is IllegalArgumentException, is IllegalStateException -> Thread.currentThread()
                    .uncaughtExceptionHandler?.uncaughtException(Thread.currentThread(), it)
                else -> Timber.e(it)
            }
        }
    }

    private fun setUpDependencyInjection() {
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
        appComponent.inject(this)
    }

    private fun setUpNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setUpJoda() {
        JodaTimeAndroid.init(this)
    }

    @SuppressLint("CheckResult")
    private fun persistJsonContent() {
        settingsUtils.updateSettings(SettingsDataHolder(rewardBadgesStartTimestamp = null))
        val isJsonContentPersisted = settingsUtils.getSettingsHolder().isJsonContentPersisted
        val hasRewards = settingsUtils.getSettingsHolder().rewardBadgesStartTimestamp != null
        if (isJsonContentPersisted == true && hasRewards) return

        val rewardBadgesObs = jsonRepository.getRewardBadgesList()
        val obsArray = if (isJsonContentPersisted == true) {
            arrayListOf(rewardBadgesObs)
        } else {
            val communityObs = jsonRepository.getAboutCommunity()
            val faqObs = jsonRepository.getFaqList()
            val goalsObs = jsonRepository.getGoalsList()
            val howItWorksObs = jsonRepository.getHowItWorksList()
            val tipsObs = jsonRepository.getTips()
            val walkingMessagesObs = jsonRepository.getWalkingMessages()
            val legalsObs = jsonRepository.getLegalList()
            arrayListOf(
                communityObs,
                faqObs,
                goalsObs,
                howItWorksObs,
                tipsObs,
                walkingMessagesObs,
                rewardBadgesObs,
                legalsObs
            )
        }

        Observable.concat(obsArray)
            .subscribeOn(Schedulers.io())
            .subscribe({
            }, {
                Timber.d(it)
            }, {
                Timber.d("Json content persisted")
                settingsUtils.updateSettings(SettingsDataHolder(isJsonContentPersisted = true))
                settingsUtils.updateSettings(
                    SettingsDataHolder(
                        rewardBadgesStartTimestamp = DateHelper.getStartTimestampOfDay(
                            System.currentTimeMillis()
                        )
                    )
                )
            })
    }

    private fun setupFirebaseAnalyticsUserID() {
        FirebaseAnalytics.getInstance(this).setUserId(DeviceUtils.getDeviceId(settingsUtils))
    }

    fun updateUserInteraction() {
        lastUserInteractionTime = System.currentTimeMillis()
        scheduleAuthScreen()
    }

    private fun updateContinuousUsageNhsTime() {
        preferenceRepository.continuousUsageNhsTime = System.currentTimeMillis()
    }

    private fun checkAuthOnAppOpen() {
        checkAuthManager.invokeAuthScreen.value = true
    }

    private fun scheduleAuthScreen() {
        runnable?.let { handler.removeCallbacks(it) }

        if (checkAuthManager.invokeAuthScreen.value == true ||
            !preferenceRepository.isUserLoggedIn) return

        val delay1Min = if (preferenceRepository.set30SecForAuthTest == "1min") 30 * 1000 else 1 * 60 * 1000
        val delay5min = if (preferenceRepository.set30SecForAuthTest == "5min") 30 * 1000 else 5 * 60 * 1000
        val delay30min = if (preferenceRepository.set30SecForAuthTest == "30min") 30 * 1000 else 30 * 60 * 1000
        val delay12h = if (preferenceRepository.set30SecForAuthTest == "12h") 30 * 1000 else 12 * 60 * 60 * 1000

        val delayMillis = if (
            !preferenceRepository.authPinCode.isNullOrBlank() ||
            (preferenceRepository.isBiometricAllowed && applicationContext.isBiometricAvailable())
        ) {
            if (isForegrounded) {
                val inactiveTime = System.currentTimeMillis() - lastUserInteractionTime
                delay5min.toLong() - inactiveTime
            } else {
                delay1Min.toLong()
            }
        } else {
            val appWorkTime = System.currentTimeMillis() - preferenceRepository.continuousUsageNhsTime
            val appWorkTimeDelta = delay12h.toLong() - appWorkTime
            val inactiveTime = System.currentTimeMillis() - lastUserInteractionTime
            val inactiveTimeDelta = delay30min.toLong() - inactiveTime
            if (appWorkTimeDelta < inactiveTimeDelta) appWorkTimeDelta else inactiveTimeDelta
        }

        runnable = Runnable {
            checkAuthManager.invokeAuthScreen.value = true
            scheduleAuthScreen()
        }

        if (delayMillis <= 0) {
            handler.post(runnable!!)
            return
        }

        handler.postDelayed(runnable!!, delayMillis)
    }
}
