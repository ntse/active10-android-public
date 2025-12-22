package com.flipsidegroup.active10.data.network

import com.flipsidegroup.active10.data.models.api.DiscoverCategory
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import javax.inject.Singleton

@Singleton
interface NewAppApi {

    @GET("categories/?format=json")
    fun getDiscoverCategory(): Single<List<DiscoverCategory>>

    @GET("articles/?format=json")
    fun getDiscoverArticles(): Single<List<InfoPage>>

    @GET("views?format=json")
    fun getScreenContents(): Single<List<ScreenContent>>

    @GET("walking-plans/plans/?format=json")
    fun getWalkingPlans(): Single<List<WalkingPlan>>
}
