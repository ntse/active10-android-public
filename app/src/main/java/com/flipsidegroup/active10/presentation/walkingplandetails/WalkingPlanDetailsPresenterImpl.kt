package com.flipsidegroup.active10.presentation.walkingplandetails

import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.CurrentWalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.WalkingPlanEntity
import com.flipsidegroup.active10.data.persistance.newapi.DiscoverRepository
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.persistance.newapi.WalkingPlanRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanCommonDialog
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanCommonDialog.Companion.WALKING_PLAN_RESTART_AFTER_DELAY_DIALOG
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanDebugDialog
import com.flipsidegroup.active10.presentation.usecases.ChangePlanStateUseCase
import com.flipsidegroup.active10.utils.WalkingPlanState
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.awaitWithLoading
import com.flipsidegroup.active10.utils.blockWithCustomLaunch
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import timber.log.Timber
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

data class WalkingPlanDetailsResult(
    val plansContent: List<WalkingPlan>,
    val viewContent: ScreenContent,
    val funcItems: List<ScreenContent>,
    val userPlan: CurrentWalkingPlan? = null,
)

class WalkingPlanDetailsPresenterImpl @Inject constructor(
    private val settingsUtils: SettingsUtils,
    private val screenRepository: ScreenRepository,
    private val walkingPlanRepository: WalkingPlanRepository,
    private val discoverRepository: DiscoverRepository,
    private val preferenceRepository: PreferenceRepository,
    private val localNotificationRepository: LocalNotificationRepository,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val changePlanStateUseCase: ChangePlanStateUseCase,
) : BasePresenter<WalkingPlanDetailsView>(), WalkingPlanDetailsPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())
    private var planId = -2L

    override fun checkIfUserIsLoggedIn() {
        walkingPlanRepository.checkIfUserIsLoggedIn()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { isLoggedIn ->
                    if (!isLoggedIn) {
                        view?.pop()
                    }
                },
                { error -> Timber.e(error, "checkIfUserIsLoggedIn error") }
            ).addToDisposables()
    }

    override fun loadContent(planId: Long) {
        view?.showLoading()
        this.planId = planId

        val content =
            Single.zip(getPlanContent(), getViewContent(), getFuncItems(), this::createContentResult)
                .subscribeOn(Schedulers.io())

        Observable.combineLatest(
            subscribeUserPlans().toObservable(),
            content.toObservable()
        ) { currentPlans, result -> currentPlans to result }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (currentPlans, result) ->
                val finalResult = setContent(currentPlans, result)
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
    ) = WalkingPlanDetailsResult(
        plansContent = walkingPlan,
        viewContent = screenContent,
        funcItems = funcItems,
    )

    private fun openPlan(planContent: WalkingPlan) {
        if (BuildConfig.DEBUG) {
            view?.showDialog(
                WalkingPlanDebugDialog(
                    cmsPlan = planContent,
                    onFirstButton = {
                        view?.goToWalkingPlanStatistics(planId)
                    },
                    onSecondButton = {
                        view?.goToWalkingPlanStatistics(planId)
                    }
                )
            )
        } else {
            view?.goToWalkingPlanStatistics(planId)
        }
    }

    private fun setContent(
        currentPlans: List<WalkingPlanEntity>,
        result: WalkingPlanDetailsResult
    ): List<WalkingPlanDetailsPart> {

        val planContent = result.plansContent.first { it.id == planId }
        val userPlansData = currentPlans.firstOrNull() ?: WalkingPlanEntity()
        val userPlan = userPlansData.currentWalkingPlan?.takeIf { it.planId == planId }
        val viewContent = result.viewContent
        val funcItems = result.funcItems

        val isEmailAllowed =
            settingsUtils.getSettingsHolder().nhsUser?.isEmailUpdatesAllowed ?: false

        val planState = userPlan?.getEnumState() ?: WalkingPlanState.IDLE

        val cancelBtnCase = WalkingPlanDetailsPart.SecondaryButton(
            title = viewContent.getPropertyValue(WALKING_PLAN_CANCEL_PLAN) ?: "",
            onClickCallback = {
                firebaseAnalyticsHelper.sendButtonClickedEvent(
                    "CancelMyWalkingPlan",
                    extraParams = mapOf("walking_plan_name" to planContent.planName),
                )
                view?.showDialog(
                    WalkingPlanCommonDialog(
                        slug = WalkingPlanCommonDialog.WALKING_PLAN_CANCEL_DIALOG,
                        onFirstButton = presenterScope.blockWithCustomLaunch {
                            changePlanStateUseCase(WalkingPlanState.CANCELLED).awaitWithLoading(view)
                        }
                    )
                )
            }
        )

        val content = buildList {
            add(
                WalkingPlanDetailsPart.TagImage(
                    planTagline = planContent.planTagline,
                    image = planContent.image ?: "",
                )
            )
            add(
                WalkingPlanDetailsPart.Overview(
                    state = planState,
                    planName = planContent.planName,
                    planDescription = planContent.planDescription,
                    findWalksBtnTitle = viewContent.getPropertyValue(WALKING_PLAN_FIND_WALKS) ?: "",
                    planDuration = planContent.planDuration,
                    planGoal = planContent.planGoal,
                    planDifficultyLevel = planContent.planDifficulty,
                    planItineraryTitle = viewContent.getPropertyValue(WALKING_PLAN_ITINERARY) ?: "",
                    planItineraryItems = planContent.planItineraryItems.sortedBy { it.id },
                )
            )
            when (planState) {
                WalkingPlanState.IDLE -> {
                    add(
                        WalkingPlanDetailsPart.PrimaryButton(
                            title = viewContent.getPropertyValue(WALKING_PLAN_START_PLAN) ?: "",
                            onClickCallback = { showStartPlanPopup(planContent) }
                        )
                    )
                }

                WalkingPlanState.ACTIVE -> {
                    add(
                        WalkingPlanDetailsPart.PrimaryButton(
                            title = viewContent.getPropertyValue(WALKING_PLAN_CONTINUE_PLAN) ?: "",
                            onClickCallback = {
                                firebaseAnalyticsHelper.sendButtonClickedEvent(
                                    "ContinueMyWalkingPlan",
                                    extraParams = mapOf("walking_plan_name" to planContent.planName),
                                )
                                openPlan(planContent)
                            }
                        )
                    )
                }

                WalkingPlanState.PAUSED -> {
                    add(
                        WalkingPlanDetailsPart.PrimaryButton(
                            title = viewContent.getPropertyValue(WALKING_PLAN_RESTART_PLAN) ?: "",
                            onClickCallback = presenterScope.blockWithCustomLaunch {
                                firebaseAnalyticsHelper.sendButtonClickedEvent(
                                    "RestartMyWalkingPlan",
                                    extraParams = mapOf("walking_plan_name" to planContent.planName),
                                )
                                changePlanStateUseCase(WalkingPlanState.ACTIVE, planId)
                                    .awaitWithLoading(view)
                                openPlan(planContent)
                            }
                        )
                    )
                }

                else -> {}
            }
            addAll(
                funcItems.mapNotNull {
                    if ((!isEmailAllowed && it.slug == "monthly_report") ||
                        (isEmailAllowed && it.slug == "monthly_report_opt_out")
                    ) {
                        null
                    } else {
                        WalkingPlanDetailsPart.FuncItem(
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
            when (planState) {
                WalkingPlanState.ACTIVE -> {
                    add(
                        WalkingPlanDetailsPart.SecondaryButton(
                            title = viewContent.getPropertyValue(WALKING_PLAN_PAUSE_PLAN) ?: "",
                            onClickCallback = {
                                firebaseAnalyticsHelper.sendButtonClickedEvent(
                                    "PauseMyWalkingPlan",
                                    extraParams = mapOf("walking_plan_name" to planContent.planName),
                                )
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

                WalkingPlanState.PAUSED -> add(cancelBtnCase)

                else -> {}
            }
        }

        if (userPlansData.currentWalkingPlan?.getEnumState() == WalkingPlanState.PAUSED) {
            checkPausedPopups(planContent, userPlansData)
        }

        return content
    }

    private fun checkPausedPopups(
        planContent: WalkingPlan,
        userPlansData: WalkingPlanEntity,
    ) {
        val activityLevel = planContent.categoryDetails?.activityLevel?.getOrNull(0) ?: return

        val subtractDays = when (activityLevel) {
            "Inactive" -> 7
            "Moderately active" -> 14
            "Active" -> 21
            else -> return
        }
        val isPlanPaused = userPlansData.currentWalkingPlan!!.pauseResume.any {
            it.resumed.isEmpty()
                    && LocalDateTime.parse(it.paused).with(LocalTime.MIN) <=
                    LocalDateTime.now().minusDays(subtractDays.toLong()).with(LocalTime.MIN)
        }

        if (isPlanPaused) {
            view?.showDialog(
                WalkingPlanCommonDialog(
                    slug = WALKING_PLAN_RESTART_AFTER_DELAY_DIALOG,
                    onFirstButton = presenterScope.blockWithCustomLaunch {
                        changePlanStateUseCase(WalkingPlanState.ACTIVE, planId)
                            .awaitWithLoading(view)
                    }
                )
            )
        }
    }

    private fun showStartPlanPopup(planContent: WalkingPlan) {
        view?.showDialog(
            WalkingPlanCommonDialog(
                slug = WalkingPlanCommonDialog.WALKING_PLAN_START_DIALOG,
                onFirstButton = presenterScope.blockWithCustomLaunch {
                    localNotificationRepository.cancelNotSelectedMWP()
                    changePlanStateUseCase(WalkingPlanState.ACTIVE, planId).awaitWithLoading(view)
                    preferenceRepository.lastWeekWithShownHitPopup = -1
                    preferenceRepository.lastWeekWithShownExceededPopup = -1
                    preferenceRepository.lastWeekWithShownMissedPopup = -1
                    openPlan(planContent)
                    firebaseAnalyticsHelper.sendButtonClickedEvent(
                        "StartMyWalkingPlan",
                        extraParams = mapOf("walking_plan_name" to planContent.planName),
                    )
                },
                planContent = planContent
            )
        )
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

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }

    companion object {
        const val WALKING_PLAN_START_PLAN = "start_walking_plan"
        const val WALKING_PLAN_FIND_WALKS = "find_walks_in_my_area"
        const val WALKING_PLAN_LEARN_BENEFITS = "learn_health_benefits"
        const val WALKING_PLAN_EARN_REWARDS_INFO = "earn_rewards_with_this_plan"
        const val WALKING_PLAN_ITINERARY = "walking_plan_itinerary"
        const val WALKING_PLAN_VIEW_PLAN = "view_walking_plan"
        const val WALKING_PLAN_PAUSE_PLAN = "pause_walking_plan"
        const val WALKING_PLAN_CANCEL_PLAN = "cancel_walking_plan"
        const val WALKING_PLAN_CONTINUE_PLAN = "continue_walking_plan"
        const val WALKING_PLAN_RESTART_PLAN = "restart_walking_plan"
    }

}