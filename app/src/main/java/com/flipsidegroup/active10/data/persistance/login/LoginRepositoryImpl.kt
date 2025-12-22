package com.flipsidegroup.active10.data.persistance.login

import com.flipsidegroup.active10.data.ActivityLevelEnum
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.models.api.NhsActivityBulkRequest
import com.flipsidegroup.active10.data.models.api.NhsActivityRequest
import com.flipsidegroup.active10.data.models.api.NhsActivityResponse
import com.flipsidegroup.active10.data.models.api.NhsDailyTargetRequest
import com.flipsidegroup.active10.data.models.api.NhsDailyTargetResponse
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.models.requests.EmailPreferenceRequest
import com.flipsidegroup.active10.data.models.requests.LatestActivityLevelRequest
import com.flipsidegroup.active10.data.models.requests.LatestMotivationRequest
import com.flipsidegroup.active10.data.models.response.NhsUserDetailsResponse
import com.flipsidegroup.active10.data.network.LoginApi
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.usecases.RemoveNhsUserDataUseCase
import com.flipsidegroup.active10.utils.Constants.NHSLogin.EMAIL_PREFERENCES_NAME
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.HttpException
import timber.log.Timber
import java.util.Optional

class LoginRepositoryImpl(
    private val localNotificationRepository: LocalNotificationRepository,
    private val settingsUtils: SettingsUtils,
    private val loginApi: LoginApi,
    private val removeNhsUserDataUseCase: RemoveNhsUserDataUseCase,
): LoginRepository {

    override fun postSubscribeEmailPref(): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken
        val body = EmailPreferenceRequest(EMAIL_PREFERENCES_NAME)
        return loginApi.postSubscribeEmailPref("Bearer $token", body)
            .doOnComplete { localNotificationRepository.cancelMonthlyEmailsAreOff() }
    }

    override fun postUnsubscribeEmailPref(): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken
        val body = EmailPreferenceRequest(EMAIL_PREFERENCES_NAME)
        return loginApi.postUnsubscribeEmailPref("Bearer $token", body)
            .doOnComplete { localNotificationRepository.setMonthlyEmailsAreOff() }
    }

    override fun getUserDetails(): Single<Optional<NhsUserDetailsResponse>> {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.d("No token found")
            settingsUtils.updateSettings(SettingsDataHolder(nhsUser = null))
            return Single.just(Optional.empty())
        }
        return loginApi.getUserDetails("Bearer $token")
            .map {
                settingsUtils.updateSettings(SettingsDataHolder(nhsUser = it.toEntity()))
                Optional.of(it)
            }
            .onErrorReturn {
                if (it is HttpException && it.code() == 403) {
                    removeNhsUserDataUseCase()
                }
                Timber.d("Error fetching user details: ${it.message}")
                Optional.empty()
            }
    }

    override fun getActivitiesForDate(date: Long): Single<List<NhsActivityResponse>> {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.d("No token found")
            return Single.just(emptyList())
        }
        return loginApi.getActivitiesForDate("Bearer $token", date)
            .onErrorReturn { error ->
                handle403Error(error)
                if (error is HttpException && error.code() == 404) {
                    // 404 means no activities found
                    Timber.d(error, "No activities found for the given date. (404 error)")
                    emptyList()
                } else {
                    Timber.e(error, "Error while fetching activities for $date")
                    throw error
                }
            }
    }

    override fun getActivitiesFromTo(start: Long, end: Long): Single<List<NhsActivityResponse>> {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.d("No token found")
            return Single.just(emptyList())
        }
        return loginApi.getActivitiesFromTo("Bearer $token", start, end)
            .onErrorReturn { error ->
                handle403Error(error)
                if (error is HttpException && error.code() == 404) {
                    // 404 means no activities found
                    Timber.d("No activities found for the given date range. (404 error)")
                    emptyList()
                } else if (error is HttpException && error.code() == 403) {

                    emptyList()
                } else {
                    Timber.e(error, "Error while fetching activities from $start to $end")
                    throw error
                }
            }
    }

    override fun postActivities(activities: NhsActivityRequest): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.d("No token found")
            return Completable.complete()
        }
        return loginApi.postActivities("Bearer $token", activities)
            .doOnError(::handle403Error)
    }

    override fun postActivitiesBulk(
        month: Long,
        activities: List<NhsActivityRequest>
    ): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.d("No token found")
            return Completable.complete()
        }
        return loginApi.postActivitiesBulk("Bearer $token", NhsActivityBulkRequest(month, activities))
            .doOnError(::handle403Error)
    }

    override fun postDailyTarget(date: Long, target: Int): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.d("No token found")
            return Completable.complete()
        }
        return loginApi.postDailyTarget("Bearer $token", NhsDailyTargetRequest(date, target))
            .doOnError(::handle403Error)
    }

    override fun getDailyTargetForDate(date: Long): Single<List<NhsDailyTargetResponse>> {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.d("No token found")
            return Single.just(emptyList())
        }
        return loginApi.getDailyTargets("Bearer $token", date, null, null, null, null)
            .onErrorReturn { error ->
                handle403Error(error)
                if (error is HttpException && error.code() == 404) {
                    // 404 means no activities found
                    Timber.d(error, "No daily target found for the given date. (404 error)")
                    emptyList()
                } else {
                    Timber.d(error, "Error while fetching daily target for date $date")
                    throw error
                }
            }
    }

    override fun getDailyTargetById(id: String): Single<NhsDailyTargetResponse> {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.d("No token found")
            return Single.just(NhsDailyTargetResponse("", 0, 0))
        }
        return loginApi.getDailyTarget("Bearer $token", id)
            .doOnError(::handle403Error)
    }

    override fun getDailyTargetFromTo(
        start: Long,
        end: Long
    ): Single<List<NhsDailyTargetResponse>> {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.d("No token found")
            return Single.just(emptyList())
        }
        return loginApi.getDailyTargets("Bearer $token", null, start, end, null, null)
            .onErrorReturn { error ->
                handle403Error(error)
                if (error is HttpException && error.code() == 404) {
                    // 404 means no activities found
                    Timber.d("No daily target found for the given date range from $start to $end. (404 error)")
                    emptyList()
                } else {
                    Timber.e(error, "Error while fetching daily target from $start to $end")
                    throw error
                }
            }
    }


    override fun updateDailyTarget(id: String, date: Long, target: Int): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.d("No token found")
            return Completable.complete()
        }
        return loginApi.putDailyTarget("Bearer $token", id, NhsDailyTargetRequest(date, target))
            .doOnError(::handle403Error)
    }

    override fun deleteDailyTarget(id: String): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.d("No token found")
            return Completable.complete()
        }
        return loginApi.deleteDailyTarget("Bearer $token", id)
            .doOnError(::handle403Error)
    }

    override fun postActivityLevel(activityLevel: ActivityLevelEnum): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken ?: run {
            Timber.d("No token found")
            return Completable.complete()
        }
        return loginApi.postLatestActivityLevel(token = "Bearer $token", activityLevel = LatestActivityLevelRequest(
            level = activityLevel.value
        )).doOnError(::handle403Error)
    }

    override fun postMotivations(motivations: List<Goal>): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken ?: run {
            Timber.d("No token found")
            return Completable.complete()
        }
        return loginApi.postLatestMotivations(token = "Bearer $token", motivations = LatestMotivationRequest(
            goals = motivations.map { appGoal -> appGoal.toNhsGoalRequest() }
        )).doOnError(::handle403Error)
    }

    override fun logout(): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.w("Cannot log out on the backend. No token found")
            return Completable.complete()
        }
        return loginApi.logout("Bearer $token")
            .doOnError(::handle403Error)
    }

    override fun disconnect(): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken
        if (token.isNullOrBlank()) {
            Timber.w("Cannot disconnect on the backend. No token found")
            return Completable.complete()
        }
        return loginApi.disconnect("Bearer $token")
            .doOnError(::handle403Error)
    }

    private fun handle403Error(error: Throwable) {
        if (error is HttpException && error.code() == 403) {
            Timber.d("Error 403: Forbidden. Logging out user")
            removeNhsUserDataUseCase()
            throw error
        }
    }

}