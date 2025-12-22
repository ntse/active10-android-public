package com.flipsidegroup.active10.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.work.WorkManager
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.OnboardingNotificationEnum
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.models.root.RootException
import com.flipsidegroup.active10.data.persistance.PlayIntegrityValidationRepository
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.home.activities.DEEPLINK_EVENT_SHARE
import com.flipsidegroup.active10.presentation.home.activities.DEEPLINK_EVENT_SHOW_WALKING_PLANS
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.home.activities.LOCAL_NOTIFICATION_NAV_INFO
import com.flipsidegroup.active10.presentation.home.activities.LOCAL_NOTIFICATION_TITLE_FOR_EVENT
import com.flipsidegroup.active10.presentation.home.adapters.MY_WALK_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.home.adapters.SETTINGS_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.onboarding.activities.IntroIntent
import com.flipsidegroup.active10.presentation.onboarding.activities.UserActivity
import com.flipsidegroup.active10.presentation.root.RootDetectionAlertDialog
import com.flipsidegroup.active10.presentation.splash.presenter.SplashPresenter
import com.flipsidegroup.active10.presentation.targets.activities.SetTargetMode
import com.flipsidegroup.active10.presentation.targets.activities.TargetIntent
import com.flipsidegroup.active10.presentation.userDetails.activities.UserDetailsActivityIntent
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.DeviceUtils
import com.flipsidegroup.active10.utils.OfflineRootDetectionHelper
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.worker.ApiWorker
import com.flipsidegroup.active10.utils.worker.WorkSchedulerHelper
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val SPLASH_DURATION = 4000L

@SuppressLint("CustomSplashScreen")
class SplashActivity : BasePublicActivity<BaseView>() {

    @Inject
    internal lateinit var presenter: SplashPresenter

    @Inject
    internal lateinit var playIntegrityValidationRepository: PlayIntegrityValidationRepository

    @Inject
    internal lateinit var analyticsHelper: FirebaseAnalyticsHelper

    @Inject
    internal lateinit var localNotificationRepository: LocalNotificationRepository

    private val handler = Handler(Looper.getMainLooper())

    override fun getPresenter(): LifecycleAwarePresenter<BaseView> = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        DeviceUtils.setDeviceIdIfNotExisting(settingsUtils)
        setMyWalksSavedTimestamp()
        if (!BuildConfig.DEBUG) {
            setUpNotificationsWorkersIfNeeded()
        }

        settingsUtils.updateSettings(SettingsDataHolder(useProdCMS = false))

