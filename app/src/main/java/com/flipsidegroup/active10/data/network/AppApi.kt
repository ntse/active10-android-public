package com.flipsidegroup.active10.data.network

import com.flipsidegroup.active10.data.*
import com.flipsidegroup.active10.data.models.Goal
import io.reactivex.Single
import retrofit2.http.GET


interface AppApi {

    @GET("faq/")
    fun getFaqList(): Single<List<FaqItem>>

    @GET("about-one-you/")
    fun getAboutCommunity(): Single<AboutCommunity>

    @GET("goals/")
    fun getGoalsList(): Single<List<Goal>>

    @GET("dynamic-texts/")
    fun getWalkingMessages(): Single<WalkingMessageResponse>

    @GET("how-it-works/")
    fun getHowItWorksList(): Single<List<HowItWorks>>

    @GET("tips/")
    fun getTips(): Single<List<Tip>>

    @GET("onboarding/")
    fun getOnboarding(): Single<Onboarding>

    @GET("notifications/")
    fun getNotifications(): Single<Notifications>

    @GET("global_rules/")
    fun getGlobalRules(): Single<GlobalRules>

    @GET("legals/")
    fun getLegalList(): Single<List<LegalRules>>
}