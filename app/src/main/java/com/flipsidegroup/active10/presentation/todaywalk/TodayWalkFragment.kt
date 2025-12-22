package com.flipsidegroup.active10.presentation.todaywalk

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flipside.briskcounter.BriskCounter
import com.flipside.briskcounter.data.BriskActivity
import com.flipside.briskcounter.internal.ActivityListener
import com.flipside.briskcounter.internal.SensorStepListener
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.EarnRewardBadge
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.databinding.FragmentTodayWalkBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.dialogs.NhsLogBackInDialog
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.howitworks.activity.HowItWorksIntent
import com.flipsidegroup.active10.presentation.information.InformationIntent
import com.flipsidegroup.active10.presentation.pacechecker.getPaceCheckerIntent
import com.flipsidegroup.active10.presentation.pacechecker.intro.PaceCheckerIntroDialog
import com.flipsidegroup.active10.presentation.reward.getRewardIntent
import com.flipsidegroup.active10.presentation.targets.activities.DEFAULT_TARGET
import com.flipsidegroup.active10.presentation.todaywalk.adapters.TrophyAdapter
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.DrawOverlayInitListener
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.OverlayDrawListener
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.StepDataInitListener
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.StepDataListener
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.TodayWalkOverlayListener
import com.flipsidegroup.active10.presentation.todaywalk.presenter.TodayWalkPresenter
import com.flipsidegroup.active10.presentation.todaywalk.view.TodayWalkView
import com.flipsidegroup.active10.services.MigrationService
import com.flipsidegroup.active10.services.widget.MediumWidget
import com.flipsidegroup.active10.services.widget.SmallWidget
import com.flipsidegroup.active10.utils.AlertHelper
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.MEDIUM
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.MEDIUM_DIFF
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.MEDIUM_WIDGETS
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.SMALL
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.SMALL_DIFF
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.SMALL_WIDGETS
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.WIDGETS
import com.flipsidegroup.active10.utils.Constants.FirebaseAnalytics.WIDGET_INSTALLED_KEY
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.DeviceUtils
import com.flipsidegroup.active10.utils.EarnBadgeHelper
import com.flipsidegroup.active10.utils.MarginItemDecoration
import com.flipsidegroup.active10.utils.TargetHelper
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.loadFromUrl
import com.flipsidegroup.active10.utils.playAnimationIfEnabled
import com.flipsidegroup.active10.utils.setHeading
import com.flipsidegroup.active10.utils.setOnClickListenerWithDebounce
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.phe.betterhealth.widgets.utils.dpToPx
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.overlay.BalloonOverlayRoundRect
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.view.ViewTreeObserver.OnGlobalLayoutListener as OnGlobalLayoutListener1

private const val GOOGLE_FIT_DELAY_MILLIS = 30000L
private const val GOOGLE_FIT_INITIAL_DELAY = 0L
private const val SENSOR_DELAY_MILLIS = 11000L

private const val TARGET_REQUEST_CODE = 31

