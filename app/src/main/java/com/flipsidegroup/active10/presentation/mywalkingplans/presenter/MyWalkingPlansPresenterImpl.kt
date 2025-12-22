package com.flipsidegroup.active10.presentation.mywalkingplans.presenter

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.WalkingPlanEntity
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.persistance.newapi.WalkingPlanRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.mywalkingplans.adapters.MyWalkingPlansPart
import com.flipsidegroup.active10.presentation.mywalkingplans.view.MyWalkingPlansView
import com.flipsidegroup.active10.presentation.usecases.IsPlanMatchToUserUseCase
import com.flipsidegroup.active10.utils.WalkingPlanState
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import timber.log.Timber
import javax.inject.Inject

data class MyWalkingPlansResult(
    val planContent: List<WalkingPlan>,
    val viewContent: ScreenContent,
    val funcItems: List<ScreenContent>,
    val userPlans: List<WalkingPlanEntity>,
)

class MyWalkingPlansPresenterImpl @Inject constructor(
    private val screenRepository: ScreenRepository,
    private val walkingPlanRepository: WalkingPlanRepository,
    private val settingsUtils: SettingsUtils,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val isPlanMatchToUserUseCase: IsPlanMatchToUserUseCase
) : BasePresenter<MyWalkingPlansView>(), MyWalkingPlansPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun loadContent() {
        view?.showLoading()

        val content =
            Single.zip(getPlans(), getView(), getFuncItems(), this::createContentResult)
                .subscribeOn(Schedulers.io())

        Observable.combineLatest(
            subscribeUserPlans().toObservable(),
            content.toObservable()
        ) { currentPlans, result ->
            currentPlans to result
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (currentPlans, result) ->
                val finalResult = setContent(currentPlans, result)
                view?.showData(finalResult)
                view?.hideLoading()
            }, {
                Timber.e(it)
                view?.showAlert(it)
                view?.hideLoading()
            }).addToDisposables()
    }

    private fun createContentResult(
        walkingPlans: List<WalkingPlan>,
        screenContent: ScreenContent,
        screenContents: List<ScreenContent>
    ) = MyWalkingPlansResult(walkingPlans, screenContent, screenContents, emptyList())

    private fun setContent(
        userPlans: List<WalkingPlanEntity>,
        result: MyWalkingPlansResult
    ): List<MyWalkingPlansPart> {

        val plans = result.planContent
        val viewContent = result.viewContent
        val funcItems = result.funcItems

        val isEmailAllowed =
            settingsUtils.getSettingsHolder().nhsUser?.isEmailUpdatesAllowed ?: false

        val plansMapped = plans.mapNotNull { plan ->
            val planState = userPlans.firstOrNull {
                it.currentWalkingPlan?.planId == plan.id
            }?.currentWalkingPlan?.getEnumState() ?: WalkingPlanState.IDLE

            if (planState == WalkingPlanState.IDLE &&
                !isPlanMatchToUserUseCase.invoke(cmsPlan = plan)) {
                return@mapNotNull null
            }

            MyWalkingPlansPart.Plan(
                state = planState,
                planId = plan.id,
                planName = plan.planName,
                planCode = plan.planCode,
                planTagline = plan.planTagline,
                planDuration = "${plan.planDurationWeeks} weeks",
                planDescription = plan.planDescription,
                image = plan.image ?: "",
                buttonTitle = viewContent.getPropertyValue("view_walking_plan") ?: "",
                onClickCallback = {
                    firebaseAnalyticsHelper.sendButtonClickedEvent(
                        "ViewWalkingPlan",
                        extraParams = mapOf("walking_plan_name" to plan.planName),
                    )
                    view?.navigateToWalkingPlan(plan.id)
                }
            )
        }.sortedWith(compareBy({ it.state == WalkingPlanState.IDLE }, { it.planCode }))

        val funcItemsMapped = funcItems.mapNotNull {
            if ((!isEmailAllowed && it.slug == "monthly_report") ||
                (isEmailAllowed && it.slug == "monthly_report_opt_out")
            ) {
                null
            } else {
                MyWalkingPlansPart.FuncItem(
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

        val list = buildList {
            addAll(plansMapped)
            addAll(funcItemsMapped)
        }
        
        return list
    }

    private fun getPlans() = walkingPlanRepository.getAllWalkingPlans().subscribeOn(Schedulers.io())

    private fun getFuncItems() = screenRepository.getScreenContentBySlug("my_walking_plans_view")
        .flatMap { screenRepository.getScreensByIds(it.childrenIds.orEmpty()) }
        .subscribeOn(Schedulers.io())

    private fun getView() = screenRepository.getScreenContentBySlug("my_walking_plans_view")
        .subscribeOn(Schedulers.io())

    private fun subscribeUserPlans() = walkingPlanRepository
        .subscribeUserWalkingPlans()
        .subscribeOn(Schedulers.io())

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}