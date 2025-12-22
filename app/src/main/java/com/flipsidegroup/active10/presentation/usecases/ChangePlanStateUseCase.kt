package com.flipsidegroup.active10.presentation.usecases

import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.CurrentWalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.PauseResume
import com.flipsidegroup.active10.data.models.dataholders.WalkingPlanEntity
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.WalkingPlanRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.utils.BadArgumentException
import com.flipsidegroup.active10.utils.EarnBadgeHelper
import com.flipsidegroup.active10.utils.WalkingPlanState
import com.flipsidegroup.active10.utils.retryWithDelay
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

class ChangePlanStateUseCase @Inject constructor(
    private val localNotificationRepository: LocalNotificationRepository,
    private val walkingPlanRepository: WalkingPlanRepository,
    private val preferenceRepository: PreferenceRepository,
    private val localRepository: LocalRepository,
    private val settingsUtils: SettingsUtils,
    private val isPlanMatchToUserUseCase: IsPlanMatchToUserUseCase
) {

    operator fun invoke(newState: WalkingPlanState, cmsPlanId: Long? = null): Completable {
        return Single.zip(
            localRepository.getWalkingPlanEntity(),
            localRepository.getWalkingPlans()
        ) { entity, cmsPlans -> Pair(entity, cmsPlans) }
            .subscribeOn(Schedulers.io())
            .doOnError {
                Timber.d("Error changing walking plan state: $it")
            }
            .flatMapCompletable { pair ->
                val userPlans = pair.first.firstOrNull()
                val cmsPlans = pair.second

                val result = when (newState) {
                    WalkingPlanState.ACTIVE -> {
                        cmsPlanId?.let {
                            setAsActivePlan(userPlans, cmsPlanId = it)
                        }
                    }

                    WalkingPlanState.PAUSED -> {
                        userPlans?.let {
                            setAsPausedPlan(it)
                        }
                    }

                    WalkingPlanState.COMPLETED -> {
                        userPlans?.let {
                            setAsCompletedPlan(userPlans)
                                .doFinally {
                                    setReward(userPlans, cmsPlans)
                                }
                        }
                    }

                    WalkingPlanState.CANCELLED -> {
                        userPlans?.let {
                            setAsCancelledPlan(userPlans)
                        }
                    }

                    else -> null

                } ?: throw BadArgumentException("Lack of correct argument value")

                result
            }

    }

    private fun setAsActivePlan(userPlansData: WalkingPlanEntity?, cmsPlanId: Long): Completable {
        val currentTime = LocalDateTime.now()
        val userPlanDataTemp = userPlansData ?: WalkingPlanEntity()
        if (userPlanDataTemp.currentWalkingPlan?.planId != cmsPlanId) {
            if (userPlanDataTemp.currentWalkingPlan?.planId != null) {
                userPlanDataTemp.moveCurrentPlanToHistoricalPlan(WalkingPlanState.COMPLETED)
            }
        }
        if (userPlanDataTemp.currentWalkingPlan?.state == WalkingPlanState.ACTIVE.name.lowercase()) {
            return Completable.complete()
        }
        val currentPlanTemp = userPlanDataTemp.currentWalkingPlan?.apply {
            state = WalkingPlanState.ACTIVE.name.lowercase()
            pauseResume.firstOrNull { it.resumed.isEmpty() }?.resumed = LocalDateTime.now().toString()
        } ?: CurrentWalkingPlan(
            planId = cmsPlanId,
            startDate = currentTime.toString(),
            state = WalkingPlanState.ACTIVE.name.lowercase(),
        )
        userPlanDataTemp.apply {
            currentWalkingPlan = currentPlanTemp
        }
        checkNotificationConditions(WalkingPlanState.ACTIVE, cmsPlanId)
        return walkingPlanRepository.saveWalkingPlanEntity(userPlanDataTemp)
    }

    private fun setAsPausedPlan(userPlansData: WalkingPlanEntity): Completable {
        val userPlanTemp = userPlansData.currentWalkingPlan
        if (userPlanTemp?.state != WalkingPlanState.PAUSED.name.lowercase()) {
            userPlanTemp?.apply {
                state = WalkingPlanState.PAUSED.name.lowercase()
                pauseResume.add(PauseResume(paused = LocalDateTime.now().toString()))
            } ?: return Completable.error(Throwable("No active plan found"))
        }
        checkNotificationConditions(WalkingPlanState.PAUSED, userPlanTemp.planId)
        return walkingPlanRepository.saveWalkingPlanEntity(userPlansData).retryWithDelay()
    }

    private fun setAsCancelledPlan(userPlansData: WalkingPlanEntity): Completable {
        if (userPlansData.currentWalkingPlan == null) {
            return Completable.complete()
        }
        userPlansData.moveCurrentPlanToHistoricalPlan(WalkingPlanState.CANCELLED)
        checkNotificationConditions(WalkingPlanState.CANCELLED)
        return walkingPlanRepository.saveWalkingPlanEntity(userPlansData)
    }

    private fun setAsCompletedPlan(userPlansData: WalkingPlanEntity): Completable {
        if (userPlansData.currentWalkingPlan == null) {
            return Completable.complete()
        }
        userPlansData.moveCurrentPlanToHistoricalPlan(WalkingPlanState.COMPLETED)
        checkNotificationConditions(WalkingPlanState.COMPLETED)
        return walkingPlanRepository.saveWalkingPlanEntity(userPlansData)
    }

    private fun setReward(walkingPlanEntity: WalkingPlanEntity, cmsPlans: List<WalkingPlan>) {
        val allUserCmsPlans = cmsPlans.mapNotNull { plan ->
            if (!isPlanMatchToUserUseCase.invoke(cmsPlan = plan, checkForPlanHeroBadge = true)) {
                return@mapNotNull null
            } else {
                plan
            }
        }

        val completedPlansCount = walkingPlanEntity.walkingPlanHistory
            .filter { it.status == WalkingPlanState.COMPLETED.name.lowercase() }
            .distinctBy { it.planId }
            .count { historicalPlans ->
                allUserCmsPlans.any { it.id == historicalPlans.planId }
            }

        EarnBadgeHelper.saveEarnedBadgeWithoutCheck(
            settingsUtils = settingsUtils,
            badge = RewardBadgeEnum.TARGET_HITTER,
            timestamp = System.currentTimeMillis(),
            preferenceRepository = preferenceRepository
        )

        EarnBadgeHelper.saveEarnedBadge(
            settingsUtils = settingsUtils,
            badge = RewardBadgeEnum.STARTING_STRONG,
            timestamp = System.currentTimeMillis(),
            preferenceRepository = preferenceRepository
        )

        if (completedPlansCount >= allUserCmsPlans.size) {
            EarnBadgeHelper.saveEarnedBadge(
                settingsUtils = settingsUtils,
                badge = RewardBadgeEnum.WALKING_PLAN_HERO,
                timestamp = System.currentTimeMillis(),
                preferenceRepository = preferenceRepository
            )
        }
    }

    private fun checkNotificationConditions(state: WalkingPlanState?, planId: Long? = null) {
        state ?: return

        when {
            (state == WalkingPlanState.PAUSED && planId != null) -> {
                localNotificationRepository.setUserPausedPlan(planId)
            }
            (state == WalkingPlanState.ACTIVE && planId != null) -> {
                localNotificationRepository.setUserNoWalkingActivePlan(planId)
                preferenceRepository.showMWPCompletedNotification = false
            }
            (state == WalkingPlanState.COMPLETED) -> {
                preferenceRepository.showMWPCompletedNotification = true
                localNotificationRepository.cancelUserPausedPlan()
                localNotificationRepository.cancelUserNoWalkingActivePlan()
            }
            else -> {
                localNotificationRepository.cancelUserPausedPlan()
                localNotificationRepository.cancelUserNoWalkingActivePlan()
            }
        }
    }
}