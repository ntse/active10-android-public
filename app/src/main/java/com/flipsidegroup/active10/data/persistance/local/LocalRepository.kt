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

interface LocalRepository {

    fun persistDailyActivity(
        timestamp: Long,
        dailyStepData: DailyStepData
    )

    fun persistActivity(stepOverview: StepOverview)

    fun getAllActivities(onDataLoaded: AppDatabase.OnDataLoadedListener<List<StepOverview>>)

    fun getAllActivities(): Single<List<StepOverview>>

    fun getActivitiesOnDays(
        startDay: Long,
        endDay: Long,
        onDataLoaded: AppDatabase.OnDataLoadedListener<List<StepOverview>>
    )

    fun getActivitiesOnDays(
        startDay: Long,
        endDay: Long,
    ): Single<List<StepOverview>>

    fun getSortedActivitiesOnDays(
        startDay: Long,
        endDay: Long,
        onDataLoaded: AppDatabase.OnDataLoadedListener<List<StepOverview>>
    )

    fun deleteActivitiesBetween(startDay: Long, endDay: Long)

    fun deleteAllActivities()

    /** ONLY goals from CMS **/
    fun persistGoalsContent(goals: List<Goal>)

    /** ONLY goals from CMS **/
    fun getGoalsContent(onDataLoaded: AppDatabase.OnDataLoadedListener<List<Goal>>)

    /** ONLY goals from CMS **/
    fun getGoalsContent(): Single<List<Goal>>

    fun persistFaqContent(list: List<FaqItem>)

    fun getFaqContent(onDataLoaded: AppDatabase.OnDataLoadedListener<List<FaqItem>>)

    fun persistHowItWorksContent(list: List<HowItWorks>)

    fun getHowItWorksContent(onDataLoaded: AppDatabase.OnDataLoadedListener<List<HowItWorks>>)

    fun getTipsContent(onDataLoaded: AppDatabase.OnDataLoadedListener<List<Tip>>)

    fun persistTipsContent(list: List<Tip>)

    fun persistWalkingMessages(walkingMessageResponse: WalkingMessageResponse)

    fun getWalkingMessages(onDataLoaded: AppDatabase.OnDataLoadedListener<WalkingMessageResponse?>)

    fun getFirstActivity(): StepOverview?

    fun getFirstActivityStartsFrom(start: Long): StepOverview?

    fun persistAboutCommunity(aboutCommunity: AboutCommunity)

    fun getAboutCommunity(listener: AppDatabase.OnDataLoadedListener<AboutCommunity?>)

    fun persistOnboarding(onboarding: Onboarding)

    fun getOnboarding(listener: AppDatabase.OnDataLoadedListener<Onboarding?>)

    fun persistNotifications(notifications: Notifications)

    fun getNotifications(listener: AppDatabase.OnDataLoadedListener<Notifications?>)

    fun persistGlobalRules(globalRules: GlobalRules)

    fun getGlobalRules(listener: AppDatabase.OnDataLoadedListener<GlobalRules?>)

    fun getLegalRules(listener: AppDatabase.OnDataLoadedListener<List<LegalRules>>)

    fun registerDataListener(listener: AppDatabase.OnDataChange)

    fun unregisterDataListener()

    fun persistRewardBadges(rewards: List<RewardBadge>)

    fun persistLegalRules(legalRules: List<LegalRules>)

    fun persistScreenContents(screenContents: List<ScreenContent>)

    fun getScreenContents(): Single<List<ScreenContent>>

    fun persistWalkingPlans(walkingPlan: List<WalkingPlan>)

    fun getWalkingPlans(): Single<List<WalkingPlan>>

    fun subscribeUserWalkingPlans(): Flowable<List<WalkingPlanEntity>>

    /**
     * Note: There should be at most one WalkingPlanEntity in the database, no more.
     */
    fun getWalkingPlanEntity(): Single<List<WalkingPlanEntity>>

    fun getUserWalkingPlans(): Single<List<CurrentWalkingPlan>>

    fun persistDiscoveryArticles(discoveryArticles: List<InfoPage>)

    fun getDiscoveryArticles(): Single<List<InfoPage>>

    fun persistDiscoveryCategories(discoveryCategories: List<DiscoverCategory>)

    fun getDiscoveryCategories(): Single<List<DiscoverCategory>>

    fun getRewardBadges(onDataLoaded: AppDatabase.OnDataLoadedListener<List<RewardBadge>>)

    fun removeRewardBadges()

    fun getActivitiesDaysNumber(
        startDay: Long,
        endDay: Long
    ): Int

    fun hasActivityOnDay(day: Long): Boolean

    fun getPauseResume(): Single<List<PauseResume>>

    fun persistWalkingPlanEntity(userPlans: WalkingPlanEntity)

    fun deleteWalkingPlanEntity()
}