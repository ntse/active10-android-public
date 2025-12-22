package com.flipsidegroup.active10.data.persistance

import com.flipsidegroup.active10.data.*
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.models.api.DiscoverCategory
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.models.response.CircularWalkResponse
import com.flipsidegroup.active10.data.models.response.CuratedWalksResponse
import io.reactivex.Observable
import io.reactivex.Single


interface AppRepository {

    fun getAboutCommunity(): Single<AboutCommunity>

    fun getFaqList(): Single<List<FaqItem>>

    fun getHowItWorksList(): Single<List<HowItWorks>>

    fun getTips(): Single<List<Tip>>

    fun getWalkingMessages(): Single<WalkingMessageResponse>

    fun getGoalsList(): Single<List<Goal>>

    fun getOnboarding(): Single<Onboarding>

    fun getNotifications(): Single<Notifications>

    fun getGlobalRules(): Single<GlobalRules>

    fun getRewardList(): Single<List<RewardBadge>>

    fun getLegalList(): Single<List<LegalRules>>

    fun getScreenContents(): Single<List<ScreenContent>>

    fun getWalkingPlans(): Single<List<WalkingPlan>>

    fun getDiscoverArticles(): Single<List<InfoPage>>

    fun getDiscoverCategories(): Single<List<DiscoverCategory>>
}