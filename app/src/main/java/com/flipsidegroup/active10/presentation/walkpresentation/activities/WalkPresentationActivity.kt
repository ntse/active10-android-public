package com.flipsidegroup.active10.presentation.walkpresentation.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.PeriodTypeEnum
import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.databinding.ActivityWalkPresentationBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.walkpresentation.adapters.WalkPresentationAdapter
import com.flipsidegroup.active10.presentation.walkpresentation.presenter.WalkPresentationPresenter
import com.flipsidegroup.active10.presentation.walkpresentation.view.WalkPresentationView
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.announceHeader
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import java.util.Calendar
import javax.inject.Inject

const val IN_PERIOD_TYPE = "IN_PERIOD_TYPE"
const val IN_START_TIMESTAMP = "IN_START_TIMESTAMP"
const val IN_END_TIMESTAMP = "IN_END_TIMESTAMP"
const val IN_TODAY_BRISK = "IN_TODAY_BRISK"

fun Context.WalkPresentationIntent(
    periodType: PeriodTypeEnum,
    startTimestampMs: Long,
    endTimestampMs: Long,
    todayBrisk: Int
): Intent {
    return Intent(this, WalkPresentationActivity::class.java).apply {
        putExtra(IN_PERIOD_TYPE, periodType)
        putExtra(IN_START_TIMESTAMP, startTimestampMs)
        putExtra(IN_END_TIMESTAMP, endTimestampMs)
        putExtra(IN_TODAY_BRISK, todayBrisk)
    }
}

class WalkPresentationActivity : BaseSecureActivity<WalkPresentationView>(), WalkPresentationView {

    @Inject
    internal lateinit var presenter: WalkPresentationPresenter

    private var binding: ActivityWalkPresentationBinding by lifecycleAwareVariable()

    private var periodType: PeriodTypeEnum = PeriodTypeEnum.DAYS
    private var startTimestampMs: Long = 0
    private var endTimestampMs: Long = 0
    private var todayBrisk: Int = 0
    private val steps = mutableListOf<StepOverview>()

    override fun getPresenter(): LifecycleAwarePresenter<WalkPresentationView> = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityWalkPresentationBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        periodType =
            (intent.getSerializableExtra(IN_PERIOD_TYPE) ?: PeriodTypeEnum.DAYS) as PeriodTypeEnum
        startTimestampMs = intent.getLongExtra(IN_START_TIMESTAMP, 0)
        endTimestampMs = intent.getLongExtra(IN_END_TIMESTAMP, 0)
        todayBrisk = intent.getIntExtra(IN_TODAY_BRISK, 0)
        createSteps()
        presenter.getWalkData(startTimestampMs, endTimestampMs)
        setUpView()
    }

    private fun setUpView() {
        binding.periodContainer.announceHeader()
        val start = Calendar.getInstance()
        start.timeInMillis = startTimestampMs
        when (periodType) {
            PeriodTypeEnum.WEEKS -> {
                val end = Calendar.getInstance()
                end.timeInMillis = endTimestampMs

                binding.walkPeriodTv.text = UIUtils.getString(
                    R.string.my_walk_week_period,
                    DateHelper.getMonthName(start),
                    start.get(Calendar.DAY_OF_MONTH),
                    end.get(Calendar.DAY_OF_MONTH)
                )
                binding.titleTv.text = getString(R.string.brisk_mins_this_week)
            }
            PeriodTypeEnum.MONTHS -> {
                binding.walkPeriodTv.text = DateHelper.getMonthFullName(start)
                binding.titleTv.text = getString(R.string.brisk_mins_this_month)
            }
            else -> {
                return
            }
        }

        binding.closeBTN.setOnClickListener { finish() }
    }

    private fun setUpWalkRv(steps: MutableList<StepOverview>) {
        val adapter = WalkPresentationAdapter(periodType, steps)
        binding.walkRv.adapter = adapter
    }

    override fun onWalkDataReceived(stepList: MutableList<StepOverview>) {
        val today = System.currentTimeMillis()
        if (today in startTimestampMs..endTimestampMs) {
            stepList.add(0, StepOverview(today, todayBrisk, todayBrisk))
        }

        for (step in stepList) {
            steps.find { DateHelper.isSameDay(it.timestamp ?: 0, step.timestamp ?: 0) }?.let {
                it.totalBriskMin = step.totalBriskMin
                it.totalWalkMin = step.totalWalkMin
            }
        }

        setUpWalkRv(steps)
    }

    private fun createSteps() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startTimestampMs
        val daysCount = DateHelper.getDateDiffInDays(startTimestampMs, endTimestampMs) + 1
        for (i in 0 until daysCount) {
            steps.add(StepOverview(calendar.timeInMillis, 0, 0))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }
}