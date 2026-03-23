package com.flipsidegroup.active10.data.network

import com.flipsidegroup.active10.data.models.api.NhsActivityBulkRequest
import com.flipsidegroup.active10.data.models.api.NhsActivityRequest
import com.flipsidegroup.active10.data.models.api.NhsActivityResponse
import com.flipsidegroup.active10.data.models.api.NhsDailyTargetRequest
import com.flipsidegroup.active10.data.models.api.NhsDailyTargetResponse
import com.flipsidegroup.active10.data.models.api.WalkingPlanDTO
import com.flipsidegroup.active10.data.models.requests.EmailPreferenceRequest
import com.flipsidegroup.active10.data.models.requests.LatestActivityLevelRequest
import com.flipsidegroup.active10.data.models.requests.LatestMotivationRequest
import com.flipsidegroup.active10.data.models.response.NhsTokenExchangeResponse
import com.flipsidegroup.active10.data.models.response.NhsUserDetailsResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

@JvmSuppressWildcards
@Singleton
interface LoginApi {

    @FormUrlEncoded
    @POST("nhs_login/token")
    fun exchangeAuthorizationCode(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("code_verifier") codeVerifier: String,
    ): Single<NhsTokenExchangeResponse>

    @GET("v1/users/")
    fun getUserDetails(@Header("Authorization") token: String): Single<NhsUserDetailsResponse>

    @POST("v1/users/email_preferences/subscribe")
    fun postSubscribeEmailPref(
        @Header("Authorization") token: String,
        @Body body: EmailPreferenceRequest,
    ): Completable

    @POST("v1/users/email_preferences/unsubscribe")
    fun postUnsubscribeEmailPref(
        @Header("Authorization") token: String,
        @Body body: EmailPreferenceRequest,
    ): Completable

    @GET("v1/walking_plans")
    fun getUserWalkingPlans(@Header("Authorization") token: String): Single<WalkingPlanDTO>

    @DELETE("v1/walking_plans")
    fun deleteUserWalkingPlans(@Header("Authorization") token: String): Completable

    @POST("v1/walking_plans")
    fun postUserWalkingPlans(
        @Header("Authorization") token: String,
        @Body walkingPlan: WalkingPlanDTO
    ): Completable

    @POST("nhs_login/logout")
    fun logout(@Header("Authorization") token: String): Completable

    @POST("nhs_login/disconnect")
    fun disconnect(@Header("Authorization") token: String): Completable

    @POST("v1/activities")
    fun postActivities(
        @Header("Authorization") token: String,
        @Body activities: NhsActivityRequest
    ): Completable

    @GET("v1/activities")
    fun getActivitiesFromTo(
        @Header("Authorization") token: String,
        @Query("start_date") start: Long,
        @Query("end_date") end: Long
    ): Single<List<NhsActivityResponse>>

    @GET("v1/activities")
    fun getActivitiesForDate(
        @Header("Authorization") token: String,
        @Query("date") date: Long
    ): Single<List<NhsActivityResponse>>

    @POST("v1/migrations/activities")
    fun postActivitiesBulk(
        @Header("Authorization") token: String,
        @Body activities: NhsActivityBulkRequest
    ): Completable

    @POST("v1/daily_targets")
    fun postDailyTarget(
        @Header("Authorization") token: String,
        @Body dailyTargets: NhsDailyTargetRequest
    ): Completable

    @GET("v1/daily_targets")
    fun getDailyTargets(
        @Header("Authorization") token: String,
        @Query("date") date: Long?,
        @Query("start_date") start: Long?,
        @Query("end_date") end: Long?,
        @Query("min_daily_target") minDailyTarget: Int?,
        @Query("max_daily_target") maxDailyTarget: Int?
    ): Single<List<NhsDailyTargetResponse>>

    @GET("v1/daily_targets/{target_id}")
    fun getDailyTarget(
        @Header("Authorization") token: String,
        @Path("target_id") id: String
    ): Single<NhsDailyTargetResponse>

    @PUT("v1/daily_targets/{target_id}")
    fun putDailyTarget(
        @Header("Authorization") token: String,
        @Path("target_id") id: String,
        @Body dailyTarget: NhsDailyTargetRequest
    ): Completable

    @DELETE("v1/daily_targets/{target_id}")
    fun deleteDailyTarget(
        @Header("Authorization") token: String,
        @Path("target_id") id: String
    ): Completable

    @POST("v1/activity_level/")
    fun postLatestActivityLevel(
        @Header("Authorization") token: String,
        @Body activityLevel: LatestActivityLevelRequest
    ): Completable

    @POST("v1/motivations/")
    fun postLatestMotivations(
        @Header("Authorization") token: String,
        @Body motivations: LatestMotivationRequest
    ): Completable

}
