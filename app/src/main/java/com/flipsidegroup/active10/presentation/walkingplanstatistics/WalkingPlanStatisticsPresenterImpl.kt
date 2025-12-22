package com.flipsidegroup.active10.presentation.walkingplanstatistics

import com.flipside.briskcounter.data.BriskActivity
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.CurrentWalkingPlanDay
import com.flipsidegroup.active10.data.models.dataholders.PauseResume
import com.flipsidegroup.active10.data.models.dataholders.WalkingPlanEntity
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.persistance.newapi.WalkingPlanRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanBottomSheetDialog
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanCommonDialog
import com.flipsidegroup.active10.presentation.usecases.ChangePlanStateUseCase
import com.flipsidegroup.active10.utils.WalkingPlanState
import com.flipsidegroup.active10.utils.awaitWithLoading
import com.flipsidegroup.active10.utils.blockWithCustomLaunch
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val DAYS_IN_WEEK = 7

data class WalkingPlanContentResult(
    val plansContent: List<WalkingPlan>,
    val viewContent: ScreenContent,
    val funcItems: List<ScreenContent>,
)

data class WalkingPlanContentWithDataResult(
    val currentPlans: List<WalkingPlanEntity>,
    val result: WalkingPlanContentResult,
    val todaySession: BriskActivity,
)