        presenter.checkNhsLoginStatus()
        presenter.registerDevice()
        presenter.checkForGoalsBackwardsCompatibility()
        checkNhsLogBackPopupCondition()
    }

    override fun onResume() {
        super.onResume()

        try {
            OfflineRootDetectionHelper.checkRootedDeviceOffline(applicationContext)

            // Offline root detection passed, check it using integrity API
            playIntegrityValidationRepository.launchIntegrityCheck()

        } catch (e: RootException) {
            Timber.w("Offline root detection: ${e.message}")
            FirebaseCrashlytics.getInstance().log("Offline root detection: ${e.message}")
            RootDetectionAlertDialog().show(supportFragmentManager, "RootDetectionAlertDialog")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            WorkManager.getInstance(this@SplashActivity)
                .enqueue(ApiWorker.getRequest())

            goToNextScreen()
        }
    }

    private fun goToNextScreen() {
        settingsUtils.updateSettings(SettingsDataHolder(lastTimeInApp = DateHelper.getCurrentTimestamp()))
        val isFirstTimeInApp = settingsUtils.getSettingsHolder().firstTimeInApp
        val isNewUser = UIUtils.checkIfFileExists(Constants.MIGRATION_FILE_NAME)
        val navInfo = intent.getStringExtra(LOCAL_NOTIFICATION_NAV_INFO)
        intent.getStringExtra(LOCAL_NOTIFICATION_TITLE_FOR_EVENT)?.let {
            analyticsHelper.notificationTapped(it)
        }

        val intent = if (isFirstTimeInApp == null) {
            settingsUtils.updateSettings(SettingsDataHolder(shouldShowRewardOnboarding = false))
            IntroIntent(isNewUser = !isNewUser)
//            unlockSetupInitIntent(false)
        } else {
            val isOnboardingFinished = settingsUtils.getSettingsHolder().isOnboardingFinished
                ?: !settingsUtils.getSettingsHolder().goalsList.isNullOrEmpty()
            val areUserDetailsSet =
                settingsUtils.getSettingsHolder().areUserDetailsSet ?: false || isOnboardingFinished
            val shouldShowSetTarget = settingsUtils.getSettingsHolder().showSetTarget ?: true
            when {
                !isOnboardingFinished -> {
                    IntroIntent(isNewUser = true)
//                    unlockSetupInitIntent(false)
                }
                !(areUserDetailsSet) -> {
                    UserDetailsActivityIntent()
                }
                shouldShowSetTarget -> {
                    TargetIntent(SetTargetMode.ONBOARDING)
                }
                else -> {
                    when (intent?.data?.path) {
                        getString(R.string.deeplink_link_todays_walk_path) -> {
                            HomeActivity.getHomeIntent(this, navInfo = navInfo)
                        }
                        getString(R.string.deeplink_link_share_path) -> {
                            HomeActivity.getHomeIntent(this, navInfo = navInfo, screenPosition = SETTINGS_SCREEN_POSITION, deeplinkEvent = DEEPLINK_EVENT_SHARE)
                        }
                        getString(R.string.deeplink_link_my_walking_plans_path) -> {
                            HomeActivity.getHomeIntent(this, navInfo = navInfo, screenPosition = MY_WALK_SCREEN_POSITION, deeplinkEvent = DEEPLINK_EVENT_SHOW_WALKING_PLANS)
                        }
                        else -> {
                            HomeActivity.getHomeIntent(this, navInfo = navInfo)
                        }
                    }
                }
            }
        }

        handleIntentWithDelay(intent)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    private fun handleIntentWithDelay(intent: Intent) {
        runDelayed {
            startActivity(
                if (BuildConfig.DEBUG) {
                    UserActivity(intent)
//                    intent
                } else {
                    intent
                }
            )
            finish()
        }
    }

    private fun runDelayed(action: () -> (Unit)) {
        handler.postDelayed({ action.invoke() }, SPLASH_DURATION)
    }

    private fun setMyWalksSavedTimestamp() {
        val lastMyWalksSavedTimestamp = settingsUtils.getSettingsHolder().lastMyWalksSavedTimestamp
        if (lastMyWalksSavedTimestamp == null) {
            val currentTimeInMs = System.currentTimeMillis()
            settingsUtils.updateSettings(SettingsDataHolder(lastMyWalksSavedTimestamp = currentTimeInMs))
            settingsUtils.updateSettings(SettingsDataHolder(shouldCheckForLostData = false))
        }
    }

    private fun setUpNotificationsWorkersIfNeeded() {
        if (shouldScheduleNotifications()) {
            WorkSchedulerHelper.scheduleOnboardingNotification(
                OnboardingNotificationEnum.DAY_3.timeSinceInstallation, false
            )
            settingsUtils.updateSettings(SettingsDataHolder(lapsedCount = 0))
            settingsUtils.updateSettings(SettingsDataHolder(nextOnboardingNotification = 0))
        }
    }

    private fun shouldScheduleNotifications(): Boolean =
        settingsUtils.getSettingsHolder().nextOnboardingNotification == null ||
                settingsUtils.getSettingsHolder().lapsedCount == null

    private fun checkNhsLogBackPopupCondition() {
        if (!preferenceRepository.isUserLoggedIn &&
            preferenceRepository.showLogoutPopupAttemptsCount > 0) {
            preferenceRepository.showLogoutPopup = true
        }
    }

    companion object {
        private const val SKIP_ONLINE_ROOT_CHECK_FOR_DEBUG = true
    }
}
