package com.flipsidegroup.active10.data.persistance.local

import com.flipsidegroup.active10.data.*
import com.flipsidegroup.active10.data.models.DailyStepData
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.data.models.api.DiscoverCategory
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.CurrentWalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.PauseResume
import com.flipsidegroup.active10.data.models.dataholders.WalkingPlanEntity
import com.flipsidegroup.active10.data.persistance.AppDatabase
import io.reactivex.Flowable
import io.reactivex.Single



class LocalRepositoryImpl(private val appDatabase: AppDatabase) :
    LocalRepository {

    override fun getFirstActivity(): StepOverview? {
        return appDatabase.getFirstActivity()
    }

    override fun getFirstActivityStartsFrom(start: Long): StepOverview? {
        return appDatabase.getFirstActivityStartFrom(start)
    }

    override fun getAllActivities(onDataLoaded: AppDatabase.OnDataLoadedListener<List<StepOverview>>) {
        appDatabase.getAllActivities(onDataLoaded)
    }

    override fun getAllActivities(): Single<List<StepOverview>> {
        return Single.create { emitter ->
            appDatabase.getAllActivities(object :
                AppDatabase.OnDataLoadedListener<List<StepOverview>> {
                override fun onDataLoaded(data: List<StepOverview>) {
                    emitter.onSuccess(data)
                }
            })
        }
    }

    override fun getActivitiesOnDays(
        startDay: Long,
        endDay: Long,
        onDataLoaded: AppDatabase.OnDataLoadedListener<List<StepOverview>>
    ) {
        appDatabase.getActivitiesOnDays(startDay, endDay, onDataLoaded)
    }

    override fun getActivitiesOnDays(startDay: Long, endDay: Long): Single<List<StepOverview>> {
        return Single.create { emitter ->
            appDatabase.getActivitiesOnDays(startDay, endDay, object :
                AppDatabase.OnDataLoadedListener<List<StepOverview>> {
                override fun onDataLoaded(data: List<StepOverview>) {
                    emitter.onSuccess(data)
                }
            })
        }
    }

    override fun getSortedActivitiesOnDays(
        startDay: Long,
        endDay: Long,
        onDataLoaded: AppDatabase.OnDataLoadedListener<List<StepOverview>>
    ) {
        appDatabase.getSortedActivitiesOnDays(startDay, endDay, onDataLoaded)
    }

    override fun deleteActivitiesBetween(startDay: Long, endDay: Long) {
        appDatabase.deleteActivitiesBetween(startDay, endDay)
    }

    override fun deleteAllActivities() {
        appDatabase.deleteAllActivities()
    }

    override fun persistDailyActivity(
        timestamp: Long,
        dailyStepData: DailyStepData
    ) {
        persistActivity(
            StepOverview(
                timestamp = timestamp,
                totalBriskMin = dailyStepData.briskMinutes,
                totalWalkMin = dailyStepData.nonBriskMinutes + dailyStepData.briskMinutes,
                totalSteps = dailyStepData.totalSteps
            )
        )
    }

    override fun persistActivity(stepOverview: StepOverview) {
        appDatabase.persistActivity(stepOverview)
    }

    override fun persistGoalsContent(goals: List<Goal>) {
        appDatabase.persistGoalsContent(goals)
    }

    override fun getGoalsContent(onDataLoaded: AppDatabase.OnDataLoadedListener<List<Goal>>) {
        appDatabase.getGoalsContent(onDataLoaded)
    }

    override fun getGoalsContent(): Single<List<Goal>> {
        return appDatabase.getGoalsContent()
    }

    override fun getFaqContent(onDataLoaded: AppDatabase.OnDataLoadedListener<List<FaqItem>>) {
        appDatabase.getFaqContent(onDataLoaded)
    }

    override fun persistFaqContent(list: List<FaqItem>) {
        appDatabase.persistFaqContent(list)
    }

    override fun persistHowItWorksContent(list: List<HowItWorks>) {
        appDatabase.persistHowItWorksContent(list)
    }

    override fun getHowItWorksContent(onDataLoaded: AppDatabase.OnDataLoadedListener<List<HowItWorks>>) {
        appDatabase.getHowItWorksContent(onDataLoaded)
    }

    override fun persistTipsContent(list: List<Tip>) {
        appDatabase.persistTipContent(list)
    }

    override fun getTipsContent(onDataLoaded: AppDatabase.OnDataLoadedListener<List<Tip>>) {
        appDatabase.getTipsContent(onDataLoaded)
    }

    override fun persistWalkingMessages(walkingMessageResponse: WalkingMessageResponse) {
        appDatabase.persistWalkingMessages(walkingMessageResponse)
    }

    override fun getWalkingMessages(onDataLoaded: AppDatabase.OnDataLoadedListener<WalkingMessageResponse?>) {
        appDatabase.getWalkingMessages(onDataLoaded)
    }

    override fun persistAboutCommunity(aboutCommunity: AboutCommunity) {
        appDatabase.persistAboutCommunity(aboutCommunity)
    }

    override fun getAboutCommunity(listener: AppDatabase.OnDataLoadedListener<AboutCommunity?>) {
        appDatabase.getAboutCommunity(listener)
    }

    override fun persistOnboarding(onboarding: Onboarding) {
        appDatabase.persistOnboarding(onboarding)
    }

    override fun getOnboarding(listener: AppDatabase.OnDataLoadedListener<Onboarding?>) {
        appDatabase.getOnboarding(listener)
    }

    override fun persistNotifications(notifications: Notifications) {
        appDatabase.persistNotifications(notifications)
    }

    override fun getNotifications(listener: AppDatabase.OnDataLoadedListener<Notifications?>) {
        appDatabase.getNotifications(listener)
    }

    override fun persistGlobalRules(globalRules: GlobalRules) {
        appDatabase.persistGlobalRules(globalRules)
    }

    override fun getGlobalRules(listener: AppDatabase.OnDataLoadedListener<GlobalRules?>) {
        appDatabase.getGlobalRules(listener)
    }

    override fun unregisterDataListener() {
        appDatabase.unregisterDataListener()
    }

    override fun persistRewardBadges(rewards: List<RewardBadge>) {
        appDatabase.persistRewardBadges(rewards)
    }

    override fun persistLegalRules(legalRules: List<LegalRules>) {
        appDatabase.persistLegalRules(legalRules)
    }

    override fun persistScreenContents(screenContents: List<ScreenContent>) {
        appDatabase.persistScreenContents(screenContents)
    }

    override fun getScreenContents(): Single<List<ScreenContent>> {
        return Single.create { emitter ->
            appDatabase.getScreenContents(object :
                AppDatabase.OnDataLoadedListener<List<ScreenContent>> {
                override fun onDataLoaded(data: List<ScreenContent>) {
                    emitter.onSuccess(data)
                }
            })
        }
    }

    override fun persistWalkingPlans(walkingPlan: List<WalkingPlan>) {
        appDatabase.persistWalkingPlans(walkingPlan)
    }

    override fun getWalkingPlans(): Single<List<WalkingPlan>> {
        return Single.create { emitter ->
            appDatabase.getWalkingPlans(object :
                AppDatabase.OnDataLoadedListener<List<WalkingPlan>> {
                override fun onDataLoaded(data: List<WalkingPlan>) {
                    emitter.onSuccess(data)
                }
            })
        }
    }

    override fun subscribeUserWalkingPlans() : Flowable<List<WalkingPlanEntity>> {
        return appDatabase.subscribeUserWalkingPlans()
    }

    override fun getWalkingPlanEntity(): Single<List<WalkingPlanEntity>> {
        return Single.create { emitter ->
            appDatabase.getWalkingPlanEntity(object :
                AppDatabase.OnDataLoadedListener<List<WalkingPlanEntity>> {
                override fun onDataLoaded(data: List<WalkingPlanEntity>) {
                    emitter.onSuccess(data)
                }
            })
        }
    }

    override fun getUserWalkingPlans(): Single<List<CurrentWalkingPlan>> {
        return Single.create { emitter ->
            appDatabase.getUserWalkingPlans(object :
                AppDatabase.OnDataLoadedListener<List<CurrentWalkingPlan>> {
                override fun onDataLoaded(data: List<CurrentWalkingPlan>) {
                    emitter.onSuccess(data)
                }
            })
        }
    }

    override fun persistDiscoveryArticles(discoveryArticles: List<InfoPage>) {
        appDatabase.persistDiscoveryArticles(discoveryArticles)
    }

    override fun getDiscoveryArticles(): Single<List<InfoPage>> {
        return Single.create { emitter ->
            appDatabase.getDiscoveryArticles(object :
                AppDatabase.OnDataLoadedListener<List<InfoPage>> {
                override fun onDataLoaded(data: List<InfoPage>) {
                    emitter.onSuccess(data)
                }
            })
        }
    }

    override fun persistDiscoveryCategories(discoveryCategories: List<DiscoverCategory>) {
        appDatabase.persistDiscoveryCategories(discoveryCategories)
    }

    override fun getDiscoveryCategories(): Single<List<DiscoverCategory>> {
        return Single.create { emitter ->
            appDatabase.getDiscoveryCategories(object :
                AppDatabase.OnDataLoadedListener<List<DiscoverCategory>> {
                override fun onDataLoaded(data: List<DiscoverCategory>) {
                    emitter.onSuccess(data)
                }
            })
        }
    }

    override fun deleteWalkingPlanEntity() {
        appDatabase.deleteWalkingPlanEntity()
    }

    override fun getPauseResume(): Single<List<PauseResume>>{
        return Single.create { emitter ->
            appDatabase.getAllPauseResume(object :
                AppDatabase.OnDataLoadedListener<List<PauseResume>> {
                override fun onDataLoaded(data: List<PauseResume>) {
                    emitter.onSuccess(data)
                }
            })
        }
    }

    override fun persistWalkingPlanEntity(userPlans: WalkingPlanEntity) {
        appDatabase.persistWalkingPlanEntity(userPlans)
    }

    override fun getLegalRules(listener: AppDatabase.OnDataLoadedListener<List<LegalRules>>) {
        appDatabase.getLegalRules(listener)
    }

    override fun removeRewardBadges() {
        appDatabase.removeRewardBadges()
    }

    override fun getActivitiesDaysNumber(startDay: Long, endDay: Long): Int {
        return appDatabase.getActivitiesDaysNumber(startDay, endDay)
    }

    override fun hasActivityOnDay(day: Long): Boolean {
        return appDatabase.hasActivityOnDay(day)
    }

    override fun getRewardBadges(onDataLoaded: AppDatabase.OnDataLoadedListener<List<RewardBadge>>) {
        appDatabase.getRewardBadges(onDataLoaded)
    }

    override fun registerDataListener(listener: AppDatabase.OnDataChange) {
        appDatabase.registerDataListener(listener)
    }
}