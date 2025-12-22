package com.flipsidegroup.active10.presentation.mywalks

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flipside.briskcounter.data.BriskActivity
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.GlobalRules
import com.flipsidegroup.active10.data.IntervalWalk
import com.flipsidegroup.active10.data.PeriodTypeEnum
import com.flipsidegroup.active10.data.Reward
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.data.WalkingMessageResponse
import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.databinding.FragmentMyWalksBinding
import com.flipsidegroup.active10.databinding.TooltipWidgetBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.dialogs.MissingDataDialog
import com.flipsidegroup.active10.presentation.discover_details.DiscoverDetailsIntent
import com.flipsidegroup.active10.presentation.home.activities.DEEPLINK_EVENT_PARAM
import com.flipsidegroup.active10.presentation.home.activities.DEEPLINK_EVENT_SHOW_WALKING_PLANS
import com.flipsidegroup.active10.presentation.mywalks.adapters.MyWalksAdapter
import com.flipsidegroup.active10.presentation.mywalks.adapters.RewardBadgeAdapter
import com.flipsidegroup.active10.presentation.mywalks.interfaces.MyWalkRetrieveDataListener
import com.flipsidegroup.active10.presentation.mywalks.interfaces.RetrieveDataInitListener
import com.flipsidegroup.active10.presentation.mywalks.interfaces.WalkDataChangedListener
import com.flipsidegroup.active10.presentation.mywalks.presenter.MyWalksPresenter
import com.flipsidegroup.active10.presentation.mywalks.view.MyWalksView
import com.flipsidegroup.active10.presentation.reward.getRewardIntent
import com.flipsidegroup.active10.presentation.targets.activities.SetTargetMode
import com.flipsidegroup.active10.presentation.targets.activities.TargetIntent
import com.flipsidegroup.active10.presentation.tips.activities.TipsIntent
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.StepDataInitListener
import com.flipsidegroup.active10.presentation.todaywalk.interfaces.StepDataListener
import com.flipsidegroup.active10.presentation.usecases.FuncItemActionUseCase
import com.flipsidegroup.active10.presentation.usecases.FuncItemActionUseCase.Companion.ACTION_MY_WALKING_PLANS
import com.flipsidegroup.active10.presentation.walkpresentation.activities.WalkPresentationIntent
import com.flipsidegroup.active10.utils.AlertHelper
import com.flipsidegroup.active10.utils.Constants.RetrieveLostData.NUMBER_OF_RETRIEVE_DATA_TRIES
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.MyWalkRewardMessageHelper
import com.flipsidegroup.active10.utils.MyWalksRewardsDestination
import com.flipsidegroup.active10.utils.MyWalksTooltip
import com.flipsidegroup.active10.utils.SmoothScroller
import com.flipsidegroup.active10.utils.SnapOnScrollListener
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.getYScreenPosition
import com.flipsidegroup.active10.utils.hide
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.onScrollTo
import com.flipsidegroup.active10.utils.onTabSelected
import com.flipsidegroup.active10.utils.setAccessibilityButton
import com.flipsidegroup.active10.utils.setAccessibilityTab
import com.flipsidegroup.active10.utils.setRoleDescription
import com.flipsidegroup.active10.utils.show
import com.flipsidegroup.active10.utils.view.HighlightView
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.phe.betterhealth.widgets.utils.dpToPx
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import timber.log.Timber
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs

private const val EMPTY_PROGRESS = 0
const val IN_PERIOD_TYPE = "IN_PERIOD_TYPE"
const val SWIPE_THRESHOLD = 100
private const val CHART_BAR_WIDTH = 0.5f
private const val CHART_ANIMATION_DURATION = 300
private const val MONDAY_INDEX = 0
private const val SUNDAY_INDEX = 6
private const val ONE_DAY = 1
private const val CHART_LEFT_AXIS_MIN_LABEL_COUNT = 4
private const val CHART_LEFT_AXIS_MAX_LABEL_COUNT = 5
private const val CHART_LEFT_AXIS_MIN = 0f
private const val CHART_LEFT_AXIS_MAX = 60f

