package com.flipsidegroup.active10.data.persistance.login

import com.flipsidegroup.active10.data.ActivityLevelEnum
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.models.api.NhsActivityRequest
import com.flipsidegroup.active10.data.models.api.NhsActivityResponse
import com.flipsidegroup.active10.data.models.api.NhsDailyTargetResponse
import com.flipsidegroup.active10.data.models.response.NhsUserDetailsResponse
import io.reactivex.Completable
import io.reactivex.Single
import java.util.Optional

interface LoginRepository {

    fun exchangeAuthorizationCode(code: String, codeVerifier: String): Completable

    fun getUserDetails(): Single<Optional<NhsUserDetailsResponse>>

    fun getActivitiesForDate(date: Long): Single<List<NhsActivityResponse>>

    fun getActivitiesFromTo(start: Long, end: Long): Single<List<NhsActivityResponse>>

    fun postActivities(activities: NhsActivityRequest): Completable

    fun postActivitiesBulk(month: Long, activities: List<NhsActivityRequest>): Completable

    fun postDailyTarget(date: Long, target: Int): Completable

    fun getDailyTargetForDate(date: Long): Single<List<NhsDailyTargetResponse>>

    fun getDailyTargetFromTo(start: Long, end: Long): Single<List<NhsDailyTargetResponse>>

    fun getDailyTargetById(id: String): Single<NhsDailyTargetResponse>

    fun updateDailyTarget(id: String, date: Long, target: Int): Completable

    fun deleteDailyTarget(id: String): Completable

    fun logout(): Completable

    fun disconnect(): Completable

    fun postSubscribeEmailPref(): Completable

    fun postUnsubscribeEmailPref(): Completable

    fun postActivityLevel(activityLevel: ActivityLevelEnum): Completable

    fun postMotivations(motivations: List<Goal>): Completable
}
