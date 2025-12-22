package com.flipsidegroup.active10.data.persistance.newapi

import com.flipside.briskcounter.data.BriskPauseResume
import com.flipsidegroup.active10.data.models.dataholders.PauseResume
import com.flipsidegroup.active10.data.models.dataholders.WalkingPlanEntity
import com.flipsidegroup.active10.data.network.LoginApi
import com.flipsidegroup.active10.data.persistance.AppRepository
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.usecases.RemoveNhsUserDataUseCase
import com.flipsidegroup.active10.utils.WalkingPlanState
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

class WalkingPlanRepository @Inject constructor(
    private val appRepository: AppRepository,
    private val localRepository: LocalRepository,
    private val settingsUtils: SettingsUtils,
    private val loginApi: LoginApi,
    private val removeNhsUserDataUseCase: RemoveNhsUserDataUseCase,
) {

    fun getAllWalkingPlans() = getWalkingPlans()

    /**
     * Note: There should be at most one CurrentWalkingPlan in the database, no more.
     */
    fun getUserWalkingPlans() = localRepository.getUserWalkingPlans()

    /**
     * Note: There should be at most one WalkingPlanEntity in the database, no more.
     */
    fun subscribeUserWalkingPlans() = localRepository.subscribeUserWalkingPlans()

    fun checkIfUserIsLoggedIn(): Single<Boolean> {
        val token = settingsUtils.getSettingsHolder().nhsToken
        return loginApi.getUserWalkingPlans("Bearer $token")
            .subscribeOn(Schedulers.io())
            .map { true }
            .onErrorReturn { error -> !(error is HttpException && error.code() == 403) }
    }

    fun fetchUserWalkingPlanAndSyncWithLocalDatabase(): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken
        return loginApi.getUserWalkingPlans("Bearer $token")
            .subscribeOn(Schedulers.io())
            .doOnError {
                handle403Error(it)
                Timber.d("Error fetching walking plan data: ${it.message}")
            }
            .doOnSuccess {
                Timber.d("Fetched walking plan data DTO")
                Timber.d(it.walkingPlanData.toString())
                localRepository.persistWalkingPlanEntity(it.walkingPlanData.toEntity())
                Timber.d("Fetched walking plan data Entity")
                Timber.d(it.walkingPlanData.toEntity().toString())
            }
            .flatMapCompletable {
                Completable.complete()
            }
            .onErrorComplete { true }
    }

    fun saveWalkingPlanEntity(entity: WalkingPlanEntity): Completable {
        localRepository.persistWalkingPlanEntity(entity)
        return saveWalkingPlanEntityToBackend(entity)
            .doOnError { handle403Error(it) }
    }

    fun saveToBackendAndDeleteFromDatabaseWalkingPlanEntity(): Completable {
        return localRepository.getWalkingPlanEntity()
            .flatMapCompletable { entityList ->
                if (entityList.isEmpty()) return@flatMapCompletable Completable.complete()
                val entity = entityList.first()
                entity.currentWalkingPlan?.apply {
                    if (state == WalkingPlanState.ACTIVE.name.lowercase()) {
                        state = WalkingPlanState.PAUSED.name.lowercase()
                        pauseResume.add(PauseResume(paused = LocalDateTime.now().toString()))
                    }
                }
                saveWalkingPlanEntityToBackend(entity)
            }
            .doOnComplete {
                localRepository.deleteWalkingPlanEntity()
            }
    }

    private fun saveWalkingPlanEntityToBackend(walkingPlan: WalkingPlanEntity): Completable {
        val token = settingsUtils.getSettingsHolder().nhsToken ?: return Completable.complete()
        return loginApi.getUserWalkingPlans("Bearer $token")
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                loginApi.deleteUserWalkingPlans("Bearer $token")
                    .andThen(
                        loginApi.postUserWalkingPlans(
                            token = "Bearer $token",
                            walkingPlan = walkingPlan.toDTO()
                        )
                    )
                    .subscribeOn(Schedulers.io())
            }
            .onErrorResumeNext {
                loginApi.postUserWalkingPlans(
                    token = "Bearer $token",
                    walkingPlan = walkingPlan.toDTO()
                )
                    .subscribeOn(Schedulers.io())
            }
            .doOnComplete {
                Timber.d("Saved user walking plan in backend")
            }
            .doOnError {
                Timber.d("Error saving user walking plan in backend: $it")
            }
    }

    private fun getWalkingPlans() = localRepository.getWalkingPlans()
        .flatMap { walkingPlans ->
            if (walkingPlans.isNotEmpty()) {
                Single.just(walkingPlans)
                    .subscribeOn(Schedulers.io())
            } else {
                appRepository.getWalkingPlans()
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess {
                        localRepository.persistWalkingPlans(it)
                    }
            }
        }

    fun deleteWalkingPlanEntity() = localRepository.deleteWalkingPlanEntity()

    fun deleteDaysFromCurrentWeek(): Completable {
        return Single.zip(
            localRepository.getWalkingPlanEntity(),
            localRepository.getWalkingPlans()
        ) { entity, cmsPlans -> Pair(entity, cmsPlans) }
            .subscribeOn(Schedulers.io())
            .doOnError {
                Timber.d("Error removing days from current week: $it")
            }
            .flatMapCompletable { pair ->
                val userPlan = pair.first.first()
                val currentPlan = userPlan.currentWalkingPlan!!
                val cmsPlan = pair.second.first { it.id == currentPlan.planId }

                currentPlan.deleteCurrentWeekDays(cmsPlan)
                saveWalkingPlanEntity(userPlan)
            }
    }

    fun resetCurrentPlan(): Completable {
        return localRepository.getWalkingPlanEntity()
            .subscribeOn(Schedulers.io())
            .doOnError {
                Timber.d("Error reset plan from current week: $it")
            }
            .flatMapCompletable { userPlans ->
                val userPlan = userPlans.first()
                val currentPlan = userPlan.currentWalkingPlan!!

                currentPlan.resetPlan()
                saveWalkingPlanEntity(userPlan)
            }
    }

    fun getAllPauseResume(): Single<List<BriskPauseResume>> {
        return localRepository.getPauseResume().flatMap { list ->
            val convertedPauseResume = list.map { it.toBriskPauseResume() }
            Single.just(convertedPauseResume)
                .subscribeOn(Schedulers.io())
        }
    }

    private fun handle403Error(error: Throwable) {
        if (error is HttpException && error.code() == 403) {
            Timber.d("Error 403: Forbidden. Logging out user")
            removeNhsUserDataUseCase()
            throw error
        }
    }

}