class TodayWalkFragment : BaseFragment<TodayWalkView>(), StepDataListener, SensorStepListener,
    TodayWalkOverlayListener, TodayWalkView {

    companion object {
        fun newInstance() = TodayWalkFragment()
    }

    @Inject
    internal lateinit var presenter: TodayWalkPresenter

    private var binding: FragmentTodayWalkBinding by lifecycleAwareVariable()

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    private val sensorHandler = Handler(Looper.getMainLooper())

    private var initCalendar = Calendar.getInstance()

    private var trophyAdapter: TrophyAdapter? = null

    private var isActivityJustCreated = true
    private var shouldAnimateProgress = true
    private var currentActiveTen = 0
    private var totalBriskMin = 0
    private var totalWalkMin = 0
    private var isFirstTime = true

    private var tipsDrawListener: OverlayDrawListener? = null

    private var headerMessage: String? = null

    private var rewards: List<RewardBadge>? = null
    private var currentReward: RewardBadge? = null

    private var todayActivityObserver: Disposable = Disposables.empty()
    private var shouldStopRetrievingTodayWalk = false

    override fun getPresenter(): LifecycleAwarePresenter<TodayWalkView> = presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is StepDataInitListener) {
            context.initStepDataListener(this)
        }

        if (context is DrawOverlayInitListener) {
            context.initDrawOverlayListener(this)
        }

        if (context is OverlayDrawListener) {
            tipsDrawListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()

        if (context is StepDataInitListener) {
            (context as StepDataInitListener).removeStepDataListener(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_today_walk, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTodayWalkBinding.bind(view)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        TargetHelper.initTargetIfEmpty(settingsUtils)

        presenter.getRewardBadges()

        presenter.checkPaceCheckerIntro()
        presenter.checkMentalHealthBanner()
        presenter.checkCampaignBanner()
        presenter.checkMyWalksNewFeaturesBanner()

        setUpToolbar()
        initCalendar()
        setUpViews()
        setUpGoogleFitRunnable()
        startMigrationService()
        setUpBriskAndTotalLabels(0, 0)
        handleTargets()
        prepareWidgetAnalyticsLogs()
        initializePaceCheckerTooltip()
        initializePaceCheckerBanner()
    }

    override fun showMentalHealthBanner(screenContent: ScreenContent?) {
        binding.todayWalkBanner.isVisible = screenContent != null
        if (screenContent == null) return

        val endDate = screenContent.getPropertyValue("end_date")?.let {
            LocalDate.parse(
                it,
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            )
        }
        val startDate = screenContent.getPropertyValue("start_date")?.let {
            LocalDate.parse(
                screenContent.getPropertyValue("start_date"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
            )
        }

        if (startDate != null && endDate != null && LocalDate.now() in startDate..endDate) {
            binding.todayWalkBanner.isVisible = true
            binding.todayWalkBanner.infoText = screenContent.title
            binding.todayWalkBanner.subInfoText = screenContent.description
            binding.todayWalkBanner.imageSrc = Uri
                .parse("android.resource://${BuildConfig.APPLICATION_ID}/" + R.drawable.ic_mental_banner)
                .toString()

            binding.todayWalkBanner.setOnClickListenerWithDebounce {
                firebaseAnalyticsHelper.mentalHealthBannerClicked()
                (requireActivity() as? HomeActivity?)?.navigateToDiscoverHealth()
            }
        } else {
            binding.todayWalkBanner.isVisible = false
        }
    }

    override fun showCampaignBanner(screenContent: ScreenContent?) {
        if (screenContent == null) return

        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val campaignBannerVersion = screenContent.getPropertyValue("version").orEmpty()

        val endDate = screenContent.getPropertyValue("end_date")?.let {
            runCatching { LocalDate.parse(it, dateTimeFormatter) }.getOrNull()
        }
        val startDate = screenContent.getPropertyValue("start_date")?.let {
            runCatching { LocalDate.parse(it, dateTimeFormatter) }.getOrNull()
        }

        val bannerId = "${screenContent.slug}$campaignBannerVersion"
        val shouldDisplayPopup = presenter.shouldDisplayCampaignBanner(bannerId)

        if (shouldDisplayPopup && startDate != null && endDate != null && LocalDate.now() in startDate..endDate) {
            binding.todayWalkBanner.isVisible = true
            binding.todayWalkBanner.infoText = screenContent.title
            binding.todayWalkBanner.subInfoText = screenContent.description
            binding.todayWalkBanner.imageSrc = screenContent.media.firstOrNull()?.url.orEmpty()

            binding.todayWalkBanner.setOnClickListenerWithDebounce {
                (requireActivity() as? HomeActivity?)?.navigateToDiscover(
                    tab = screenContent.getPropertyValue("menu_position")?.toIntOrNull(),
                    slug = screenContent.getPropertyValue("article_slug").orEmpty()
                )

                binding.todayWalkBanner.isVisible = false
                presenter.markCampaignBannerAsSeen(bannerId)
            }
        }
    }

    override fun introduceCoachApp() {
        startActivity(requireContext().InformationIntent(Constants.CoachAppIntroduction.POPUP_SMASHING_IT))
    }

    private fun initializePaceCheckerBanner() {
        binding.cardHeaderPaceCheckerClose.setOnClickListener {
            firebaseAnalyticsHelper.paceCheckerPopupDismissed()
            preferenceRepository.isPaceCheckerBannerDismissed = true
            binding.cardHeaderPaceCheckerContainer.isVisible = false
            binding.cardHeaderContainer.isVisible = true
            setupBadgeNotification()
        }

        binding.cardHeaderPaceCheckerContainer.setOnClickListener {
            firebaseAnalyticsHelper.paceCheckerPopupDismissed()
            preferenceRepository.isPaceCheckerBannerDismissed = true
            binding.cardHeaderPaceCheckerContainer.isVisible = false
            binding.cardHeaderContainer.isVisible = true
            setupBadgeNotification()

            binding.todayWalkScrollView.post {
                binding.todayWalkScrollView.smoothScrollToAndNotify(binding.todayWalkPaceCheckerContainer.top) {
                    binding.todayWalkPaceCheckerButtonContainer.performClick()
                }
            }
        }

        setupBadgeNotification()
    }

    private fun initializePaceCheckerTooltip() {
        val balloon = Balloon.Builder(requireContext())
            .setLayout(R.layout.layout_pace_checker_tooltip)
            .setWidthRatio(0.8f)
            .setLifecycleOwner(viewLifecycleOwner)
            .setCornerRadius(10f)
            .setArrowColorResource(R.color.yellow_trophy)
            .setIsVisibleOverlay(true)
            .setArrowPosition(0.2f)
            .setOverlayColor(Color.parseColor("#78000000"))
            .setOverlayShape(
                BalloonOverlayRoundRect(
                    10f.dpToPx(requireContext()),
                    10f.dpToPx(requireContext())
                )
            )
            .build()

        binding.todayWalkPaceCheckerButtonContainer.setOnClickListener {
            if (preferenceRepository.isPaceCheckerTooltipShown) {
                startActivity(requireContext().getPaceCheckerIntent())
            } else {
                preferenceRepository.isPaceCheckerTooltipShown = true
                balloon.showAlignTop(
                    anchor = binding.todayWalkPaceCheckerContentContainer,
                    xOff = -60,
                    yOff = -(16.dpToPx(requireContext()).toInt())
                )
            }
        }

        balloon.getContentView().findViewById<View>(R.id.pace_checker_tooltip_close)
            .setOnClickListener {
                balloon.dismiss()
            }
    }

    private fun prepareWidgetAnalyticsLogs() {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val widgetsPrefs =
            requireContext().getSharedPreferences(WIDGETS, Context.MODE_PRIVATE)
        val prevSmallValue = widgetsPrefs.getInt(SMALL_WIDGETS, 0)
        val prevMediumValue = widgetsPrefs.getInt(MEDIUM_WIDGETS, 0)

        val smallActive = appWidgetManager.getAppWidgetIds(
            ComponentName(
                requireContext(),
                SmallWidget::class.java
            )
        ).size

        val mediumActive = appWidgetManager.getAppWidgetIds(
            ComponentName(
                requireContext(),
                MediumWidget::class.java
            )
        ).size

        val smallRemoved = smallActive - prevSmallValue
        val mediumRemoved = mediumActive - prevMediumValue

        widgetsPrefs.edit().putInt(SMALL_WIDGETS, smallActive).apply()
        widgetsPrefs.edit().putInt(MEDIUM_WIDGETS, mediumActive).apply()

        if (smallRemoved == 0 && mediumRemoved == 0) return
        sendWidgetAnalyticsLogs(smallActive, smallRemoved, mediumActive, mediumRemoved)
    }

    private fun sendWidgetAnalyticsLogs(
        smallWidgetsActive: Int, smallWidgetsRemoved: Int,
        mediumWidgetsActive: Int, mediumWidgetsRemoved: Int
    ) {
        val bundle = Bundle()
        bundle.putString(
            Constants.FirebaseAnalytics.DEVICE_ID,
            DeviceUtils.getDeviceId(settingsUtils)
        )
        bundle.putString(SMALL, smallWidgetsActive.toString())
        bundle.putString(SMALL_DIFF, smallWidgetsRemoved.toString())
        bundle.putString(MEDIUM, mediumWidgetsActive.toString())
        bundle.putString(MEDIUM_DIFF, mediumWidgetsRemoved.toString())

        firebaseAnalyticsHelper.sendFirebaseEvent(
            WIDGET_INSTALLED_KEY,
            bundle
        )
    }

    override fun showPaceCheckerIntro(screenContent: ScreenContent) {
        PaceCheckerIntroDialog.newInstance(screenContent).show(childFragmentManager, null)
    }

    override fun showMyWalksNewFeaturesBanner(screenContent: ScreenContent) {
        binding.cardHeaderMyWalksNewFeatureText.text = screenContent.title
        binding.cardHeaderMyWalksNewFeatureIcon.loadFromUrl(screenContent.firstImageUrl)

        binding.cardHeaderMyWalksNewFeatureClose.setOnClickListener {
            preferenceRepository.showMyWalksNewFeaturesBanner = false
            binding.cardHeaderMyWalksNewFeatureContainer.isVisible = false
            binding.cardHeaderContainer.isVisible = true
            setupBadgeNotification()
        }

        binding.cardHeaderMyWalksNewFeatureContainer.setOnClickListenerWithDebounce {
            (requireActivity() as? HomeActivity?)?.navigateToMyWalks()
        }

        setupBadgeNotification()
    }

    private fun setClassicBanners() {
        val dateOfOnboardFinished =
            preferenceRepository.dateOfOnboardFinished?.plusDays(6) ?: LocalDate.MIN

        val isPaceCheckerOpened = preferenceRepository.isPaceCheckerOpened
        val isNewUser = preferenceRepository.isPaceCheckerNewUser
        val isSixDaysAfterOnboardFinished = dateOfOnboardFinished <= LocalDate.now()
        val isDismissed = preferenceRepository.isPaceCheckerBannerDismissed
        val showMyWalksBanner = preferenceRepository.showMyWalksNewFeaturesBanner
        val isUserLoggedIn = preferenceRepository.isUserLoggedIn

        if (!isPaceCheckerOpened && !isDismissed && (!isNewUser || isSixDaysAfterOnboardFinished)
            && ((isUserLoggedIn && !showMyWalksBanner) || !isUserLoggedIn)) {
            binding.cardHeaderPaceCheckerContainer.isVisible = true
            binding.cardHeaderMyWalksNewFeatureContainer.isVisible = false
            binding.cardHeaderContainer.isVisible = false
        }

        if (isUserLoggedIn && showMyWalksBanner) {
            binding.cardHeaderMyWalksNewFeatureContainer.isVisible = true
            binding.cardHeaderPaceCheckerContainer.isVisible = false
            binding.cardHeaderContainer.isVisible = false
        } else {
            binding.cardHeaderMyWalksNewFeatureContainer.isVisible = false
        }

        if (!binding.cardHeaderPaceCheckerContainer.isVisible &&
            !binding.cardHeaderMyWalksNewFeatureContainer.isVisible) {
            binding.cardHeaderContainer.isVisible = true
        }
    }

    override fun onPause() {
        super.onPause()

        isActivityJustCreated = false
        shouldStopRetrievingTodayWalk = true
        clearDisposables()

        sensorHandler.removeCallbacksAndMessages(null)
        if (binding.walkingLAV.isAnimating) {
            binding.walkingLAV.cancelAnimation()
        }
    }

    override fun onResume() {
        super.onResume()
        val cal = Calendar.getInstance()
        val lastSaved = settingsUtils.getSettingsHolder().lastSavedTarget
        cal.time = lastSaved ?: Calendar.getInstance().time

        if (!DateHelper.isSameDay(cal)) {
            settingsUtils.updateSettings(SettingsDataHolder(totalBriskMinutes = 0))
        }

        if (!isActivityJustCreated) {

            if (!DateHelper.isSameDay(initCalendar)) {
                initCalendar()
                trophyAdapter?.updateDoneTarget(0)
                setTargetContentDescription()
            }

            setUpGoogleFitRunnable()
        }

        setupBadgeNotification()

        if (trophyAdapter != null) {
            updateTargetValue()
        }

        binding.todayWalkToolbar.requestFocus()
    }

    override fun onFailure(sensorStepError: String) {
        Timber.e(IllegalStateException(sensorStepError), "Sensor failure")

        FirebaseCrashlytics.getInstance()
            .log(Constants.FirebaseAnalytics.REGISTER_TO_SENSORS_FOR_LIVE_CADENCE)
    }

    override fun onSuccess(cadence: Double, isBrisk: Boolean) {
        sensorHandler.removeCallbacksAndMessages(null)

        binding.walkingLAV.let {
            if (isBrisk) {
                if (!it.isAnimating) {
                    it.playAnimationIfEnabled(preferenceRepository.isAnimationEnabled)
                }
            } else {
                if (it.isAnimating) {
                    it.frame = 0
                    it.cancelAnimation()
                }
            }

            sensorHandler.postDelayed({
                it.frame = 0
                it.cancelAnimation()
            }, SENSOR_DELAY_MILLIS)
        }
    }

    private fun setRewardsMessagesHeader(totalBriskMin: Int) {
        presenter.getWalkingMessages(totalBriskMin)
    }

    private fun handleTargets() {
        updateTargetValue()
        setRewardsMessagesHeader(totalBriskMin)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TARGET_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                updateTargetValue()
                setRewardsMessagesHeader(totalBriskMin)
            }
        }
    }

    override fun onStepDataReceived(briskActivity: BriskActivity) {
        stopLoading()

        todayActivityObserver.dispose()

        val oldBriskMinutes = settingsUtils.getSettingsHolder().totalBriskMinutes ?: 0

        updateLocalBriskActivityData(briskActivity)
        setUpBriskAndTotalLabels(totalWalkMin, totalBriskMin)
        setRewardsMessagesHeader(totalBriskMin)

        val maxProgress = binding.horseShoePB.getMaximumProgress()

        if (oldBriskMinutes == totalBriskMin) {
            if (isFirstTime) {
                shouldAnimateProgress = true
            }
            updateScreenDetails(maxProgress)
        } else {
            shouldAnimateProgress = true
            animateScreenDetails(maxProgress, oldBriskMinutes)
        }
        isFirstTime = false

        presenter.checkForIntroducingCoachApp(briskActivity.minutesOfBrisk)
    }

    private fun setUpBriskAndTotalLabels(minWalking: Int, briskWalking: Int) {
        binding.briskTotal.text = UIUtils.getQuantityString(R.plurals.progress_mins, briskWalking)
        binding.minsTotal.text = UIUtils.getQuantityString(R.plurals.progress_mins, minWalking)
    }

    private fun updateScreenDetails(maxProgress: Int) {
        val activeTen = totalBriskMin / maxProgress
        currentActiveTen = activeTen
        trophyAdapter?.updateDoneTarget(currentActiveTen)
        setTargetContentDescription()
        val progress = totalBriskMin % maxProgress
        updateHorseShoeProgress(progress)
    }

    override fun onStepDataError(errorMessage: String) {
        AlertHelper.showErrorToast(errorMessage, Toast.LENGTH_LONG)
    }

    private fun setUpGoogleFitRunnable() {
        addDisposable(Observable.interval(
            GOOGLE_FIT_INITIAL_DELAY,
            GOOGLE_FIT_DELAY_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .repeatUntil { shouldStopRetrievingTodayWalk }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                requestStepActivity()
            }, {
                Timber.d("Error occurred while retrieving google fit today walk: ${it.message}")
            })
        )
    }

    private fun requestStepActivity() {
        todayActivityObserver =
            BriskCounter.retrieveTodayActivity(context ?: return, activity as ActivityListener)

        startLoadingData()
    }

    private fun startLoadingData() {
        if (isFirstTime) {
            // Show loading only first time, not for every refresh because refreshes are to fast.
            binding.loadingWalksView.isVisible = true
        }
    }

    private fun stopLoading() {
        binding.loadingWalksView.isVisible = false
    }

    private fun animateScreenDetails(maxProgress: Int, oldBriskMinutes: Int) {
        val oldActiveTen = oldBriskMinutes / maxProgress
        val activeTen = totalBriskMin / maxProgress

        if (oldActiveTen != activeTen && activeTen > 0) {
            currentActiveTen = activeTen

            createNewTrophy()

            trophyAdapter?.updateDoneTarget(oldActiveTen)

            if (preferenceRepository.isAnimationEnabled) {
                tipsDrawListener?.startConfettiAnimation(totalBriskMin % maxProgress, activeTen)
            }

            updateHorseShoeProgress(maxProgress)

        } else {
            val progress = totalBriskMin % maxProgress
            updateHorseShoeProgress(progress)
            trophyAdapter?.updateDoneTarget(activeTen)
        }
        setTargetContentDescription()
    }

    override fun onStart() {
        super.onStart()
        BriskCounter.registerLiveCadence(context ?: return, this)
    }

    override fun onStop() {
        super.onStop()
        BriskCounter.unregisterLiveCadence(context ?: return)
        todayActivityObserver.dispose()
    }

    private fun updateLocalBriskActivityData(briskActivity: BriskActivity) {
        totalWalkMin = briskActivity.minutesOfWalk
        shouldAnimateProgress = totalBriskMin != briskActivity.minutesOfBrisk
        totalBriskMin = briskActivity.minutesOfBrisk

        settingsUtils.updateSettings(SettingsDataHolder(totalBriskMinutes = totalBriskMin))
        settingsUtils.updateSettings(SettingsDataHolder(lastSavedTarget = Calendar.getInstance().time))
    }

    private fun initCalendar() {
        initCalendar.time = Date()
        val resetTimestamp = settingsUtils.getSettingsHolder().resetTimestamp
        if (resetTimestamp != null) {
            val resetCalendar = Calendar.getInstance()
            resetCalendar.timeInMillis = resetTimestamp
            initCalendar.set(Calendar.HOUR_OF_DAY, resetCalendar.get(Calendar.HOUR_OF_DAY))
            initCalendar.set(Calendar.MINUTE, resetCalendar.get(Calendar.MINUTE))
            initCalendar.set(Calendar.SECOND, resetCalendar.get(Calendar.SECOND))
            initCalendar.set(Calendar.MILLISECOND, resetCalendar.get(Calendar.MILLISECOND))
        } else {
            initCalendar.set(Calendar.HOUR_OF_DAY, 0)
            initCalendar.set(Calendar.MINUTE, 0)
            initCalendar.set(Calendar.SECOND, 0)
            initCalendar.set(Calendar.MILLISECOND, 0)
        }
    }

    private fun updateHorseShoeProgress(walkMinutes: Int) {
        setHorseShoeRewardBasedOnProgress(walkMinutes)

        binding.horseShoePB.updateProgress(walkMinutes)
        binding.horseShoePB.contentDescription =
            UIUtils.getString(R.string.accessibility_today_walk_horse_shoe, walkMinutes)
        binding.todaysWalksProgressCurrentText.text =
            UIUtils.getQuantityString(R.plurals.progress_mins, walkMinutes)
    }

    private fun setUpToolbar() {
        ViewCompat.setAccessibilityHeading(binding.todayWalkToolbar, true)
    }

    private fun setUpHorseShoe() {
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        val percentage =
            height * resources.getFraction(R.fraction.horse_shoe_progress_proportion, 1, 10) / width
        val params = binding.horseShoePB.layoutParams as ConstraintLayout.LayoutParams
        params.matchConstraintPercentWidth = percentage
        binding.horseShoePB.layoutParams = params
        binding.horseShoePB.shouldDrawMinMax = false
        binding.todaysWalksProgressCurrentText.text =
            UIUtils.getQuantityString(R.plurals.progress_mins, 0)
        binding.todaysWalksProgressMaxText.text =
            UIUtils.getQuantityString(R.plurals.progress_mins, 10)
    }

    private fun setUpViews() {
        setUpHorseShoe()
        val targets = settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: DEFAULT_TARGET
        binding.trophyRV.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        binding.trophyRV.addItemDecoration(
            MarginItemDecoration(
                UIUtils.getDimension(R.dimen.margin_size_6).toInt()
            )
        )
        trophyAdapter = TrophyAdapter(targets, currentActiveTen)
        binding.trophyRV.adapter = trophyAdapter
        binding.trophyTV.text =
            UIUtils.getQuantityString(R.plurals.today_walk_today_active_10, targets)
        setTargetContentDescription()
        binding.headerHorseShoe.setHeading()
        binding.todaysTotalLabel.setHeading()

        binding.helpContainer.setOnClickListener { startActivity(requireContext().HowItWorksIntent()) }
    }

    private fun markRewardAsOpened() {
        currentReward?.id?.let {
            EarnBadgeHelper.markBadgeAsShown(it, settingsUtils)
        }
    }

    private fun setTargetContentDescription() {
        val totalTargets =
            settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: DEFAULT_TARGET
        val currentTarget = currentActiveTen

        val totalTargetString =
            UIUtils.getQuantityString(R.plurals.accessibility_target_plural, totalTargets)
        val currentTargetString =
            UIUtils.getQuantityString(R.plurals.accessibility_target_plural, currentTarget)
        binding.trophyTV.contentDescription =
            UIUtils.getString(
                R.string.accessibility_today_walk_target,
                totalTargetString,
                currentTargetString
            )
    }

    private fun setupBadgeNotification() {
        val badge = getRewardBadgeToShow()
        if (badge != null) {
            showReward(badge)
        } else if (headerMessage != null) {
            showHeaderMessage()
        } else {
            binding.cardHeader.isVisible = false
        }
        setClassicBanners()
    }

    private fun showHeaderMessage() {
        binding.cardHeaderContainer.setOnClickListener(null)
        binding.cardHeader.isClickable = false
        binding.cardHeader.isVisible = true
        binding.todayWalkRewardTitle.isVisible = true
        binding.todayWalkRewardDescription.isVisible = false
        binding.todayWalkRewardTitle.text = headerMessage
        Glide.with(requireContext())
            .load(R.drawable.ic_badge_quick_march_on)
            .into(binding.headerImage)
    }

    private fun getRewardBadgeToShow(): RewardBadge? {
        val hasTodayBadge =
            settingsUtils.getSettingsHolder().earnedBadges?.find { DateUtils.isToday(it.timestamp) } != null
        if (!hasTodayBadge) return null
        val earnedBadges = arrayListOf<EarnRewardBadge>()
        val rewardsTemp = (settingsUtils.getSettingsHolder().earnedBadges ?: emptyList())
        earnedBadges.addAll(rewardsTemp)
        earnedBadges.sortByDescending { it.timestamp }
        val lastEarnBadge = earnedBadges.firstOrNull() ?: return null
        if (lastEarnBadge.wasShown) return null
        val badge = rewards?.find { it.id == lastEarnBadge.id } ?: return null
        return badge.apply { repetitions = rewardsTemp.count { it.id == this.id } }
    }

    private fun showReward(badge: RewardBadge) {
        binding.cardHeader.isVisible = true
        currentReward = badge
        binding.headerImage.loadFromUrl(badge.onImage)
        if (badge.slug == RewardBadgeEnum.TARGET_HITTER.slug && badge.repetitions > 0) {
            binding.numberImage.text = "${badge.repetitions}"
            binding.numberImage.isVisible = true
        } else {
            binding.numberImage.isVisible = false
        }
        binding.todayWalkRewardTitle.isVisible = true
        binding.todayWalkRewardDescription.isVisible = true
        binding.todayWalkRewardTitle.text = badge.title
        binding.todayWalkRewardDescription.text = badge.text
        if (!binding.cardHeaderContainer.hasOnClickListeners()) {
            binding.cardHeaderContainer.setOnClickListener {
                markRewardAsOpened()
                startActivity(context?.getRewardIntent() ?: return@setOnClickListener)
            }
        }
    }

    private fun updateTargetValue() {
        val targets = settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: DEFAULT_TARGET
        trophyAdapter?.updateNewTargets(targets)
        binding.trophyTV.text =
            UIUtils.getQuantityString(R.plurals.today_walk_today_active_10, targets)
        setTargetContentDescription()
    }

    private fun scrollToTrophiesToEnd() {
        binding.trophyRV.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener1 {
            override fun onGlobalLayout() {
                binding.trophyRV.smoothScrollToPosition(currentActiveTen)
                binding.trophyRV.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun setHorseShoeRewardBasedOnProgress(progress: Int) {
        binding.horseShoeRewardImageView.isVisible = progress != 0

        Glide.with(requireContext())
            .load(UIUtils.getHorseShoeRewardImage(progress))
            .into(binding.horseShoeRewardImageView)
    }

    override fun updateCurrentProgress(progress: Int, trophies: Int) {
        updateHorseShoeProgress(progress)
        trophyAdapter?.updateDoneTarget(trophies)
    }

    private fun createNewTrophy() {
        val noOfSetTargets = settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target
        if (noOfSetTargets == null || currentActiveTen > noOfSetTargets) {
            trophyAdapter?.createNewTrophy()
            setTargetContentDescription()
            scrollToTrophiesToEnd()
        }
    }

    private fun startMigrationService() {
        val isMigrationDataSent = settingsUtils.getSettingsHolder().isMigrationDataSent
        if (isMigrationDataSent == true) {
            return
        }

        context?.let {
            MigrationService.enqueueWork(it, it.MigrationService())
        }
    }

    override fun onMessagesReceived(message: String) {
        headerMessage = message
        setupBadgeNotification()
    }

    override fun onRewardsReceived(data: List<RewardBadge>) {
        this.rewards = data
        setupBadgeNotification()
    }
}