class MyWalksFragment : BaseFragment<MyWalksView>(),
    SnapOnScrollListener.OnSnapPositionChangeListener,
    RadioGroup.OnCheckedChangeListener, MyWalksView, StepDataListener, View.OnTouchListener,
    GestureDetector.OnGestureListener, MyWalkRetrieveDataListener {

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var presenter: MyWalksPresenter

    private var binding: FragmentMyWalksBinding by lifecycleAwareVariable()

    @Inject
    internal lateinit var myWalkHelper: MyWalkRewardMessageHelper

    @Inject
    internal lateinit var preferenceRepository: PreferenceRepository

    @Inject
    internal lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    @Inject
    internal lateinit var funcItemActionUseCase: FuncItemActionUseCase

    private lateinit var mDetector: GestureDetectorCompat

    private var badgesAdapter: RewardBadgeAdapter? = null
    private val myWalksAdapter by lazy {
        MyWalksAdapter().apply {
            onItemClickListener = { actionSlug ->
                funcItemActionUseCase(requireContext(), actionSlug)
            }
        }
    }

    private var rewardBadges = ArrayList<RewardBadge>()
    private var intervals = ArrayList<IntervalWalk>()
    private var currentPeriodType = PeriodTypeEnum.DAYS
    private lateinit var currentInterval: IntervalWalk
    private lateinit var smoothScroller: RecyclerView.SmoothScroller

    private var currentPosition = 0

    private var todayWalk = 0
    private var todayBriskWalk = 0
    private var todayRewards = ArrayList<Reward>()
    private var todayTargetHit = 0
    private var recyclerLastFocusItemPosition = -1
    private var walkChangedListener: WalkDataChangedListener? = null

    private var tooltipHandler: Handler? = null
    private var lastTooltipIndex: Int = 0
    private var moveTooltipRunnable: Runnable? = null
    private var tooltipsContent = listOf<MyWalksTooltip>()
    private var openMyWalkingPlans = false

    companion object {
        fun newInstance(currentPeriodPosition: PeriodTypeEnum = PeriodTypeEnum.DAYS): MyWalksFragment {
            val instance = MyWalksFragment()
            instance.arguments = Bundle().apply {
                putSerializable(IN_PERIOD_TYPE, currentPeriodPosition)
            }
            return instance
        }
    }

    override fun getPresenter(): LifecycleAwarePresenter<MyWalksView> = presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.registerDataListener()

        if (context is StepDataInitListener) {
            context.initStepDataListener(this)
        }

        if (context is RetrieveDataInitListener) {
            context.initRetrieveDataListener(this)
        }

        if (context is WalkDataChangedListener) {
            walkChangedListener = context
        }
    }

    override fun onDetach() {
        presenter.unregisterDataListener()
        super.onDetach()

        if (context is StepDataInitListener) {
            (context as StepDataInitListener).removeStepDataListener(this)
        }
        if (context is RetrieveDataInitListener) {
            (context as RetrieveDataInitListener).removeRetrieveDataListener()
        }
    }

    override fun onResume() {
        super.onResume()
        if (rewardBadges.isNotEmpty()) {
            setUpRewardsBadges()
        }

        if (binding.nestedScrollView != null) {
            binding.nestedScrollView.smoothScrollTo(0, 0)

            presenter.getGlobalRules()
        }
        setRetrieveDataOverlayView()
        binding.myWalksToolbar.requestFocus()
        updateTotalA10DestinationListener()
        updateMyWalksNewFeaturesBannerState()

        setupMyWalksChildrenItems()
        setTooltipView(lastTooltipIndex)

        if (openMyWalkingPlans) {
            openMyWalkingPlans = false
            funcItemActionUseCase(requireContext(), ACTION_MY_WALKING_PLANS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tooltipHandler = Handler(Looper.getMainLooper())
        openMyWalkingPlans =
            activity?.intent?.getStringExtra(DEEPLINK_EVENT_PARAM) == DEEPLINK_EVENT_SHOW_WALKING_PLANS
                    && preferenceRepository.isUserLoggedIn
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_walks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyWalksBinding.bind(view)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        presenter.getAllRewardsList()
        setUpToolbar()
        setUpView()
        initChart()
    }


    private val goToTipsView = View.OnClickListener {
        context?.TipsIntent()?.let {
            startActivity(it)
        }
    }

    private val goToTargetPage = View.OnClickListener {
        context?.TargetIntent(SetTargetMode.SETTINGS)?.let {
            startActivity(it)
        }
    }

    private val goToArticle150 = View.OnClickListener {
        presenter.goToArticle150()
    }

    private fun initChart() {
        binding.barChart.setPinchZoom(false)
        binding.barChart.isDoubleTapToZoomEnabled = false
        binding.barChart.isNestedScrollingEnabled = false
        binding.barChart.setScaleEnabled(false)
        binding.barChart.description.isEnabled = false
        binding.barChart.isHighlightPerTapEnabled = false
        binding.barChart.isHighlightPerDragEnabled = false

        val xAxis: XAxis = binding.barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.isEnabled = true
        xAxis.setDrawLabels(true)
        xAxis.textColor = UIUtils.getColor(R.color.black)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)

        val leftAxis: YAxis = binding.barChart.axisLeft
        leftAxis.setDrawLabels(true)
        leftAxis.isEnabled = true
        leftAxis.textColor = UIUtils.getColor(R.color.black)
        leftAxis.setDrawAxisLine(false)
        leftAxis.axisMinimum = CHART_LEFT_AXIS_MIN
        leftAxis.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)

        val rightAxis: YAxis = binding.barChart.axisRight
        rightAxis.isEnabled = false

        val l: Legend = binding.barChart.legend
        l.isEnabled = false
    }

    override fun onStepDataReceived(briskActivity: BriskActivity) {
        this.todayWalk = briskActivity.minutesOfWalk
        this.todayBriskWalk = briskActivity.minutesOfBrisk
        presenter.getTodayRewards(briskActivity.minutesOfBrisk, briskActivity.minutesOfWalk)
    }

    override fun onStepDataError(errorMessage: String) {
        AlertHelper.showErrorToast(errorMessage, Toast.LENGTH_LONG)
    }

    private fun setUpToolbar() {
        ViewCompat.setAccessibilityHeading(binding.myWalksToolbar, true)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpView() {
        setUpHeaders()
        setUpIntervalRecycler()
        setUpBadgesRecycler()
        setupIntervalTabs()
        setupMyWalksChildrenItems()

        binding.periodTypeRG.setOnCheckedChangeListener(this)
        binding.daysWalkRB.isChecked = true
        val currentPeriod =
            (arguments?.getSerializable(IN_PERIOD_TYPE) ?: PeriodTypeEnum.DAYS) as PeriodTypeEnum
        setCurrentPeriodType(currentPeriod)

        mDetector = GestureDetectorCompat(requireContext(), this)
        binding.progressRL.setOnTouchListener(this)

        binding.viewRewardBadgesIV.setOnClickListener {
            val intent = context?.getRewardIntent() ?: return@setOnClickListener
            startActivity(intent)
        }

        binding.viewRewards.setOnClickListener {
            val intent = context?.getRewardIntent() ?: return@setOnClickListener
            startActivity(intent)
        }

        binding.daysWalkRB.setAccessibilityTab()
        binding.weeksWalkRB.setAccessibilityTab()
        binding.monthsWalkRB.setAccessibilityTab()

        binding.barChart.setOnClickListener {
            context?.let {
                val intent = it.WalkPresentationIntent(
                    currentPeriodType,
                    currentInterval.startTimestamp,
                    currentInterval.endTimestamp,
                    todayBriskWalk
                )
                it.startActivity(intent)
            }
        }
    }

    private fun setupMyWalksChildrenItems() {
        with(binding.myWalksRecycler) {
            if (preferenceRepository.isUserLoggedIn) {
                if (adapter == null) adapter = myWalksAdapter
                presenter.getMyWalksChildrenItems()
            } else {
                hideTooltipsOverlay()
                removeFuncItems()
            }
        }
    }

    override fun showChildrenItems(items: List<ScreenContent>) {
        myWalksAdapter.submitList(items)
        tooltipsContent = items.map {
            MyWalksTooltip(
                title = it.tooltipTitle ?: "",
                description = it.tooltipText ?: ""
            )
        }
    }

    private fun setUpHeaders() {
        ViewCompat.setAccessibilityHeading(binding.totalA10Header, true)
        ViewCompat.setAccessibilityHeading(binding.chartTitleTV, true)
        ViewCompat.setAccessibilityHeading(binding.rewardsHeader, true)
    }

    private fun setupIntervalTabs() {
        binding.myWalksIntervalTabLayout.onTabSelected { tab ->
            onIntervalSelected(intervals.lastIndex - tab.position)
        }
    }

    private fun setRetrieveDataOverlayView() {
        val retryNr = settingsUtils.getSettingsHolder().nrOfRetryLostDada ?: 0
        val lastRetry = settingsUtils.getSettingsHolder().lastRetryLostData ?: 0
        val shouldCheckForLostData =
            settingsUtils.getSettingsHolder().shouldCheckForLostData == true && settingsUtils.getSettingsHolder().hasLeftReview == null &&
                    retryNr < NUMBER_OF_RETRIEVE_DATA_TRIES && !DateHelper.isSameDay(lastRetry)
        if (shouldCheckForLostData) {
            //todo show/hide bottomsheets if needed
        } else {
            //todo show/hide bottomsheets if needed
        }
    }

    private fun setCurrentPeriodType(position: PeriodTypeEnum) {
        when (position) {
            PeriodTypeEnum.DAYS -> binding.daysWalkRB.isChecked = true
            PeriodTypeEnum.WEEKS -> binding.weeksWalkRB.isChecked = true
            PeriodTypeEnum.MONTHS -> binding.monthsWalkRB.isChecked = true
        }
    }

    private fun setUpIntervalRecycler() {
        smoothScroller = SmoothScroller(this.context)

    }

    private fun setUpBadgesRecycler() {
        badgesAdapter = RewardBadgeAdapter(true) {
            startActivity(requireContext().getRewardIntent())
        }
        binding.rewardBadgesRV.layoutManager = GridLayoutManager(context, 3)
        binding.rewardBadgesRV.adapter = badgesAdapter
    }

    private fun setUpRewardsBadges() {
        val earnedBadges = settingsUtils.getSettingsHolder().earnedBadges ?: arrayListOf()
        val badgesFromInterval =
            earnedBadges.filter { it.timestamp >= currentInterval.startTimestamp && it.timestamp <= currentInterval.endTimestamp }
                .sortedByDescending { it.timestamp }
        if (badgesFromInterval.isEmpty()) {
            binding.rewardBadgesRV.hide()
            binding.rewardsGroup.show()
            binding.badgesCL.setOnClickListener {
                val intent = context?.getRewardIntent() ?: return@setOnClickListener
                startActivity(intent)
            }
        } else {
            binding.badgesCL.setOnClickListener { }
            binding.rewardBadgesRV.show()
            binding.rewardsGroup.hide()

            val rewardsEarned = arrayListOf<RewardBadge>()
            for (badge in badgesFromInterval) {
                if (rewardsEarned.any { it.slug == badge.slug }) continue
                rewardsEarned.add(
                    rewardBadges
                        .find { it.id == badge.id }
                        ?.apply {
                            repetitions = earnedBadges.count { badges -> badges.slug == this.slug }
                        }
                        ?: continue
                )
                if (rewardsEarned.size == 3) {
                    break
                }
            }
            badgesAdapter?.addRewardsMyWalk(rewardsEarned, badgesFromInterval)
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checedkId: Int) {
        currentPeriodType = when (checedkId) {
            R.id.daysWalkRB -> {
                PeriodTypeEnum.DAYS
            }

            R.id.weeksWalkRB -> {
                PeriodTypeEnum.WEEKS
            }

            else -> {
                PeriodTypeEnum.MONTHS
            }
        }
        presenter.updateCurrentPeriod(currentPeriodType)
        changeWalkSection()
    }

    private fun changeWalkSection() {
        intervals.clear()
        when (currentPeriodType) {
            PeriodTypeEnum.DAYS -> {
                presenter.getDaysIntervals(this.context)
            }

            PeriodTypeEnum.WEEKS -> {
                presenter.getWeeksIntervals(this.context)
            }

            PeriodTypeEnum.MONTHS -> {
                presenter.getMonthsIntervals(this.context)
            }
        }
        recyclerLastFocusItemPosition = -1
    }

    override fun onSnapPositionChange(position: Int) {
        selectIntervalByPosition(position)
    }

    private fun onIntervalSelected(position: Int) {
        currentPosition = position

        currentInterval = intervals[position]
        setUpRewardsBadges()

        // Set Bar Chart visibility
        if (currentPeriodType == PeriodTypeEnum.DAYS) {
            binding.briskThisMonthCard.hide()
        } else {
            binding.briskThisMonthCard.show()
        }

        if (DateHelper.isSameDay(currentInterval.startTimestamp)) {
            setUpRewards(
                todayWalk,
                todayBriskWalk,
                todayRewards,
                todayTargetHit,
                todayBriskWalk / 10
            )
            loadBarChart(arrayListOf(), currentInterval)
        } else {
            presenter.getPeriodRewards(currentPeriodType, currentInterval)
        }
    }

    private fun setRewardsMessage(targetHit: Int, briskWalk: Int, active10: Int) {
        presenter.getWalkingMessages(targetHit, briskWalk, active10)
    }

    private fun setUpEmptyProgress() {
        binding.myWalkProgress.setUpEmptyMyWalks()
        binding.noBriskMessageTv.isVisible = true
        setRewardsMessage(0, 0, 0)
        binding.currentProgressValueTV.text = ""
        binding.maxProgressValueTV.text = ""
        binding.currentProgressTV.setTextColor(UIUtils.getColor(R.color.black_transparent_20))
        binding.maxProgressTV.setTextColor(UIUtils.getColor(R.color.black_transparent_20))
    }

    private fun setUpProgress(walk: Int, briskWalk: Int, targetHit: Int, active10: Int) {
        binding.myWalkProgress.setUpMyWalks()
        binding.myWalkProgress.setMaximumProgress(walk)
        binding.myWalkProgress.updateProgress(briskWalk)

        binding.currentProgressValueTV.text =
            UIUtils.getQuantityString(R.plurals.progress_mins, briskWalk)
        binding.maxProgressValueTV.text = UIUtils.getQuantityString(R.plurals.progress_mins, walk)

        val briskMinutesString =
            UIUtils.getQuantityString(R.plurals.accessibility_my_walks_horse_shoe_plural, briskWalk)
        val totalMinutesString =
            UIUtils.getQuantityString(R.plurals.accessibility_my_walks_horse_shoe_plural, walk)
        binding.myWalkProgress.contentDescription = UIUtils.getString(
            R.string.accessibility_my_walks_horse_shoe,
            totalMinutesString,
            briskMinutesString
        )

        binding.noBriskMessageTv.isVisible = false
        binding.currentProgressTV.setTextColor(UIUtils.getColor(R.color.black))
        binding.maxProgressTV.setTextColor(UIUtils.getColor(R.color.black))

        setRewardsMessage(targetHit, briskWalk, active10)
    }

    override fun setUpPeriods(intervals: ArrayList<IntervalWalk>) {
        binding.myWalksIntervalTabLayout.isInvisible = true
        binding.progressRL.isInvisible = true
        binding.periodProgress.isVisible = true

        this.intervals = intervals
        binding.myWalksIntervalTabLayout.removeAllTabs()

        intervals.reversed()
            .forEachIndexed { idx, interval ->
                binding.myWalksIntervalTabLayout.addTab(
                    binding.myWalksIntervalTabLayout
                        .newTab()
                        .setText(interval.interval),
                    idx == intervals.lastIndex
                )
            }

        binding.myWalksIntervalTabLayout.postDelayed({
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return@postDelayed
            try {
                binding.myWalksIntervalTabLayout.setScrollPosition(
                    intervals.lastIndex,
                    0f,
                    true
                )
            } catch (err: Throwable) {
                Timber.d(err)
            }

            binding.myWalksIntervalTabLayout.isInvisible = false
            binding.progressRL.isInvisible = false
            binding.periodProgress.isVisible = false
        }, 200)

        onIntervalSelected(0)
    }

    override fun setUpRewards(
        walk: Int,
        briskWalk: Int,
        rewards: List<Reward>,
        daysTargetHit: Int,
        active10: Int
    ) {
        var finalWalk = walk
        var finalBrisk = briskWalk
        if (!DateHelper.isSameDay(currentInterval.startTimestamp) && (
                    currentPeriodType == PeriodTypeEnum.WEEKS && DateHelper.isSameWeek(
                        currentInterval.startTimestamp
                    ) ||
                            currentPeriodType == PeriodTypeEnum.MONTHS && DateHelper.isSameMonth(
                        currentInterval.startTimestamp
                    ))
        ) {
            finalWalk += todayWalk
            finalBrisk += todayBriskWalk
            todayRewards.forEach { reward ->
                rewards.find { it.id == reward.id }?.let {
                    it.count += reward.count
                }
            }
        }

        if (finalWalk == EMPTY_PROGRESS && DateHelper.getCurrentTimestamp() in currentInterval.startTimestamp until currentInterval.endTimestamp) {
            setUpEmptyProgress()
        } else {
            setUpProgress(finalWalk, finalBrisk, daysTargetHit, active10)
        }
    }

    private fun setNumberOfA10Earned(targetHit: Int) {
        if (targetHit == 0) {
            binding.numberOfA10.hide()
            binding.trophyImageView.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_mywalks_trophy_off)
        } else {
            binding.numberOfA10.show()
            binding.trophyImageView.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_mywalks_trophy_on)
            binding.numberOfA10.text = getString(R.string.today_walk_x_a10, targetHit)
        }
    }

    override fun setUpTodayRewards(rewards: List<Reward>, todayTargetHit: Int, active10: Int) {
        this.todayTargetHit = todayTargetHit
        this.todayRewards.clear()
        this.todayRewards.addAll(rewards)
        if (currentPeriodType == PeriodTypeEnum.DAYS && DateHelper.isSameDay(currentInterval.startTimestamp)) {
            setUpRewards(todayWalk, todayBriskWalk, todayRewards, todayTargetHit, active10)
        }
    }

    data class DestinationWalksCache(
        val targetHit: Int,
        val briskWalk: Int,
    )

    var destinationCache: DestinationWalksCache? = null

    private fun updateTotalA10DestinationListener() {
        destinationCache?.let {
            val destination = myWalkHelper.getRewardMessageDestination(
                currentPeriodType,
                currentInterval.startTimestamp,
                it.targetHit,
                it.briskWalk
            )
            binding.totalA10Card.setOnClickListener(
                when (destination) {
                    MyWalksRewardsDestination.TIPS -> goToTipsView
                    MyWalksRewardsDestination.TARGETS -> goToTargetPage
                    MyWalksRewardsDestination.ARTICLE150 -> goToArticle150
                    MyWalksRewardsDestination.NOTHING -> null
                }
            )
        }
    }

    override fun onMessagesReceived(
        targetHit: Int,
        briskWalk: Int,
        walkingMessageResponse: WalkingMessageResponse?,
        active10: Int
    ) {
        binding.totalA10Description.text =
            myWalkHelper.getRewardMessage(
                currentPeriodType,
                currentInterval.startTimestamp,
                targetHit,
                briskWalk,
                walkingMessageResponse
            )

        destinationCache = DestinationWalksCache(targetHit, briskWalk)
        updateTotalA10DestinationListener()

        binding.noBadgesMessageTV.text =
            myWalkHelper.getNoBadgesMessage(currentPeriodType, currentInterval)
        setNumberOfA10Earned(if (currentPeriodType == PeriodTypeEnum.DAYS) active10 else targetHit)

        binding.barChartRL.contentDescription = myWalkHelper.getBreakdownAccessibilityText()
        binding.barChart.contentDescription = myWalkHelper.getBreakdownAccessibilityText()
    }

    override fun goToArticle(infoPage: InfoPage) {
        context?.DiscoverDetailsIntent(infoPage.id, infoPage.title, "my_walks")?.let {
            startActivity(it)
        }
    }

    override fun loadBarChart(stepData: MutableList<StepOverview>, interval: IntervalWalk) {
        if ((currentPeriodType == PeriodTypeEnum.WEEKS && DateHelper.isSameWeek(
                currentInterval.startTimestamp
            ) || currentPeriodType == PeriodTypeEnum.MONTHS && DateHelper.isSameMonth(
                currentInterval.startTimestamp
            ))
        ) {
            val todayTimestamp = DateHelper.getEndOfDay(Calendar.getInstance()).timeInMillis
            stepData.add(StepOverview(todayTimestamp, todayBriskWalk, todayWalk))
        }

        val startCalendar = Calendar.getInstance(Locale.UK)
        startCalendar.timeInMillis = interval.startTimestamp

        val endCalendar = Calendar.getInstance(Locale.UK)
        endCalendar.timeInMillis = interval.endTimestamp

        val entries: ArrayList<BarEntry> = ArrayList()

        var startIndex = MONDAY_INDEX
        var endIndex = SUNDAY_INDEX
        if (currentPeriodType == PeriodTypeEnum.WEEKS) {
            binding.chartTitleTV.text = getString(R.string.brisk_mins_this_week)
            binding.chartTitleTV.contentDescription = getString(R.string.brisk_mins_this_week_description)
            binding.chartXAxisLegendTV.text = getString(R.string.days)

            binding.barChart.xAxis.valueFormatter = WeekXAxisFormatter()
        } else {
            binding.chartTitleTV.text = getString(R.string.brisk_mins_this_month)
            binding.chartTitleTV.contentDescription = getString(R.string.brisk_mins_this_month_description)
            binding.chartXAxisLegendTV.text = DateHelper.getMonth(startCalendar.timeInMillis)

            binding.barChart.xAxis.valueFormatter = MonthXAxisFormatter()

            startIndex = startCalendar.get(Calendar.DAY_OF_MONTH)
            endIndex = endCalendar.get(Calendar.DAY_OF_MONTH)
        }

        var maxBrisk = EMPTY_PROGRESS.toFloat()
        for (i in startIndex..endIndex) {
            var brisk = EMPTY_PROGRESS.toFloat()

            stepData.forEach {
                if (DateHelper.isSameDay(it.timestamp!!, startCalendar.timeInMillis)) {
                    brisk = it.totalBriskMin!!.toFloat()
                    if (brisk > maxBrisk) {
                        maxBrisk = brisk
                    }
                }
            }

            entries.add(BarEntry(i.toFloat(), brisk))

            startCalendar.add(Calendar.DAY_OF_YEAR, ONE_DAY)
        }

        if (maxBrisk > CHART_LEFT_AXIS_MAX) {
            binding.barChart.axisLeft.axisMaximum = ((maxBrisk + 5) / 10) * 10
            binding.barChart.axisLeft.setLabelCount(CHART_LEFT_AXIS_MAX_LABEL_COUNT, true)
        } else {
            binding.barChart.axisLeft.axisMaximum = CHART_LEFT_AXIS_MAX
            binding.barChart.axisLeft.setLabelCount(CHART_LEFT_AXIS_MIN_LABEL_COUNT, true)
        }

        binding.barChart.xAxis.granularity = 1f
        binding.barChart.xAxis.labelCount = entries.size
        binding.barChart.xAxis.isGranularityEnabled = true

        val dataSet = BarDataSet(entries, null)
        dataSet.color = UIUtils.getColor(R.color.yellow)

        val data = BarData(dataSet)
        data.setDrawValues(false)
        data.barWidth = CHART_BAR_WIDTH
        binding.barChart.animateY(CHART_ANIMATION_DURATION)
        binding.barChart.data = data
        binding.barChart.invalidate()
    }

    override fun onGlobalRulesRetrieved(globalRules: GlobalRules?) {

        showMissingDataDialog(globalRules)
    }

    override fun onRewardBadgesReceived(rewards: List<RewardBadge>) {
        this.rewardBadges.clear()
        this.rewardBadges.addAll(rewards)
        setUpRewardsBadges()
    }

    override fun onWalkDataChanged() {
        walkChangedListener?.onWalkDataChanged()
    }

    private fun showMissingDataDialog(globalRules: GlobalRules?) {
        globalRules?.let {
            MissingDataDialog(it).show(requireActivity().supportFragmentManager, null)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return mDetector.onTouchEvent(event)
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return true
    }

    override fun onDown(e: MotionEvent): Boolean {
        binding.progressRL.parent.requestDisallowInterceptTouchEvent(true)
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val downX = e1?.getAxisValue(MotionEvent.AXIS_X) ?: return false
        val upX = e2.getAxisValue(MotionEvent.AXIS_X)
        if (upX - downX > SWIPE_THRESHOLD && currentPosition < intervals.lastIndex) {
            //swipe right
            selectIntervalByPosition(currentPosition + 1)
        } else if (downX - upX > SWIPE_THRESHOLD && currentPosition > 0) {
            //swipe left
            selectIntervalByPosition(currentPosition - 1)
        }
        return true
    }

    private fun selectIntervalByPosition(position: Int) {
        binding.myWalksIntervalTabLayout.getTabAt(intervals.lastIndex - position)?.let {
            it.customView?.isSelected = true
            it.select()
        }
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (abs(distanceY) > abs(distanceX))
            binding.progressRL.parent.requestDisallowInterceptTouchEvent(false)
        return true
    }

    override fun onLongPress(e: MotionEvent) {
    }

    override fun onLostDataRetrieved() {
        setRetrieveDataOverlayView()
    }

    private fun updateMyWalksNewFeaturesBannerState() {
        if (preferenceRepository.isUserLoggedIn) {
            preferenceRepository.showMyWalksNewFeaturesBanner = false
        }
    }

    private fun setTooltipView(index: Int) {
        if (preferenceRepository.isUserLoggedIn && preferenceRepository.showMyWalksTooltips) {
            binding.isTooltipEnabled = true
            Handler(Looper.getMainLooper()).postDelayed({
                updateTooltipView(
                    index = index,
                    tooltips = tooltipsContent.map { MyWalksTooltip(it.title, it.description) }
                )
            }, 500)
        }
    }

    private fun updateTooltipView(
        index: Int,
        tooltips: List<MyWalksTooltip>,
    ) {
        if (tooltips.isEmpty() && index !in tooltips.indices) return

        lastTooltipIndex = index

        val tip = tooltips[index]
        val tooltipWidget = binding.tooltipWidget.binding
        val scrollView = binding.nestedScrollView
        val highlightView = binding.highlightView
        val recyclerPos = binding.myWalksRecycler.top
        val itemView =
            binding.myWalksRecycler.findViewHolderForAdapterPosition(index)?.itemView ?: return
        val statusBarHeight = 0

        with(tooltipWidget) {
            tooltipPopup.visibility = View.INVISIBLE
            title.text = tip.title
            description.text = tip.description
            pageIndicatorView.count = tooltips.size
            pageIndicatorView.setSelected(index)
            closeButton.setOnClickListener {
                hideTooltipsOverlay()
                preferenceRepository.showMyWalksTooltips = false
            }
            nextButton.text = if (index == tooltips.lastIndex) "Done" else "Next"
            nextButton.setOnClickListener {
                if (index == tooltips.lastIndex) {
                    hideTooltipsOverlay()
                    preferenceRepository.showMyWalksTooltips = false
                } else {
                    updateTooltipView(index + 1, tooltips)
                }
            }
        }

        val tooltipPos = recyclerPos + itemView.top - tooltipWidget.tooltipPopup.measuredHeight
        scrollView.onScrollTo(tooltipPos) {
            tooltipWidget.tooltipPopup.visibility = View.INVISIBLE
            val yRelative = itemView.getYScreenPosition() - statusBarHeight
            onTooltipsScroll(yRelative, itemView.measuredHeight, highlightView, tooltipWidget)
        }
    }

    private fun onTooltipsScroll(
        y: Int,
        h: Int,
        highlightView: HighlightView,
        tooltipWidget: TooltipWidgetBinding,
    ) {
        val (topPos, bottomPos) = getHighlightPos(y, h)

        highlightView.topPosition = topPos
        highlightView.bottomPosition = bottomPos
        highlightView.invalidate()

        moveTooltipPopup(topPos, tooltipWidget)
    }

    private fun getHighlightPos(
        y: Int,
        h: Int
    ): Pair<Float, Float> {
        return Pair(y.toFloat(), y.toFloat() + h)
    }

    private fun moveTooltipPopup(topPos: Float, tooltipWidget: TooltipWidgetBinding) {
        val tooltipPopup = tooltipWidget.tooltipPopup

        moveTooltipRunnable?.let {
            tooltipHandler?.removeCallbacks(it)
        }

        moveTooltipRunnable = Runnable {
            val mlp = tooltipPopup.layoutParams as ViewGroup.MarginLayoutParams
            mlp.topMargin =
                topPos.toInt() - tooltipPopup.measuredHeight + 4.dpToPx(requireContext()).toInt()
            tooltipPopup.layoutParams = mlp
            tooltipPopup.visibility = View.VISIBLE
            tooltipWidget.closeButton.doOnLayout {
                it.setAccessibilityButton()
                it.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            }
        }

        tooltipHandler?.postDelayed(moveTooltipRunnable!!, 300L)
    }

    private fun hideTooltipsOverlay() {
        binding.isTooltipEnabled = false
        lastTooltipIndex = 0
    }

    private fun removeFuncItems() {
        binding.myWalksRecycler.adapter = null
    }

    override fun onDestroyView() {
        tooltipHandler?.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }
}

class WeekXAxisFormatter : ValueFormatter() {
    private val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return days.getOrNull(value.toInt()) ?: value.toString()
    }
}

class MonthXAxisFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return when (value) {
            7f -> "7"
            14f -> "14"
            21f -> "21"
            28f -> "28"
            else -> ""
        }
    }
}
