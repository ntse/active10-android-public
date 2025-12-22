package com.flipsidegroup.active10.presentation.home.activities

import android.Manifest
import android.animation.Animator
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import com.afollestad.materialdialogs.MaterialDialog
import com.flipside.briskcounter.BriskCounter
import com.flipside.briskcounter.data.BriskActivity
import com.flipside.briskcounter.internal.ActivityListener
import com.flipside.briskcounter.internal.Error
import com.flipside.briskcounter.internal.InitListener
import com.flipside.briskcounter.internal.State
import com.flipside.briskcounter.internal.SubscriptionCheckListener
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.GlobalRules
import com.flipsidegroup.active10.data.LocalNotificationNavInfo
import com.flipsidegroup.active10.data.PeriodTypeEnum
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.databinding.ActivityHomeBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.activities.IN_NOTIFICATION_ANALYTICS
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.dialogs.CommonBottomSheetDialog
import com.flipsidegroup.active10.presentation.dialogs.DialogButtonModel
import com.flipsidegroup.active10.presentation.dialogs.HeroPopupDialog
import com.flipsidegroup.active10.presentation.dialogs.HighAchieversDialog
import com.flipsidegroup.active10.presentation.dialogs.HighAchieversRewardDialog
import com.flipsidegroup.active10.presentation.dialogs.IntroducingWidgetDialog
import com.flipsidegroup.active10.presentation.dialogs.NhsLogBackInDialog
import com.flipsidegroup.active10.presentation.discover.fragments.DiscoverFragment
import com.flipsidegroup.active10.presentation.discover_details.DiscoverDetailsIntent
import com.flipsidegroup.active10.presentation.home.adapters.BETTER_HEALTH_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.home.adapters.DISCOVER_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.home.adapters.HomeAdapter
import com.flipsidegroup.active10.presentation.home.adapters.MY_WALK_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.home.adapters.SETTINGS_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.home.adapters.TODAY_WALK_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.home.dialog.DeepLinkDialog
import com.flipsidegroup.active10.presentation.home.interfaces.RetrieveLostDataListener
import com.flipsidegroup.active10.presentation.home.presenter.HomePresenter
import com.flipsidegroup.active10.presentation.home.view.HomeView
import com.flipsidegroup.active10.presentation.mywalkingplans.activity.MyWalkingPlansIntent
import com.flipsidegroup.active10.presentation.mywalks.MyWalksFragment
import com.flipsidegroup.active10.presentation.mywalks.interfaces.MyWalkRetrieveDataListener
import com.flipsidegroup.active10.presentation.mywalks.interfaces.RetrieveDataInitListener
import com.flipsidegroup.active10.presentation.mywalks.interfaces.WalkDataChangedListener
import com.flipsidegroup.active10.presentation.settings.fragments.SettingsFragment
import com.flipsidegroup.active10.presentation.signIn.signInIntent
import com.flipsidegroup.active10.presentation.stayUpdated.getStayUpdatedIntent
import com.flipsidegroup.active10.presentation.targets.activities.SetTargetMode
import com.flipsidegroup.active10.presentation.targets.activities.TargetIntent
import com.flipsidegroup.active10.presentation.todaywalk.TodayWalkFragment
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.DrawOverlayInitListener
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.OverlayDrawListener
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.PermissionClickListener
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.StepDataInitListener
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.StepDataListener
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.TodayWalkOverlayListener
import com.flipsidegroup.active10.presentation.walkingplandetails.WalkingPlanDetailsIntent
import com.flipsidegroup.active10.presentation.walkingplanstatistics.WalkingPlanStatisticsIntent
import com.flipsidegroup.active10.presentation.walksnear.activity.WalksNearIntent
import com.flipsidegroup.active10.services.MigrationService
import com.flipsidegroup.active10.utils.AlertHelper
import com.flipsidegroup.active10.utils.ConfettiAnimatorListener
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.DialogUtils
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.NetworkStateReceiver
import com.flipsidegroup.active10.utils.RetrieveDataReceiver
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.hasInternetConnection
import com.flipsidegroup.active10.utils.hasPermissions
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.openURL
import com.flipsidegroup.active10.utils.showAllowingStateLoss
import com.flipsidegroup.active10.utils.worker.WorkSchedulerHelper
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.phe.betterhealth.components.moodbottomdialog.BHMoodBottomDialog
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


