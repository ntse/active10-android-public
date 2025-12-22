package com.flipsidegroup.active10.presentation.mywalks.presenter

import android.content.Context
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.*
import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.newapi.DiscoverRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.mywalks.view.MyWalksView
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.WalkDataGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import org.joda.time.DateTime
import timber.log.Timber
import java.util.*
import javax.inject.Inject

private const val DAYS_PERIOD_LIMIT = 7
private const val WEEKS_PERIOD_LIMIT = 5
private const val ACTIVE_10 = 10

class MyWalkPresenterImpl
@Inject constructor(
    private val context: Context,
    private val settingsUtils: SettingsUtils,
    private val localRepository: LocalRepository,
    private val discoverRepository: DiscoverRepository,
    private val screenRepository: ScreenRepository,
) : BasePresenter<MyWalksView>(), MyWalksPresenter {

    private var walkingMessages: WalkingMessageResponse? = null

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun unregisterDataListener() {
        localRepository.unregisterDataListener()
    }

    private var currentPeriodType = PeriodTypeEnum.DAYS

    override fun registerDataListener() {
        localRepository.registerDataListener(object : AppDatabase.OnDataChange {
            override fun onDataChange() {
                view?.onWalkDataChanged()
                when (currentPeriodType) {
                    PeriodTypeEnum.DAYS -> {
                        getDaysIntervals(context)
                    }
                    PeriodTypeEnum.WEEKS -> {
                        getWeeksIntervals(context)
                    }
                    PeriodTypeEnum.MONTHS -> {
                        getMonthsIntervals(context)
                    }
                }
            }
        })
    }

    override fun getDaysIntervals(context: Context?) {
        val intervals = ArrayList<IntervalWalk>()
        val start = DateHelper.getStartOfDay(Calendar.getInstance())
        val periodMinimum = getPeriodMinDate(
            DateTime.now().minusDays(DAYS_PERIOD_LIMIT).withTimeAtStartOfDay().millis
        )
        val end = DateHelper.getEndOfDay(Calendar.getInstance())
        var dayOfWeek: String
        do {
            dayOfWeek = if (DateHelper.isSameDay(start)) {
                UIUtils.getString(R.string.today)
            } else {
                DateHelper.getDayOfWeek(start)
            }
            intervals.add(
                IntervalWalk(
                    dayOfWeek,
                    startTimestamp = start.timeInMillis,
                    endTimestamp = end.timeInMillis
                )
            )
            start.add(Calendar.DATE, -1)
            end.add(Calendar.DATE, -1)
        } while (intervals.size < DAYS_PERIOD_LIMIT && DateHelper.isSameOrPreviousDate(
                periodMinimum,
                start
            )
        )

        view?.setUpPeriods(intervals)
    }

    override fun getWeeksIntervals(context: Context?) {
        val intervals = ArrayList<IntervalWalk>()
        val installed = getPeriodMinDate(
            DateTime.now().minusWeeks(WEEKS_PERIOD_LIMIT).withTimeAtStartOfDay().millis
        )
        if (installed.firstDayOfWeek == Calendar.SUNDAY &&
            installed.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        ) {
            installed.add(Calendar.WEEK_OF_MONTH, -1)
        }
        installed.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val start = DateHelper.getStartOfCurrentWeek()
        val end = DateHelper.getEndOfCurrentWeek()
        var week: String
        do {
            week = if (start.get(Calendar.MONTH) == end.get(Calendar.MONTH)) {
                UIUtils.getString(
                    R.string.my_walk_week_period,
                    DateHelper.getMonthName(start),
                    start.get(Calendar.DAY_OF_MONTH),
                    end.get(Calendar.DAY_OF_MONTH)
                )
            } else {
                UIUtils.getString(
                    R.string.my_walk_week_period_diff_month,
                    DateHelper.getMonthName(start),
                    start.get(Calendar.DAY_OF_MONTH),
                    DateHelper.getMonthName(end),
                    end.get(Calendar.DAY_OF_MONTH)
                )
            }
            intervals.add(
                IntervalWalk(
                    week,
                    startTimestamp = start.timeInMillis,
                    endTimestamp = end.timeInMillis
                )
            )
            start.add(Calendar.WEEK_OF_MONTH, -1)
            end.add(Calendar.WEEK_OF_MONTH, -1)
        } while (intervals.size < WEEKS_PERIOD_LIMIT && DateHelper.isSameOrPreviousDate(
                installed,
                start
            )
        )
        view?.setUpPeriods(intervals)
    }

    override fun getMonthsIntervals(context: Context?) {
        val intervals = ArrayList<IntervalWalk>()
        val start = DateHelper.getStartOfCurrentMonth()
        val end = DateHelper.getEndOfCurrentMonth()
        val installed = getPeriodMinDate(0)
        installed.set(Calendar.DAY_OF_MONTH, 1)
        do {
            intervals.add(
                IntervalWalk(
                    DateHelper.getMonthWithYearName(start),
                    startTimestamp = start.timeInMillis,
                    endTimestamp = end.timeInMillis,
                    year = start.get(Calendar.YEAR)
                )
            )
            start.add(Calendar.MONTH, -1)
            end.add(Calendar.MONTH, -1)
            end.set(Calendar.DAY_OF_MONTH, start.getActualMaximum(Calendar.DATE))
        } while (DateHelper.isSameOrPreviousDate(installed, start))
        view?.setUpPeriods(intervals)
    }

    override fun getPeriodRewards(currentPeriodType: PeriodTypeEnum, interval: IntervalWalk) {
        if (settingsUtils.getSettingsHolder().generateRandomWalkData == true
            || settingsUtils.getSettingsHolder().generate1Active10WalkData == true
            || settingsUtils.getSettingsHolder().generate5Active10WalkData == true
        ) {
            val stepIntervalData = WalkDataGenerator.getStepDataByInterval(interval)
            view?.setUpRewards(
                stepIntervalData.totalWalk,
                stepIntervalData.briskWalk,
                stepIntervalData.rewards,
                stepIntervalData.targetCount,
                stepIntervalData.briskWalk / 10
            )
            view?.loadBarChart(WalkDataGenerator.getStepData(), interval)
        } else {
            getActivitiesData(interval)
        }
    }

    override fun getTodayRewards(briskWalk: Int, totalWalk: Int) {
        val rewards = ArrayList<Reward>()

        val active10Count = briskWalk / ACTIVE_10
        rewards.add(Reward(ACTIVE_10, active10Count))
        val remainCount = briskWalk - active10Count * ACTIVE_10
        if (remainCount != 0) {
            rewards.add(Reward(remainCount, 1))
        }

        val targetHit = if (isDayRewarded(Date().time, active10Count)) {
            1
        } else {
            0
        }
        view?.setUpTodayRewards(rewards, targetHit, active10Count)
    }

    private fun getPeriodMinDate(startFrom: Long): Calendar {
        if (settingsUtils.getSettingsHolder().generateRandomWalkData == true) {
            return WalkDataGenerator.generateInstalledDate()
        } else {
            val stepOverview = localRepository.getFirstActivityStartsFrom(startFrom)
            val calendar = Calendar.getInstance()
            calendar.time = Date()

            stepOverview?.let {
                calendar.time = Date(stepOverview.timestamp ?: return calendar)
            }

            return calendar
        }
    }

    private fun getActivitiesData(interval: IntervalWalk) {
        localRepository.getActivitiesOnDays(interval.startTimestamp, interval.endTimestamp, object :
            AppDatabase.OnDataLoadedListener<List<StepOverview>> {
            override fun onDataLoaded(data: List<StepOverview>) {
                var totalWalk = 0
                var briskWalk = 0
                val rewards = ArrayList<Reward>()

                for (i in ACTIVE_10 downTo 0) {
                    rewards.add(Reward(i, 0))
                }

                var active10Count = 0
                var remainCount: Int
                var targetDays = 0

                for (stepOverview in data) {
                    totalWalk += stepOverview.totalWalkMin ?: 0
                    briskWalk += stepOverview.totalBriskMin ?: 0
                    active10Count = (stepOverview.totalBriskMin ?: 0) / ACTIVE_10
                    remainCount = (stepOverview.totalBriskMin ?: 0) - active10Count * ACTIVE_10
                    if (remainCount != 0) {
                        rewards.find { it.id == remainCount }?.let { it.count++ }
                    }

                    rewards.find { it.id == ACTIVE_10 }?.let { it.count += active10Count }
                    stepOverview.timestamp?.let {
                        targetDays += if (isDayRewarded(it, active10Count)) {
                            1
                        } else {
                            0
                        }
                    }
                }

                view?.setUpRewards(totalWalk, briskWalk, rewards, targetDays, active10Count)
                view?.loadBarChart(data.toMutableList(), interval)
            }
        })
    }

    private fun isDayRewarded(dayTimestamp: Long, active10Count: Int): Boolean {
        val target =
            settingsUtils.getSettingsHolder().targetList?.findLast { it.timestamp <= dayTimestamp }
        val dayTargetCount = target?.target ?: 1
        return active10Count >= dayTargetCount
    }

    override fun getWalkingMessages(targetHit: Int, briskWalk: Int, active10: Int) {
        if (walkingMessages == null) {

            localRepository.getWalkingMessages(object :
                AppDatabase.OnDataLoadedListener<WalkingMessageResponse?> {
                override fun onDataLoaded(data: WalkingMessageResponse?) {
                    walkingMessages = data
                    view?.onMessagesReceived(targetHit, briskWalk, data, active10)
                }
            })
        } else {
            view?.onMessagesReceived(targetHit, briskWalk, walkingMessages!!, active10)
        }
    }

    override fun getGlobalRules() {
        localRepository.getGlobalRules(object : AppDatabase.OnDataLoadedListener<GlobalRules?> {
            override fun onDataLoaded(data: GlobalRules?) {
                var myWalksTimestamp = settingsUtils.getSettingsHolder().myWalksTimestamp
                if (myWalksTimestamp == null) {
                    myWalksTimestamp = System.currentTimeMillis()
                    settingsUtils.updateSettings(SettingsDataHolder(myWalksTimestamp = myWalksTimestamp))
                }

                val showMissingDataDialog =
                    if (settingsUtils.getSettingsHolder().showMissingAlertSooner == true) {
                        DateHelper.isOlderThan10Minute(myWalksTimestamp)
                    } else {
                        DateHelper.isOlderThan30Days(myWalksTimestamp)
                    }

                if (showMissingDataDialog) {
                    view?.onGlobalRulesRetrieved(data)
                }

                settingsUtils.updateSettings(SettingsDataHolder(myWalksTimestamp = System.currentTimeMillis()))
            }
        })
    }

    override fun getAllRewardsList() {
        localRepository.getRewardBadges(object :
            AppDatabase.OnDataLoadedListener<List<RewardBadge>> {
            override fun onDataLoaded(data: List<RewardBadge>) {
                view?.onRewardBadgesReceived(data)
            }
        })
    }

    override fun updateCurrentPeriod(period: PeriodTypeEnum) {
        currentPeriodType = period
    }

    override fun goToArticle150() {
        discoverRepository
            .getArticleBySlug("how_much_activity")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { article ->
                    if (article.isAvailable) article.get()?.let { view?.goToArticle(it) }
                },
                { error -> Timber.e(error, "get widget introduce article error") }
            ).addToDisposables()
    }

    override fun getMyWalksChildrenItems() {
        view?.showLoading()

        presenterScope.launch {
            runCatching {
                getFuncItems().await()
            }.onSuccess { funcItems ->
                val isEmailAllowed =
                    settingsUtils.getSettingsHolder().nhsUser?.isEmailUpdatesAllowed ?: false

                val items = funcItems.mapNotNull { item ->
                    if ((!isEmailAllowed && item.slug == "monthly_report") ||
                        (isEmailAllowed && item.slug == "monthly_report_opt_out")) {
                        null
                    } else {
                        item.description = item.description.replace(
                            "{name}",
                            "<strong>${settingsUtils.getSettingsHolder().nhsUser?.firstName ?: "Hey"}</strong>"
                        )
                        item
                    }
                }
                view?.showChildrenItems(items)
            }.onFailure {
                view?.showAlert(it)
            }

            view?.hideLoading()
        }
    }

    private fun getFuncItems() = screenRepository.getScreenContentBySlug("my_walks")
        .flatMap { screenRepository.getScreensByIds(it.childrenIds.orEmpty()) }
        .subscribeOn(Schedulers.io())

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}