class WalkingPlanStatisticsPresenterImpl @Inject constructor(
    private val settingsUtils: SettingsUtils,
    private val screenRepository: ScreenRepository,
    private val walkingPlanRepository: WalkingPlanRepository,
    private val preferenceRepository: PreferenceRepository,
    private val localNotificationRepository: LocalNotificationRepository,
    private val changePlanStateUseCase: ChangePlanStateUseCase,
) : BasePresenter<WalkingPlanStatisticsView>(), WalkingPlanStatisticsPresenter {

    private val currentSession: BehaviorSubject<BriskActivity> = BehaviorSubject.create()
    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())
    private var plansContent = emptyList<WalkingPlan>()
    private var planId = -1L
    private var weekNum = -1
    private var checkPopupInThisSession = true
    private var showCurrentWeek = false

    override fun loadContent(planId: Long) {
        view?.showLoading()
        this.planId = planId

        localNotificationRepository.setUserNoWalkingActivePlan(planId)

        val content =
            Single.zip(getPlanContent(), getViewContent(), getFuncItems(), this::createContentResult)
                .subscribeOn(Schedulers.io())

        Observable.combineLatest(
            subscribeUserPlans().toObservable(),
            content.toObservable(),
            currentSession.debounce(0, TimeUnit.MILLISECONDS),
            ::WalkingPlanContentWithDataResult
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (currentPlans, result, todaySession) ->
                val finalResult = setContent(currentPlans, result, todaySession)
                view?.showContent(finalResult)
                view?.hideLoading()
            }, {
                Timber.e(it)
                view?.showAlert(it)
                view?.hideLoading()
            }).addToDisposables()
    }

    private fun createContentResult(
        walkingPlan: List<WalkingPlan>,
        screenContent: ScreenContent,
        funcItems: List<ScreenContent>
    ) = WalkingPlanContentResult(
        plansContent = walkingPlan,
        viewContent = screenContent,
        funcItems = funcItems,
    )

    private fun getDaysForSelectedWeek(
        days: List<CurrentWalkingPlanDay>,
        currentWeekIndex: Int
    ): List<CurrentWalkingPlanDay>? {
        val startIndex = currentWeekIndex * DAYS_IN_WEEK
        if (startIndex !in days.indices) return null
        val endIndex = minOf(startIndex + DAYS_IN_WEEK, days.size)
        return days.subList(startIndex, endIndex)
    }

    private fun setContent(
        currentPlans: List<WalkingPlanEntity>,
        result: WalkingPlanContentResult,
        todaySession: BriskActivity,
    ): List<WalkingPlanStatisticsPart> {

        if (currentPlans.firstOrNull()?.currentWalkingPlan == null) {
            Timber.d("Empty current plan or no walking plan data")
            return emptyList()
        }

        plansContent = result.plansContent
        val planContent = result.plansContent.first { it.id == planId }
        val userPlansData = currentPlans.first {
            it.currentWalkingPlan?.planId == planId
        }
        val currentPlan = userPlansData.currentWalkingPlan
            ?: throw (IllegalArgumentException("This plan isn't exist as current"))
        val viewContent = result.viewContent
        val funcItems = result.funcItems

        val isEmailAllowed =
            settingsUtils.getSettingsHolder().nhsUser?.isEmailUpdatesAllowed ?: false

        val planState = currentPlan.getEnumState()

        val allUserDays = currentPlan.days.size
        val allCmsDays = planContent.planItineraryItems.size * DAYS_IN_WEEK
        val todaySessionNonBriskMin = todaySession.minutesOfWalk - todaySession.minutesOfBrisk
        val currentDayInWeekIndex = if (allUserDays < allCmsDays) (allUserDays % DAYS_IN_WEEK) else (DAYS_IN_WEEK - 1)
        val currentWeekIndex = if (allUserDays < allCmsDays) (allUserDays / DAYS_IN_WEEK) else (planContent.planItineraryItems.size - 1)
        val todayIndex =
            currentPlan.days.indexOfFirst { LocalDate.parse(it.timestamp) == LocalDate.now() }
        val currentUserWeek = planContent.planItineraryItems.sortedBy { it.id }.getOrNull(currentWeekIndex)
        weekNum = currentWeekIndex + 1

        val isPlanCompleted = allUserDays >= allCmsDays
        // Copy of past user days from database + today's day from Google Fit
        val finalUserDays =
            currentPlan.days.sortedBy { LocalDate.parse(it.timestamp) }.toMutableList()
        if (allUserDays < allCmsDays) {
            if (todayIndex == -1) {
                finalUserDays
                    .add(
                        CurrentWalkingPlanDay(
                            timestamp = LocalDate.now().toString(),
                            totalBriskMin = todaySession.minutesOfBrisk,
                            totalNonBriskMin = todaySessionNonBriskMin
                        )
                    )
            } else {
                finalUserDays[todayIndex] =
                    CurrentWalkingPlanDay(
                        timestamp = LocalDate.now().toString(),
                        totalBriskMin = todaySession.minutesOfBrisk,
                        totalNonBriskMin = todaySessionNonBriskMin
                    )
            }
        }

        val content = buildList {
            add(
                WalkingPlanStatisticsPart.Statistics(
                    state = planState,
                    planName = planContent.planName,
                    currentWeekIndex = currentWeekIndex,
                    currentDayIndex = currentDayInWeekIndex,
                    totalDays = DAYS_IN_WEEK,
                    totalWeeks = planContent.planItineraryItems.sortedBy { it.id }.mapIndexed { index, week ->
                        StatisticsWeek(
                            briskData = StatisticsInfo(
                                currentWeekMinutes = getDaysForSelectedWeek(finalUserDays, index)?.sumOf { it.totalBriskMin } ?: 0,
                                totalWeekMinutes = week.dailyBriskMinutes.sum()
                            ),
                            nonBriskData = StatisticsInfo(
                                currentWeekMinutes = getDaysForSelectedWeek(finalUserDays, index)?.sumOf { it.totalNonBriskMin } ?: 0,
                                totalWeekMinutes = week.dailyNonBriskMinutes.sum()
                            )
                        )
                    },
                    currentDayBriskMin = todaySession.minutesOfBrisk,
                    currentDayNonBriskMin = todaySessionNonBriskMin,
                    totalDayBriskMin = currentUserWeek?.dailyBriskMinutes?.getOrNull(currentDayInWeekIndex) ?: 0,
                    totalDayNonBriskMin = currentUserWeek?.dailyNonBriskMinutes?.getOrNull(currentDayInWeekIndex) ?: 0,
                    showCurrentWeek = showCurrentWeek
                )
            )
            when (planState) {
                WalkingPlanState.ACTIVE -> {
                    add(
                        WalkingPlanStatisticsPart.PrimaryButton(
                            icon = R.drawable.paused_plan_icon,
                            title = "Pause walking plan",
                            onClickCallback = {
                                view?.showDialog(
                                    WalkingPlanCommonDialog(
                                        slug = WalkingPlanCommonDialog.WALKING_PLAN_PAUSE_DIALOG,
                                        onFirstButton = presenterScope.blockWithCustomLaunch {
                                            changePlanStateUseCase(WalkingPlanState.PAUSED)
                                                .awaitWithLoading(view)
                                        }
                                    )
                                )
                            }
                        )
                    )
                }

                WalkingPlanState.PAUSED -> {
                    add(
                        WalkingPlanStatisticsPart.PrimaryButton(
                            title = "Continue walking plan",
                            onClickCallback = {
                                view?.showDialog(
                                    WalkingPlanCommonDialog(
                                        slug = WalkingPlanCommonDialog.WALKING_PLAN_RESTART_DIALOG,
                                        onFirstButton = presenterScope.blockWithCustomLaunch {
                                            changePlanStateUseCase(WalkingPlanState.ACTIVE, planId)
                                                .awaitWithLoading(view)
                                            view?.onPlanResumed()
                                        }
                                    )
                                )
                            }
                        )
                    )
                }

                else -> {}
            }
            add(
                WalkingPlanStatisticsPart.SecondaryButton(
                    title = "Your walking plan",
                    onClickCallback = {
                        view?.goBack()
                    }
                )
            )
            add(
                WalkingPlanStatisticsPart.AdviceItem(
                    title = "Advice",
                    description = "Start with a slow minute warm-up before hitting your brisk pace! Complete the goal via bigger intervals with a short casual recovery every few minutes where needed!"
                )
            )
            addAll(
                funcItems.mapNotNull {
                    if ((!isEmailAllowed && it.slug == "monthly_report") ||
                        (isEmailAllowed && it.slug == "monthly_report_opt_out")
                    ) {
                        null
                    } else {
                        WalkingPlanStatisticsPart.FuncItem(
                            itemId = it.id,
                            slug = it.slug,
                            title = it.title,
                            description = it.description.replace(
                                "{name}",
                                "<strong>${settingsUtils.getSettingsHolder().nhsUser?.firstName ?: "Hey"}</strong>"
                            ),
                            actionTitle = it.actionTitle ?: "",
                            actionSlug = it.actionSlug ?: "",
                            image = it.firstImageUrl ?: "",
                            onClickCallback = { actionSlug -> view?.funcItemAction(actionSlug) }
                        )
                    }
                }
            )
        }

        if (checkPopupInThisSession) {
            checkBottomDialogs(
                currentWeekIndex = currentWeekIndex,
                planContent = planContent,
                finalDays = finalUserDays,
                isPlanCompleted = isPlanCompleted,
            )
        }

        showCurrentWeek = false

        return content
    }

    private fun calculateWeekSumOfCmsBrisk(
        weekIndex: Int,
        planContent: WalkingPlan,
    ) = (planContent.planItineraryItems.sortedBy { it.id }.getOrNull(weekIndex)?.dailyBriskMinutes?.sum() ?: 0)
        .toDouble()

    private fun calculateWeekSumOfUserBrisk(
        weekIndex: Int,
        finalDays: List<CurrentWalkingPlanDay>
    ) = (getDaysForSelectedWeek(finalDays, weekIndex)
        ?.sumOf { it.totalBriskMin } ?: -1).toDouble()

    private fun isWeekTargetHit(
        weekIndex: Int,
        planContent: WalkingPlan,
        finalDays: List<CurrentWalkingPlanDay>
    ): Boolean {
        val weekCmsTarget = calculateWeekSumOfCmsBrisk(weekIndex, planContent)
        if (weekCmsTarget == 0.0) return false
        val weekUserTarget = calculateWeekSumOfUserBrisk(weekIndex, finalDays)
        val result = weekUserTarget in weekCmsTarget..<(weekCmsTarget * 1.2)
        return result
    }

    private fun isWeekTargetExceeded(
        weekIndex: Int,
        inRowWeeksCount: Int,
        planContent: WalkingPlan,
        finalDays: List<CurrentWalkingPlanDay>
    ): Boolean {
        val result = (0..<inRowWeeksCount).map { index ->
            val weekCmsTarget = calculateWeekSumOfCmsBrisk(weekIndex - index, planContent)
            if (weekCmsTarget == 0.0) return false
            val weekUserTarget = calculateWeekSumOfUserBrisk(weekIndex - index, finalDays)
            weekUserTarget >= weekCmsTarget * 1.2
        }.all { it }
        return result
    }

    private fun isWeekTargetMissed(
        weekIndex: Int,
        inRowWeeksCount: Int,
        planContent: WalkingPlan,
        finalDays: List<CurrentWalkingPlanDay>
    ): Boolean {
        val result = (0..<inRowWeeksCount).map { index ->
            val weekCmsTarget = calculateWeekSumOfCmsBrisk(weekIndex - index, planContent)
            if (weekCmsTarget == 0.0) return false
            val weekUserTarget = calculateWeekSumOfUserBrisk(weekIndex - index, finalDays)
            weekUserTarget in 0.0..<weekCmsTarget
        }.all { it }
        return result
    }

    private fun checkBottomDialogs(
        currentWeekIndex: Int,
        planContent: WalkingPlan,
        finalDays: List<CurrentWalkingPlanDay>,
        isPlanCompleted: Boolean,
    ) {
        if (isPlanCompleted
            && checkMissedPopups(currentWeekIndex, planContent, finalDays)
        ) {
            preferenceRepository.lastWeekWithShownMissedPopup = currentWeekIndex
            checkPopupInThisSession = false
            return
        } else if (isPlanCompleted
            && checkPlanCompletedPopups(planContent)
        ) {
            checkPopupInThisSession = false
            return
        }

        if (preferenceRepository.lastWeekWithShownHitPopup < currentWeekIndex
            && checkHitPopups(currentWeekIndex, planContent, finalDays)
        ) {
            preferenceRepository.lastWeekWithShownHitPopup = currentWeekIndex
            checkPopupInThisSession = false
        } else if (preferenceRepository.lastWeekWithShownExceededPopup < currentWeekIndex
            && checkExceededPopups(currentWeekIndex, planContent, finalDays)
        ) {
            preferenceRepository.lastWeekWithShownExceededPopup = currentWeekIndex
            checkPopupInThisSession = false
        } else if (preferenceRepository.lastWeekWithShownMissedPopup < currentWeekIndex
            && checkMissedPopups(currentWeekIndex - 1, planContent, finalDays)
        ) {
            preferenceRepository.lastWeekWithShownMissedPopup = currentWeekIndex
            checkPopupInThisSession = false
        }
    }

    private fun checkPlanCompletedPopups(
        planContent: WalkingPlan,
    ): Boolean {
        if (planContent.superiorPlanIds.isEmpty()) {
            loadBottomDialog(
                slug = PLAN_POPUP_HIT_TARGET_NO_NEXT_PLAN,
                onFirstButton = presenterScope.blockWithCustomLaunch {
                    changePlanStateUseCase(WalkingPlanState.COMPLETED).awaitWithLoading(view)
                    view?.goToCouchAdvert()
                },
                onSecondButton = presenterScope.blockWithCustomLaunch {
                    changePlanStateUseCase(WalkingPlanState.COMPLETED).awaitWithLoading(view)
                    view?.goBack()
                },
                showConfetti = true,
            )
            return true
        } else if (planContent.superiorPlanIds.isNotEmpty()) {
            loadBottomDialog(
                slug = PLAN_POPUP_HIT_TARGET_NEXT_PLAN,
                onFirstButton = presenterScope.blockWithCustomLaunch {
                    changePlanStateUseCase(WalkingPlanState.COMPLETED).awaitWithLoading(view)
                    view?.goToAnotherPlan(planContent.superiorPlanIds.first()!!)
                },
                onSecondButton = {},
                showConfetti = true,
            )
            return true
        }
        return false
    }

    private fun checkHitPopups(
        currentWeekIndex: Int,
        planContent: WalkingPlan,
        finalDays: List<CurrentWalkingPlanDay>,
    ): Boolean {
        val isCurrentWeekTargetHit = isWeekTargetHit(currentWeekIndex, planContent, finalDays)

        if (isCurrentWeekTargetHit) {
            loadBottomDialog(
                slug = PLAN_POPUP_HIT_TARGET,
                onFirstButton = {},
                onSecondButton = {},
                showConfetti = true,
            )
            return true
        }
        return false
    }

    private fun checkExceededPopups(
        currentWeekIndex: Int,
        planContent: WalkingPlan,
        finalDays: List<CurrentWalkingPlanDay>,
    ): Boolean {
        val isCurrentWeekTargetExceeded =
            isWeekTargetExceeded(currentWeekIndex, 1, planContent, finalDays)

        if (isWeekTargetExceeded(currentWeekIndex, 3, planContent, finalDays)
            && planContent.superiorPlanIds.isEmpty()) {
            loadBottomDialog(
                slug = PLAN_POPUP_EXCEED_TARGET_3_WEEKS_NO_NEXT_PLAN,
                onFirstButton = {},
                onSecondButton = presenterScope.blockWithCustomLaunch {
                    changePlanStateUseCase(WalkingPlanState.CANCELLED).awaitWithLoading(view)
                    view?.goToCouchAdvert()
                },
                showConfetti = true,
            )
            return true
        } else if (isWeekTargetExceeded(currentWeekIndex, 3, planContent, finalDays)
            && planContent.superiorPlanIds.isNotEmpty()) {
            loadBottomDialog(
                slug = PLAN_POPUP_EXCEED_TARGET_3_WEEKS,
                onFirstButton = {},
                onSecondButton = presenterScope.blockWithCustomLaunch {
                    changePlanStateUseCase(WalkingPlanState.CANCELLED).awaitWithLoading(view)
                    view?.goToAnotherPlan(planContent.superiorPlanIds.first()!!)
                },
                showConfetti = true,
            )
            return true
        } else if (isCurrentWeekTargetExceeded) {
            loadBottomDialog(
                slug = PLAN_POPUP_EXCEED_TARGET,
                onFirstButton = {},
                onSecondButton = {},
                showConfetti = true,
            )
            return true
        }
        return false
    }

    private fun checkMissedPopups(
        weekIndex: Int,
        planContent: WalkingPlan,
        finalDays: List<CurrentWalkingPlanDay>
    ): Boolean {
        if (isWeekTargetMissed(weekIndex, 2, planContent, finalDays)
            && planContent.inferiorPlanIds.isEmpty()) {
            loadBottomDialog(
                slug = PLAN_POPUP_MISSED_TARGET_2_WEEKS_NO_PREV_PLAN,
                onFirstButton = presenterScope.blockWithCustomLaunch {
                    showCurrentWeek = true
                    walkingPlanRepository.resetCurrentPlan().awaitWithLoading(view)
                    preferenceRepository.lastWeekWithShownHitPopup = -1
                    preferenceRepository.lastWeekWithShownExceededPopup = -1
                    preferenceRepository.lastWeekWithShownMissedPopup = -1
                },
                onSecondButton = {},
            )
            return true
        } else if (isWeekTargetMissed(weekIndex, 2, planContent, finalDays)
            && planContent.inferiorPlanIds.isNotEmpty()) {
            loadBottomDialog(
                slug = PLAN_POPUP_MISSED_TARGET_2_WEEKS,
                onFirstButton = presenterScope.blockWithCustomLaunch {
                    showCurrentWeek = true
                    walkingPlanRepository.deleteDaysFromCurrentWeek().awaitWithLoading(view)
                },
                onSecondButton = presenterScope.blockWithCustomLaunch {
                    changePlanStateUseCase(WalkingPlanState.CANCELLED).awaitWithLoading(view)
                    view?.goToAnotherPlan(planContent.inferiorPlanIds.first()!!)
                },
            )
            return true
        } else if (isWeekTargetMissed(weekIndex, 1, planContent, finalDays)) {
            loadBottomDialog(
                slug = PLAN_POPUP_MISSED_TARGET,
                onFirstButton = presenterScope.blockWithCustomLaunch {
                    walkingPlanRepository.deleteDaysFromCurrentWeek().awaitWithLoading(view)
                },
                onSecondButton = {},
            )
            return true
        }
        return false
    }

    private fun getPlanContent() = walkingPlanRepository
        .getAllWalkingPlans()
        .subscribeOn(Schedulers.io())

    private fun getViewContent() = screenRepository
        .getScreenContentBySlug(ScreenRepository.WALKING_PLAN_VIEW)
        .subscribeOn(Schedulers.io())

    private fun getFuncItems() = screenRepository.getScreenContentBySlug("my_walking_plans_view")
        .flatMap { screenRepository.getScreensByIds(it.childrenIds.orEmpty()) }
        .subscribeOn(Schedulers.io())

    private fun subscribeUserPlans() = walkingPlanRepository
        .subscribeUserWalkingPlans()
        .subscribeOn(Schedulers.io())
        .doOnNext { showCurrentWeek = true }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }

    override fun onTodayActivity(briskActivity: BriskActivity) {
        currentSession.onNext(briskActivity)
    }

    override fun loadBriskData() {
        presenterScope.launch {
            runCatching {
                val pauseResumeList = walkingPlanRepository.getAllPauseResume().await()
                val currentPlan = walkingPlanRepository.getUserWalkingPlans().await()
                if (currentPlan.isEmpty()) {
                    Throwable(IllegalStateException("No current walking plan"))
                }
                pauseResumeList to currentPlan.first()
            }
                .onSuccess { result ->
                    val pauseResumeWithStartPlan = result.first.toMutableList()
                    pauseResumeWithStartPlan.add(
                        PauseResume(
                            paused = LocalDateTime.parse(result.second.startDate).with(LocalTime.MIN).toString(),
                            resumed = result.second.startDate,
                        ).toBriskPauseResume()
                    )
                    view?.doBriskCounter(pauseResumeWithStartPlan)
                }
                .onFailure {
                    Timber.e(it)
                }
        }
    }

    private fun loadBottomDialog(
        slug: String,
        onFirstButton: () -> Unit,
        onSecondButton: () -> Unit,
        showConfetti: Boolean = false
    ) {
        presenterScope.launch {
            runCatching {
                screenRepository.getScreenContentBySlug(slug).await()
            }
                .onSuccess {
                    view?.showDialog(
                        WalkingPlanBottomSheetDialog(
                            content = it
                                .apply { title = title.replace("{week}", "$weekNum") },
                            onFirstButton = onFirstButton,
                            onSecondButton = onSecondButton,
                        ),
                        showConfetti = showConfetti
                    )
                }
                .onFailure {
                    view?.showAlert(it)
                }
        }
    }

    companion object {
        const val PLAN_POPUP_HIT_TARGET = "plan_popup_hit_target"
        const val PLAN_POPUP_HIT_TARGET_NEXT_PLAN = "plan_popup_hit_target_next_plan"
        const val PLAN_POPUP_HIT_TARGET_NO_NEXT_PLAN = "plan_popup_hit_target_no_next_plan"
        const val PLAN_POPUP_EXCEED_TARGET = "plan_popup_exceeded_target"
        const val PLAN_POPUP_EXCEED_TARGET_3_WEEKS = "plan_popup_exceeded_target_3_weeks"
        const val PLAN_POPUP_EXCEED_TARGET_3_WEEKS_NO_NEXT_PLAN = "plan_popup_exceeded_target_3_weeks_no_next_plan"
        const val PLAN_POPUP_MISSED_TARGET = "plan_popup_missed_target"
        const val PLAN_POPUP_MISSED_TARGET_2_WEEKS = "plan_popup_missed_target_2_weeks"
        const val PLAN_POPUP_MISSED_TARGET_2_WEEKS_NO_PREV_PLAN = "plan_popup_missed_target_2_weeks_no_prev_plan"
    }

}