private const val OFFSET_PAGE_LIMIT = 4
const val IN_HOME_SCREEN_POSITION = "IN_HOME_SCREEN_POSITION"
const val IN_MY_WALK_TYPE = "IN_MY_WALK_TYPE"
const val IS_APP_IN_FOREGROUND = "IS_APP_IN_FOREGROUND"
const val MY_WALKS_UPDATED = "MY_WALKS_UPDATED"
const val LOCAL_NOTIFICATION_NAV_INFO = "local_notification_nav_info"
const val LOCAL_NOTIFICATION_TITLE_FOR_EVENT = "local_notification_title_for_event"

const val DEEPLINK_EVENT_PARAM = "deeplink_event"
const val DEEPLINK_EVENT_SHOW_WALKING_PLANS = "deeplink_show_walking_plans"
const val DEEPLINK_EVENT_SHARE = "deeplink_share"


private const val CONFETTI_ANIMATION_HEIGHT_RATION = 768f / 414f

private const val ACTIVITY_RECOGNITION_PERMISSION_REQUEST = 3

class HomeActivity : BaseSecureActivity<HomeView>(), PermissionClickListener, InitListener,
    StepDataInitListener, DrawOverlayInitListener, ActivityListener, HomeView, OverlayDrawListener,
    SubscriptionCheckListener, RetrieveDataInitListener, RetrieveLostDataListener,
    NetworkStateReceiver.InternetConnection, WalkDataChangedListener {

    @Inject
    internal lateinit var presenter: HomePresenter

    private var binding: ActivityHomeBinding by lifecycleAwareVariable()

    @Inject
    internal lateinit var dialogUtils: DialogUtils

    @Inject
    internal lateinit var localNotificationRepository: LocalNotificationRepository

    lateinit var reviewInfo: ReviewInfo
    lateinit var reviewManager: ReviewManager

    private var tipsOverlayListener: TodayWalkOverlayListener? = null
    private var myWalkRetrieveDataListener: MyWalkRetrieveDataListener? = null
    private var stepDataListeners = ArrayList<StepDataListener>()
    private var homeAdapter: HomeAdapter? = null
    private val handler = Handler()
    private var needToShowTips = false
    private var isMissingDataDialogVisible = false

    private var resultReceiver: RetrieveDataReceiver? = null
    private var receiver: NetworkStateReceiver? = null

    private var todayActivityObserver: Disposable = Disposables.empty()

    private val myWalksFilter = IntentFilter(MY_WALKS_UPDATED)

    private val myWalksBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            intent ?: return
            presenter.checkHighAchieversForShowing()
            presenter.syncLastActivitiesAndRewardsToNhs()
        }
    }

    companion object {
        fun getHomeIntent(
            context: Context,
            isForeground: Boolean? = null,
            screenPosition: Int = TODAY_WALK_SCREEN_POSITION,
            myWalkType: PeriodTypeEnum = PeriodTypeEnum.DAYS,
            event: String? = null,
            navInfo: String? = null,
            deeplinkEvent: String? = null
        ): Intent {
            return Intent(context, HomeActivity::class.java).apply {
                putExtra(IN_HOME_SCREEN_POSITION, screenPosition)
                putExtra(IN_MY_WALK_TYPE, myWalkType)
                putExtra(IN_NOTIFICATION_ANALYTICS, event)
                putExtra(IS_APP_IN_FOREGROUND, isForeground)
                putExtra(LOCAL_NOTIFICATION_NAV_INFO, navInfo)
                deeplinkEvent?.let { putExtra(DEEPLINK_EVENT_PARAM, it) }
                if (event != UIUtils.getString(R.string.walkin_reminder_tapped_event)) {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
        }
    }

    override fun getPresenter(): LifecycleAwarePresenter<HomeView> = presenter

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        receiver = NetworkStateReceiver(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(ActivityHomeBinding.inflate(layoutInflater).apply { binding = this }.root)
        preferenceRepository.isOnboardingCompleted = true
        resultReceiver = RetrieveDataReceiver(Handler(), this)

        val isFirstTimeInApp = settingsUtils.getSettingsHolder().firstTimeInApp
        if (isFirstTimeInApp == null) {
            settingsUtils.updateSettings(SettingsDataHolder(firstTimeInApp = true))
        }

        checkForActivityPermission()
        checkIsLoginFlowCompleted()

        setUpViews()
        if (hasInternetConnection()) {
            if (settingsUtils.getSettingsHolder().isFitnessMotionEnabled == true) {
                presenter.retrieveLostDataFrom5thJanuary(resultReceiver)
            }
        } else {
            AlertHelper.showAlerter(
                this,
                UIUtils.getString(R.string.no_internet_connection),
                UIUtils.getString(R.string.dismiss)
            )
        }

        presenter.getMyWalksData()
        presenter.getGlobalRules()
        presenter.checkForActivitiesBadges()

        checkNavigation(intent)
    }

    override fun checkForShowingNhsLogBackInDialog() {
        lifecycleScope.launch {
            lifecycle.withResumed {
                val dialogTag = NhsLogBackInDialog::class.java.simpleName
                if (preferenceRepository.showLogoutPopupAttemptsCount > 0 &&
                    preferenceRepository.showLogoutPopup &&
                    !preferenceRepository.isUserLoggedIn &&
                    supportFragmentManager.findFragmentByTag(dialogTag) == null
                ) {
                    preferenceRepository.showLogoutPopupAttemptsCount -= 1
                    preferenceRepository.showLogoutPopup = false
                    NhsLogBackInDialog().show(
                        supportFragmentManager,
                        NhsLogBackInDialog::class.java.simpleName
                    )
                }
            }
        }
    }

    var highAchieversDialog: HighAchieversDialog? = null
    var rewardsDialog: HighAchieversRewardDialog? = null

    override fun showMoodBottomDialog(moodBottomDialog: BHMoodBottomDialog) {
        moodBottomDialog.showDialog(supportFragmentManager)
    }

    override fun showHighAchieversDialog(showDontAskMeAgain: Boolean) {
        if (supportFragmentManager.findFragmentByTag("high_achiever_dialog") != null) {
            return
        }

        if (highAchieversDialog != null || rewardsDialog != null) return

        val dialog = HighAchieversDialog().apply {
            onDismissCallback = {
                highAchieversDialog = null
            }
        }

        dialog.onIncreaseNowClicked = {
            presenter.highAchieversUpdateLastShowDate()
            startActivity(TargetIntent(SetTargetMode.HIGH_ACHIEVERS))
        }

        dialog.onMaybeLaterClicked = {
            presenter.highAchieversUpdateLastShowDate()
        }

        dialog.onDoNotAskMeAgainCliched = if (showDontAskMeAgain) {
            { presenter.highAchieversDontAskMeGain() }
        } else {
            null
        }

        highAchieversDialog = dialog

        dialog.showAllowingStateLoss(supportFragmentManager, "high_achiever_dialog")
    }

    override fun showRewardDialog(rewardBadge: RewardBadge, onDismiss: () -> Unit) {
        if (rewardsDialog != null) return

        val dialog = HighAchieversRewardDialog(rewardBadge, onDismiss = {
            onDismiss()
            rewardsDialog = null
        })
        rewardsDialog = dialog
        dialog.showAllowingStateLoss(supportFragmentManager, null)
    }

    override fun onStop() {
        super.onStop()

        todayActivityObserver.dispose()
    }

    override fun onConnectionChanged(isConnected: Boolean) {
        if (!isConnected) {
            AlertHelper.showAlerter(
                this,
                UIUtils.getString(R.string.no_internet_connection),
                UIUtils.getString(R.string.dismiss)
            )
        } else if (settingsUtils.getSettingsHolder().isFitnessMotionEnabled == true) {
            presenter.retrieveLostDataFrom5thJanuary(resultReceiver)
        }
    }

    private fun checkForActivityPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val hasActivityPermission = hasPermissions(Manifest.permission.ACTIVITY_RECOGNITION)
            val hadPermission = settingsUtils.getSettingsHolder().isFitnessMotionEnabled ?: false
            if (!hasActivityPermission && hadPermission) {
                settingsUtils.updateSettings(SettingsDataHolder(isFitnessMotionEnabled = false))
            }
        }
    }

    override fun initStepDataListener(stepDataListener: StepDataListener) {
        stepDataListeners.add(stepDataListener)
    }

    override fun removeStepDataListener(stepDataListener: StepDataListener) {
        stepDataListeners.remove(stepDataListener)
    }

    override fun initDrawOverlayListener(tipsOverlayListener: TodayWalkOverlayListener) {
        this.tipsOverlayListener = tipsOverlayListener
    }

    override fun initRetrieveDataListener(listener: MyWalkRetrieveDataListener) {
        this.myWalkRetrieveDataListener = listener
    }

    override fun removeRetrieveDataListener() {
        this.myWalkRetrieveDataListener = null
    }

    override fun onSuccess(briskActivity: BriskActivity) {
        stepDataListeners.forEach {
            it.onStepDataReceived(briskActivity)
        }
        presenter.checkForActivitiesBadges(briskActivity)
    }

    override fun onFailure(activityError: String) {
        if (activityError.contains(ApiException::class.java.name)) {
            if (activityError.contains(CommonStatusCodes.SIGN_IN_REQUIRED.toString())) {
                settingsUtils.updateSettings(SettingsDataHolder(isFitnessMotionEnabled = false))
                homeAdapter?.updatePermission(false)
            }
        }

        Timber.w(activityError)
        FirebaseCrashlytics.getInstance()
            .log(Constants.FirebaseAnalytics.RETRIEVE_TODAY_ACTIVITY_FAILURE)

        AlertHelper.showErrorToast(
            getString(R.string.brisk_counter_failure_user_message),
            Toast.LENGTH_LONG
        )
        stepDataListeners.forEach {
            it.onStepDataError(getString(R.string.brisk_counter_failure_user_message))
        }
    }

    override fun onButtonClicked() {
        if (hasInternetConnection()) {
            dialogUtils.showFitnessDialog(this)
        } else {
            showNoInternetDialog()
        }
    }

    override fun onFailure(error: Error) {
        FirebaseCrashlytics.getInstance().log(error.name)
        FirebaseCrashlytics.getInstance()
            .recordException(RuntimeException(Constants.FirebaseAnalytics.INITIALIZE_BRISK_COUNTER_FAILURE))
        AlertHelper.showErrorToast(
            getString(R.string.brisk_counter_failure_user_message),
            Toast.LENGTH_LONG
        )
        stepDataListeners.forEach {
            it.onStepDataError(getString(R.string.brisk_counter_failure_user_message))
        }
    }

    override fun onSuccess(state: State) {
        settingsUtils.updateSettings(SettingsDataHolder(isFitnessMotionEnabled = true))

        startMigrationService()

        homeAdapter?.updatePermission(true)
        presenter.getMyWalksData()
        presenter.recoverWalksData()
        presenter.checkForActivitiesBadges()
    }

    fun removeIsFitnessMotionEnabledPermission() {
        settingsUtils.updateSettings(SettingsDataHolder(isFitnessMotionEnabled = false))

        homeAdapter?.updatePermission(false)
        binding.contentVP.setCurrentItem(0, false)
        binding.bottomNavigationBNV.selectedItemId = R.id.action_today_walk
    }

    override fun isNotSubscribed() {
        BriskCounter.resubscribeToGoogleFit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_RECOGNITION_PERMISSION_REQUEST) {
            if (hasPermissions(Manifest.permission.ACTIVITY_RECOGNITION)) {
                BriskCounter.initialize(this@HomeActivity as InitListener)
            }
        } else {
            BriskCounter.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun isPlayStoreInstalled(): Boolean {
        return try {
            this.packageManager.getPackageInfo(GooglePlayServicesUtil.GOOGLE_PLAY_STORE_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun showReviewDialog() {
        if (isPlayStoreInstalled()) {
            activateReviewInfo()
        } else {
            MaterialDialog(this).show {
                title(R.string.review_dialog_message)
                message(R.string.review_dialog_message)
                positiveButton(R.string.review_dialog_leave_review) {
                    val appId = BuildConfig.APPLICATION_ID

                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=$appId")
                        )
                    )
                    settingsUtils.updateSettings(SettingsDataHolder(hasLeftReview = true))
                }
                negativeButton(R.string.review_dialog_not_now) {}
            }
        }
    }

    override fun showRetrieveDataDialog() {
        isMissingDataDialogVisible = true
        CommonBottomSheetDialog(
            title = R.string.dialog_lost_data_title,
            subtitle = R.string.dialog_lost_data_text,
            primaryButton = DialogButtonModel(R.string.button_ok),
            onDismiss = {
                isMissingDataDialogVisible = false
                showDateRetrievedAlerterIfNeeded()
                showDateRetrievedErrorAlerterIfNeeded()
            }
        ).show(supportFragmentManager, null)
    }

    override fun showIntroduceWidgetDialog() {
        val widgetDialog = IntroducingWidgetDialog()
        widgetDialog.onContinue = this::continueFromIntroduceWidgetDialog
        widgetDialog.show(supportFragmentManager, null)
    }

    override fun showDeepLinkDialog() {
        DeepLinkDialog().show(supportFragmentManager, null)
    }

    override fun showHeroPopupDialog(content: ScreenContent) {
        HeroPopupDialog(content, presenter::continueFromHeroPopup, firebaseAnalyticsHelper)
            .show(supportFragmentManager, null)
    }

    private fun continueFromIntroduceWidgetDialog() {
        presenter.continueFromIntroduceWidgetDialog()
    }

    override fun goToArticle(infoPage: InfoPage, source: String) {
        startActivity(
            DiscoverDetailsIntent(infoPage.id, infoPage.title, source)
        )
    }

    override fun navigateToUrl(url: String?) {
        url?.also { openURL(it) }
    }

    override fun onWalkDataChanged() {
        Timber.d("onWalkDataChanged")
        presenter.checkForActivitiesBadges()
        todayActivityObserver = BriskCounter.retrieveTodayActivity(this, this)
        presenter.syncLastActivitiesAndRewardsToNhs()
    }

    override fun onGlobalRulesRetrieved(globalRules: GlobalRules?) {
        dialogUtils.showTermAndConditionsDialog(this, globalRules)
        dialogUtils.showUpdateAppDialog(this, globalRules)
    }

    override fun startConfettiAnimation(progress: Int, trophies: Int) {
        binding.confettiIV.visibility = View.VISIBLE

        binding.confettiIV.playAnimation()
        binding.confettiIV.addAnimatorListener(object : ConfettiAnimatorListener() {
            override fun onAnimationEnd(animation: Animator) {
                binding.confettiIV.removeAnimatorListener(this)
                binding.confettiIV.visibility = View.GONE
                tipsOverlayListener?.updateCurrentProgress(progress, trophies)
            }
        })
    }

    override fun onLostDataReceived() {
        if (!isMissingDataDialogVisible) {
            showDateRetrievedAlerterIfNeeded()
        }
    }

    override fun onLostDataError() {
        if (!isMissingDataDialogVisible) {
            showDateRetrievedErrorAlerterIfNeeded()
        }
    }

    private fun showDateRetrievedAlerterIfNeeded() {
        val hadLostData = settingsUtils.getSettingsHolder().hadLostData
        if (settingsUtils.getSettingsHolder().shouldCheckForLostData == true &&
            hadLostData != null && !isMissingDataDialogVisible
        ) {
            AlertHelper.showAlerter(
                this,
                UIUtils.getString(R.string.update_data_completed),
                UIUtils.getString(R.string.dismiss)
            )
            settingsUtils.updateSettings(SettingsDataHolder(shouldCheckForLostData = false))
            myWalkRetrieveDataListener?.onLostDataRetrieved()
        }
    }

    private fun showDateRetrievedErrorAlerterIfNeeded() {
        val shouldSeeAlerter = settingsUtils.getSettingsHolder().shouldSeeRetrieveErrorAlerted
        if (shouldSeeAlerter == true && !isMissingDataDialogVisible) {
            UIUtils.getString(R.string.date_restore_error)
            AlertHelper.showAlerter(
                this,
                UIUtils.getString(R.string.date_restore_error),
                UIUtils.getString(R.string.dismiss)
            )
            myWalkRetrieveDataListener?.onLostDataRetrieved()
            settingsUtils.updateSettings(SettingsDataHolder(shouldSeeRetrieveErrorAlerted = false))
        }
    }

    private fun setUpViews() {
        val screenPosition = if (intent == null) {
            TODAY_WALK_SCREEN_POSITION
        } else {
            intent.getIntExtra(IN_HOME_SCREEN_POSITION, TODAY_WALK_SCREEN_POSITION)
        }
        val myWalkType = if (intent == null) {
            PeriodTypeEnum.DAYS
        } else {
            (intent.getSerializableExtra(IN_MY_WALK_TYPE) ?: PeriodTypeEnum.DAYS) as PeriodTypeEnum
        }

        homeAdapter = HomeAdapter(
            fragmentManager = supportFragmentManager,
            isFitnessMotionEnabled = settingsUtils.getSettingsHolder().isFitnessMotionEnabled,
            periodType = myWalkType
        )

        binding.contentVP.offscreenPageLimit = OFFSET_PAGE_LIMIT
        binding.contentVP.adapter = homeAdapter

        handleBottomBarNavigation()
        setScreenPosition(screenPosition)

        setUpConfettiView()

        if (settingsUtils.getSettingsHolder().shouldShowRewardOnboarding != false) {
            dialogUtils.showNewRewardBadgesDialog(this)
        }
    }

    private fun setUpConfettiView() {
        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height: Int = (width * CONFETTI_ANIMATION_HEIGHT_RATION).toInt()

        val params = binding.confettiIV.layoutParams as RelativeLayout.LayoutParams
        params.width = width
        params.height = height
        binding.confettiIV.layoutParams = params
    }

    private fun startMigrationService() {
        val isDeviceRegistered = settingsUtils.getSettingsHolder().isDeviceRegistered
        if (isDeviceRegistered != true) {
            return
        }

        val isMigrationDataSent = settingsUtils.getSettingsHolder().isMigrationDataSent
        if (isMigrationDataSent == true) {
            return
        }

        MigrationService.enqueueWork(this, MigrationService())
    }

    fun setScreenPosition(screenPos: Int) {
        binding.contentVP.setCurrentItem(screenPos, false)
        binding.bottomNavigationBNV.selectedItemId = when (screenPos) {
            TODAY_WALK_SCREEN_POSITION -> R.id.action_today_walk
            MY_WALK_SCREEN_POSITION -> R.id.action_my_walks
            DISCOVER_SCREEN_POSITION, BETTER_HEALTH_SCREEN_POSITION -> R.id.action_discover
            SETTINGS_SCREEN_POSITION -> R.id.action_settings
            else -> R.id.action_today_walk
        }

        binding.bottomNavigationBNV.menu.forEach {
            androidx.core.view.MenuItemCompat.setContentDescription(
                it,
                getString(R.string.menu_item_content_description, it.title.toString())
            )
        }
    }

    fun navigateToDiscoverHealth() {
        setScreenPosition(DISCOVER_SCREEN_POSITION)
        supportFragmentManager.fragments.find { it is DiscoverFragment }?.let {
            (it as DiscoverFragment).showMentalHealthTab()
        }
    }

    fun navigateToBetterHealthTab() {
        setScreenPosition(DISCOVER_SCREEN_POSITION)
        supportFragmentManager.fragments.find { it is DiscoverFragment }?.let {
            (it as DiscoverFragment).showBetterHealthTab()
        }
    }

    fun navigateToDiscover(tab: Int?, slug: String) {
        tab ?: return

        setScreenPosition(DISCOVER_SCREEN_POSITION)
        supportFragmentManager.fragments.find { it is DiscoverFragment }?.let {
            (it as DiscoverFragment).showTab(tab, slug)
        }
    }

    fun navigateToMyWalks() {
        setScreenPosition(MY_WALK_SCREEN_POSITION)
    }

    private fun handleBottomBarNavigation() {
        binding.bottomNavigationBNV.setOnNavigationItemSelectedListener {
            if (needToShowTips) {
                return@setOnNavigationItemSelectedListener false
            }
            val previousTabName = tabNameForAnalytics(binding.contentVP.currentItem)
            when (it.itemId) {
                R.id.action_today_walk -> {
                    binding.contentVP.currentItem
                    binding.contentVP.setCurrentItem(0, false)
                }

                R.id.action_my_walks -> binding.contentVP.setCurrentItem(1, false)
                R.id.action_discover -> binding.contentVP.setCurrentItem(2, false)
                R.id.action_settings -> binding.contentVP.setCurrentItem(3, false)
            }
            val currentTabName = tabNameForAnalytics(binding.contentVP.currentItem)
            if (currentTabName != null && previousTabName != null) {
                firebaseAnalyticsHelper.sendViewScreenEvent(
                    screenClass = currentTabName,
                    previousClass = previousTabName
                )
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun tabNameForAnalytics(position: Int) = when (position) {
        TODAY_WALK_SCREEN_POSITION -> TodayWalkFragment::class.java.simpleName
        MY_WALK_SCREEN_POSITION -> MyWalksFragment::class.java.simpleName
        DISCOVER_SCREEN_POSITION -> "DiscoverView"
        SETTINGS_SCREEN_POSITION -> SettingsFragment::class.java.simpleName
        else -> null
    }

    private fun showNoInternetDialog() {
        MaterialDialog(this).show {
            title(R.string.dialog_no_internet_title)
            message(R.string.dialog_no_internet_subtitle)
            positiveButton(R.string.button_ok)
        }
    }

    override fun onResume() {
        super.onResume()

        ContextCompat.registerReceiver(
            this,
            myWalksBroadcastReceiver,
            myWalksFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        checkIsLoginFlowCompleted()
        if (preferenceRepository.logoutNhsUserAuthFlag) {
            presenter.nhsUserLogout()
            preferenceRepository.logoutNhsUserAuthFlag = false
        }

        if (isReminderIntentAndAppInForeground()) {
            setUpViews()
            presenter.getMyWalksData()
            presenter.getGlobalRules()
            presenter.checkForActivitiesBadges()
        }
        BriskCounter.addInitListener(this)
        if (settingsUtils.getSettingsHolder().isFitnessMotionEnabled == true) {
            BriskCounter.checkForSubscriptions(this)
        }
        val isNotificationMin = settingsUtils.getSettingsHolder().notificationMin ?: false
        if (!isNotificationMin) {
            settingsUtils.updateSettings(SettingsDataHolder(lastTimeInApp = DateHelper.getCurrentTimestamp()))
            WorkSchedulerHelper.startLapsedNotificationIfNeeded(
                localRepository,
                settingsUtils,
                isNotificationMin
            )
        }

        checkNoAccountUserNotification()

        presenter.recoverWalksData()
        showDateRetrievedAlerterIfNeeded()
        showDateRetrievedErrorAlerterIfNeeded()

        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        ContextCompat.registerReceiver(
            this,
            receiver,
            intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )

        presenter.checkReviewForShowing()
        presenter.checkForShowingHeroPopup()
        presenter.checkForShowingDeepLinkDialog()
        presenter.syncLastActivitiesAndRewardsToNhs()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
        unregisterReceiver(receiver)
        unregisterReceiver(myWalksBroadcastReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        BriskCounter.removeInitListener()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_PERMISSION_REQUEST) {
            val isPermissionGranted =
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (isPermissionGranted) {
                BriskCounter.initialize(this@HomeActivity as InitListener)
            }
        }
    }

    private fun checkIsLoginFlowCompleted() {
        if (!preferenceRepository.isUserLoggedIn) {
            settingsUtils.saveSettingsHolder(
                settingsUtils.getSettingsHolder().copy(
                    nhsToken = null,
                    nhsUser = null
                )
            )
        }
    }

    private fun isReminderIntentAndAppInForeground(): Boolean {
        var isForeground: Boolean? = null
        var intentEvent: String? = null
        if (intent != null
            && intent.hasExtra(IS_APP_IN_FOREGROUND)
            && intent.hasExtra(IN_NOTIFICATION_ANALYTICS)
        ) {
            isForeground = intent.getBooleanExtra(IS_APP_IN_FOREGROUND, false)
            intentEvent = intent.getStringExtra(IN_NOTIFICATION_ANALYTICS)
        }
        return isForeground != null && isForeground && intentEvent == UIUtils.getString(R.string.walkin_reminder_tapped_event)
    }

    private fun activateReviewInfo() {
        reviewManager = ReviewManagerFactory.create(this)
        val reviewInfoTask = reviewManager.requestReviewFlow()
        reviewInfoTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
                startReviewFlow()
            } else {
                Timber.tag("In-app review task").d(task.exception)
            }
        }
    }

    private fun startReviewFlow() {
        reviewManager.launchReviewFlow(this, reviewInfo)
    }

    private fun checkNoAccountUserNotification() {
        with(preferenceRepository) {
            if (!isUserLoggedIn && !isUserFirstTimeInAppNotified) {
                localNotificationRepository.setNoAccountUser()
                isUserFirstTimeInAppNotified = true
            }
        }
    }

    private fun checkNavigation(intent: Intent?) {
        intent ?: return
        Gson().fromJson(
            intent.getStringExtra(LOCAL_NOTIFICATION_NAV_INFO),
            LocalNotificationNavInfo::class.java
        )?.let { info ->
            val stackOfActivities = checkClassicUserNavigation(info) ?: checkNhsUserNavigation(info)
            stackOfActivities?.let {
                PendingIntent.getActivities(
                    this,
                    0,
                    it,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                ).send()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.let {
            val screenPosition = it.getIntExtra(IN_HOME_SCREEN_POSITION, TODAY_WALK_SCREEN_POSITION)
            if (screenPosition == BETTER_HEALTH_SCREEN_POSITION) {
                setIntent(intent)
                setScreenPosition(screenPosition)
                navigateToBetterHealthTab()
            }
        }
    }

    private fun checkClassicUserNavigation(info: LocalNotificationNavInfo): Array<Intent>? {
        return when (info.destination) {
            "settings_create_account" -> {
                setScreenPosition(SETTINGS_SCREEN_POSITION)
                arrayOf(signInIntent(FlowType.SETTINGS))
            }

            else -> null
        }
    }

    private fun checkNhsUserNavigation(info: LocalNotificationNavInfo): Array<Intent>? {
        if (!preferenceRepository.isUserLoggedIn) return null

        return when (info.destination) {
            "settings_email_pref" -> {
                setScreenPosition(SETTINGS_SCREEN_POSITION)
                arrayOf(getStayUpdatedIntent(FlowType.SETTINGS))
            }

            "walking_plans_overview" -> {
                setScreenPosition(MY_WALK_SCREEN_POSITION)
                arrayOf(MyWalkingPlansIntent())
            }

            "walking_plan_panel" -> {
                setScreenPosition(SETTINGS_SCREEN_POSITION)
                arrayOf(
                    MyWalkingPlansIntent(),
                    WalkingPlanDetailsIntent(info.planId),
                    WalkingPlanStatisticsIntent(info.planId)
                )
            }

            "walks_near_me" -> {
                setScreenPosition(MY_WALK_SCREEN_POSITION)
                arrayOf(WalksNearIntent())
            }

            else -> null
        }
    }

}
