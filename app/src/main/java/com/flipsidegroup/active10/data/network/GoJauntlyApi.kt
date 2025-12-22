package com.flipsidegroup.active10.data.network

import com.flipsidegroup.active10.data.models.requests.CircularWalksRequest
import com.flipsidegroup.active10.data.models.requests.CuratedWalkByIdRequest
import com.flipsidegroup.active10.data.models.requests.CuratedWalksRequest
import com.flipsidegroup.active10.data.models.response.CircularWalkResponse
import com.flipsidegroup.active10.data.models.response.CuratedWalk
import com.flipsidegroup.active10.data.models.response.CuratedWalksResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Singleton

@JvmSuppressWildcards
@Singleton
interface GoJauntlyApi {

    @POST("v1/routing/circular/collection")
    fun getCircularWalks(@Body circularWalksRequest: CircularWalksRequest, @Header("Authorization") token: String): Single<List<CircularWalkResponse>>

    @POST("v1/curated-walks/search")
    fun getCuratedWalks(@Body curatedWalksRequest: CuratedWalksRequest, @Header("Authorization") token: String): Single<CuratedWalksResponse>

    @POST("v1/curated-walks/{id}")
    fun getCuratedWalkById(@Path("id") id: String, @Body curatedWalkByIdRequest: CuratedWalkByIdRequest, @Header("Authorization") token: String): Single<CuratedWalk>

}