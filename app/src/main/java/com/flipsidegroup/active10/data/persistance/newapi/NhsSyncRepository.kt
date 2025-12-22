package com.flipsidegroup.active10.data.persistance.newapi

import com.flipsidegroup.active10.data.ActivityLevelEnum
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.ClassicUser
import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.data.models.api.NhsActivity
import com.flipsidegroup.active10.data.models.api.NhsActivityRequest
import com.flipsidegroup.active10.data.models.api.NhsActivityResponse
import com.flipsidegroup.active10.data.models.api.NhsDailyTargetResponse
import com.flipsidegroup.active10.data.models.api.NhsReward
import com.flipsidegroup.active10.data.models.api.NhsUserDetails
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.models.dataholders.TargetHolder
import com.flipsidegroup.active10.data.models.response.LatestActivityLevelResponse
import com.flipsidegroup.active10.data.models.response.LatestMotivationResponse
import com.flipsidegroup.active10.data.models.response.NhsUserDetailsResponse
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.EarnBadgeHelper
import com.flipsidegroup.active10.utils.retryWithDelay
import com.flipsidegroup.active10.utils.utcNanoSwiftToUtcMillisAndroid
import com.flipsidegroup.active10.utils.utcTimeMillisAndroidToTimeNanoSwift
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

class NhsSyncRepository @Inject constructor(
    private val settingsUtils: SettingsUtils,
    private val loginRepository: LoginRepository,
    private val localRepository: LocalRepository,
    private val preferenceRepository: PreferenceRepository,
    private val walkingPlanRepository: WalkingPlanRepository,
) {

    private val syncActivitySubject = PublishSubject.create<Long>()

    init {
        syncActivitySubject.debounce(3, java.util.concurrent.TimeUnit.SECONDS)
            .flatMap { syncLastActivitiesAndRewardsToNhs().toSingleDefault(DateTime.now().millis).toObservable() }
            .onErrorReturn { DateTime.now().millis }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun syncAllWithProgress(userId: String): Observable<Double> {
        val lastSyncTime = settingsUtils.getSettingsHolder().nhsLastSyncTime
        val lastSyncUserId = settingsUtils.getSettingsHolder().nhsLastSyncUserId

        return loginRepository.getUserDetails()
            .flatMapObservable { userDetails ->
                if (userDetails.isEmpty) return@flatMapObservable Observable.just(1.0)
                if (lastSyncTime == null) {
                    syncFirstTime(userDetails.get())
                } else if (lastSyncUserId == userId) {
                    syncFrom(lastSyncTime, userDetails.get())
                } else {
                    Timber.d("User changed. Deleting all local activities, targets and walking plan")
                    deleteLocalData()
                    syncFirstTime(userDetails.get())
                }
            }
    }

    fun trySyncLastActivitiesAndRewards() {
        Timber.d("Trying to sync last activities and rewards to NHS")
        syncActivitySubject.onNext(DateTime.now().millis)
    }

    private fun syncLastActivitiesAndRewardsToNhs(): Completable {
        Timber.d("Syncing last activities and rewards to NHS")
        val lastSyncTime = settingsUtils.getSettingsHolder().nhsLastSyncTime
            ?: return Completable.complete()
        if (lastSyncTime >= yesterday()) return Completable.complete()

        return loginRepository.getUserDetails()
            .flatMapCompletable { details ->
                if (details.isEmpty) return@flatMapCompletable Completable.complete()
                syncActivitiesToBackendAfterTimestamp(lastSyncTime, details.get().toEntity()).ignoreElements()
            }
    }

    private fun syncFirstTime(userDetails: NhsUserDetailsResponse): Observable<Double> {
        Timber.d("Syncing for the first time")

        val activitiesObservable = syncActivitiesFirstTime(userDetails.toEntity())
            .map { it.times(0.3) } // 30% of progress for syncing activities
        val targetsObservable = getTargetsFromToAndSync(0, DateTime.now(DateTimeZone.UTC).millis)
            .map { it.times(0.3) } // 30% of progress for syncing targets
        val walingPlanObservable =
            walkingPlanRepository.fetchUserWalkingPlanAndSyncWithLocalDatabase()
                .toSingleDefault(0.2) // 20% of progress for syncing walking plan
                .toObservable()
        val activityLevelObservable = getActivityLevelAndSync(userDetails.latestActivityLevel)
            .toSingleDefault(0.1) // 10% of progress for syncing activity level
            .toObservable()
        val motivationsObservable = getMotivationsAndSync(userDetails.latestMotivation)
            .toSingleDefault(0.1) // 10% of progress for syncing motivations
            .toObservable()

        return Observable.mergeArray(
            activitiesObservable,
            targetsObservable,
            walingPlanObservable,
            activityLevelObservable,
            motivationsObservable
        )
            .scan(0.0) { acc, value -> acc + value }
            .subscribeOn(Schedulers.io())
    }

    private fun syncActivitiesFirstTime(userDetails: NhsUserDetails): Observable<Double> {
        Timber.d("Syncing activities for the first time")
        val listOfYearsSince2019 = (2019..DateTime.now().year).map { getActivitiesForYear(it) }
        return activitiesObservableFromSingles(listOfYearsSince2019, userDetails)
    }

    private fun syncFrom(lastSyncTime: Long, userDetails: NhsUserDetailsResponse): Observable<Double> {
        val lastSyncDateTime = DateTime(lastSyncTime, DateTimeZone.UTC)
        Timber.d("Syncing from $lastSyncDateTime")

        val activitiesObservable = syncActivitiesFrom(lastSyncTime, userDetails.toEntity())
            .map { it.times(0.4) } // 40% of progress for syncing activities

        val targetsObservable = getTargetsFromToAndSync(lastSyncTime, DateTime.now(DateTimeZone.UTC).millis)
            .map { it.times(0.4) } // 40% of progress for syncing targets

        val walingPlanObservable =
            walkingPlanRepository.fetchUserWalkingPlanAndSyncWithLocalDatabase()
                .toSingleDefault(0.2) // 20% of progress for syncing walking plan
                .toObservable()

        val activityLevelObservable = getActivityLevelAndSync(userDetails.latestActivityLevel)
            .toSingleDefault(0.1) // 10% of progress for syncing activity level
            .toObservable()

        val motivationsObservable = getMotivationsAndSync(userDetails.latestMotivation)
            .toSingleDefault(0.1) // 10% of progress for syncing motivations
            .toObservable()

        return Observable.mergeArray(
            activitiesObservable,
            targetsObservable,
            walingPlanObservable,
            activityLevelObservable,
            motivationsObservable
        )
            .scan(0.0) { acc, value -> acc + value }
            .subscribeOn(Schedulers.io())
    }

    private fun syncActivitiesFrom(
        lastSyncTime: Long,
        userDetails: NhsUserDetails
    ): Observable<Double> {
        val lastSyncDateTime = DateTime(lastSyncTime, DateTimeZone.UTC)
        Timber.d("Syncing activities from $lastSyncDateTime")
        val yesterday = DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay().minusDays(1)
        if (lastSyncDateTime.isAfter(yesterday)) {
            Timber.d("No need to sync. Last sync time is after yesterday")
            return Observable.just(1.0)
        }

        val activitiesSingles = if (lastSyncDateTime.year == yesterday.year) {
            listOf(getActivitiesFromTo(lastSyncDateTime.millis / 1000, yesterday.millis / 1000))
        } else {
            (lastSyncDateTime.year + 1..yesterday.year).toList().map {
                getActivitiesForYear(it, lastSyncDateTime)
            }
        }

        return activitiesObservableFromSingles(activitiesSingles, userDetails)
    }

    private fun activitiesObservableFromSingles(
        activitiesSingles: List<Single<List<NhsActivityResponse>>>,
        userDetails: NhsUserDetails
    ): Observable<Double> {
        return Observable.fromIterable(activitiesSingles)
            .flatMap { it.toObservable() }
            // Collect all years as list of lists of activities
            .scan(emptyList<List<NhsActivityResponse>>()) { acc, value -> acc.plus(element = value) }
            // If all activities are downloaded, save activities and rewards to local database
            .flatMap { processedYears ->
                // 0.5 because we need to save it to local database and synchronize back to the backend
                val progressForFetchingOneEndpoint = 0.5 / activitiesSingles.size.toDouble()
                if (processedYears.size == activitiesSingles.size) {
                    val flattenActivities = processedYears.flatten()
                    Timber.d("All activities downloaded (${flattenActivities.size}. Saving to local database")
                    deleteLocalActivitiesBetweenRemoteActivities(flattenActivities)
                    saveActivitiesAndRewardsToLocalDatabase(flattenActivities)
                    val lastRemoteActivityTimestamp =
                        flattenActivities.maxByOrNull { it.timestampInSeconds }?.timestampInSeconds?.times(
                            1000
                        ) ?: 0
                    updateLastMyWalksSavedTimestampIfNeeded(lastRemoteActivityTimestamp)
                    return@flatMap syncActivitiesToBackendAfterTimestamp(lastRemoteActivityTimestamp, userDetails)
                        .startWith(progressForFetchingOneEndpoint)
                } else {
                    return@flatMap Observable.just(progressForFetchingOneEndpoint)
                }
            }
            .subscribeOn(Schedulers.io())
    }

    private fun yesterday(): Long {
        return DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay().minusDays(1).millis
    }

    private fun syncActivitiesToBackendAfterTimestamp(
        timestampInMillis: Long?,
        userDetails: NhsUserDetails
    ): Observable<Double> {
        Timber.d("Syncing activities and rewards after timestamp: $timestampInMillis")
        val activities = if (timestampInMillis != null) {
            Timber.d("Fetching activities after timestamp: $timestampInMillis")
            localRepository.getActivitiesOnDays(timestampInMillis, DateTime.now(DateTimeZone.UTC).millis)
        } else {
            Timber.d("Fetching all activities")
            localRepository.getAllActivities()
        }

        val earnedBadges = settingsUtils.getSettingsHolder().earnedBadges ?: emptyList()

        return activities
            .flatMapObservable { localActivitiesToSync ->
                Timber.d("Local activities to sync size: ${localActivitiesToSync.size}")
                val lastSyncDate: Long = max(
                    timestampInMillis ?: 0,
                    localActivitiesToSync.maxOfOrNull { it.timestamp ?: 0 } ?: 0
                )
                val sendActivitiesToBackendRequests = localActivitiesToSync
                    .filterNot { it.timestamp == null }
                    .groupBy { DateTime(it.timestamp, DateTimeZone.UTC).withTimeAtStartOfDay().withDayOfMonth(1) }
                    .map { activitiesDividedByMonth ->
                        Timber.d("Sending activities divided by month: ${activitiesDividedByMonth.key}")
                        loginRepository.postActivitiesBulk(
                            activitiesDividedByMonth.key.millis / 1000,
                            activitiesDividedByMonth.value.map { activity ->
                                NhsActivityRequest(
                                    userPostcode = userDetails.postcode ?: "",
                                    userAgeRange = userDetails.ageRange ?: "",
                                    timestampInSeconds = DateTime(activity.timestamp, DateTimeZone.UTC).withTimeAtStartOfDay().millis / 1000,
                                    activity = NhsActivity(
                                        steps = activity.totalSteps ?: 0,
                                        briskMinutes = activity.totalBriskMin ?: 0,
                                        walkingMinutes = activity.totalWalkMin ?: 0
                                    ),
                                    rewards = earnedBadges
                                        .filter {
                                            DateTime(it.timestamp).withTimeAtStartOfDay() == DateTime(
                                                activity.timestamp!!
                                            ).withTimeAtStartOfDay()
                                        }
                                        .map { NhsReward(it.timestamp.utcTimeMillisAndroidToTimeNanoSwift(), it.slug) }
                                )
                            }
                        ).subscribeOn(Schedulers.io())
                    }
                Observable.fromIterable(sendActivitiesToBackendRequests)
                    // We use 0.5 because Sending to backend is only a part of syncing activities. Second half is for GET calls and syncing from the backend
                    .flatMap { it.toSingleDefault(0.5).toObservable() }
                    .map { it / localActivitiesToSync.size.toDouble() } // So maximum progress is 0.5 here (50% of the activity synchronization)
                    .startWith(if (localActivitiesToSync.isEmpty()) 0.5 else 0.0)
                    .doOnComplete {
                        Timber.d("All activities sent to backend")
                        updateLastSyncTime(userDetails.id!!, lastSyncDate)
                    }
            }
    }

    private fun getActivitiesForYear(
        year: Int,
        lastSyncTime: DateTime? = null
    ): Single<List<NhsActivityResponse>> {
        val minimumStart = (lastSyncTime
            ?: DateTime(
                2019,
                6,
                1,
                0,
                0,
                DateTimeZone.UTC
            )).millis / 1000
        val startOfTheYear = DateTime(year, 1, 1, 0, 0, DateTimeZone.UTC).millis / 1000
        val endOfTheYear = DateTime(year, 12, 31, 0, 0, DateTimeZone.UTC).millis / 1000
        val yesterday =
            DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay().millis / 1000
        return getActivitiesFromTo(max(minimumStart, startOfTheYear), min(yesterday, endOfTheYear))
    }

    private fun getActivitiesFromTo(start: Long, end: Long): Single<List<NhsActivityResponse>> {
        Timber.d("Fetching activities from $start to $end")
        return loginRepository.getActivitiesFromTo(start, end)
            .subscribeOn(Schedulers.io())
            .retryWithDelay()
    }

    private fun getTargetsFromToAndSync(startTimestampInMillis: Long, endTimestampInMillis: Long): Observable<Double> {
        if (startTimestampInMillis >= endTimestampInMillis) return Observable.just(1.0)
        Timber.d("Fetching targets from $startTimestampInMillis to $endTimestampInMillis")
        return loginRepository.getDailyTargetFromTo(startTimestampInMillis / 1000, endTimestampInMillis / 1000)
            .subscribeOn(Schedulers.io())
            .retryWithDelay()
            .flatMapObservable { targets ->
                val localTargetsToSync =
                    mergeRemoteTargetsToLocalTargetsAndReturnsTargetsToSyncWithBackend(targets)
                        .filter { it.target != null }
                if (localTargetsToSync.isEmpty()) {
                    Timber.d("No targets to sync")
                    return@flatMapObservable Observable.just(1.0)
                }
                val totalCallsSize = localTargetsToSync.size
                Observable.fromIterable(localTargetsToSync)
                    .flatMap { target ->
                        postDailyTarget(
                            target.timestamp,
                            target.target!!
                        ).andThen(Observable.just(1))
                    }
                    .map { 1 / totalCallsSize.toDouble() }
            }
    }

    fun postDailyTarget(timestampInMillis: Long, target: Int): Completable {
        Timber.d("Posting daily target for date $timestampInMillis")
        return loginRepository.postDailyTarget(timestampInMillis / 1000, target)
            .onErrorResumeNext { error ->
                if (error is HttpException && error.code() == 400) {
                    // 400 means target already exists and we need to update it
                    Timber.d(error, "No daily target found for the given date")
                    loginRepository.getDailyTargetForDate(timestampInMillis / 1000)
                        .flatMapCompletable { remoteTargets ->
                            Timber.d("Found ${remoteTargets.size} remote targets for timestamp: $timestampInMillis")
                            if (remoteTargets.isNotEmpty() && remoteTargets[0].dailyTarget != target) {
                                Timber.d("Updating daily target for timestamp $timestampInMillis, with id = ${remoteTargets[0].id}")
                                loginRepository.updateDailyTarget(
                                    remoteTargets[0].id,
                                    timestampInMillis / 1000,
                                    target
                                )
                            } else {
                                Completable.complete()
                            }
                        }
                } else {
                    throw error
                }
            }
            .subscribeOn(Schedulers.io())
            .retryWithDelay()
    }

    private fun getActivityLevelAndSync(latestActivityLvl: LatestActivityLevelResponse?): Completable {
        if (latestActivityLvl == null) {
            val classicUserActivityLevel = settingsUtils.getSettingsHolder().classicUser?.activityLevel
            if (classicUserActivityLevel.isNullOrBlank()) {
                Timber.d("No classic user activity level to sync")
                return Completable.complete()
            }
            val stdActivityLevel = ActivityLevelEnum.fromValue(classicUserActivityLevel) ?: run {
                Timber.e("Invalid activity level: $classicUserActivityLevel")
                return Completable.complete()
            }
            return loginRepository.postActivityLevel(stdActivityLevel)
                .subscribeOn(Schedulers.io())
        } else {
            settingsUtils.updateSettings(
                SettingsDataHolder(
                    classicUser = settingsUtils.getSettingsHolder().classicUser?.apply {
                        activityLevel = latestActivityLvl.level
                    } ?: ClassicUser(activityLevel = latestActivityLvl.level),
                )
            )
            return Completable.complete()
        }
    }

    private fun getMotivationsAndSync(nhsUserGoals: LatestMotivationResponse?): Completable {
        if (nhsUserGoals == null) {
            val classicUserGoals = settingsUtils.getSettingsHolder().goalsList ?: run {
                Timber.d("No classic user motivations to sync")
                return Completable.complete()
            }
            return loginRepository.postMotivations(classicUserGoals)
                .subscribeOn(Schedulers.io())
        } else {
            return localRepository.getGoalsContent()
                .subscribeOn(Schedulers.io())
                .doOnSuccess { cmsGoals ->
                    settingsUtils.updateSettings(SettingsDataHolder(goalsList = nhsUserGoals.toAppGoals(cmsGoals)))
                }
                .ignoreElement()
        }
    }

    private fun deleteLocalData() {
        Timber.d("Deleting all local data")
        deleteAllLocalActivities()
        deleteAllRewards()
        deleteAllTargets()
        walkingPlanRepository.deleteWalkingPlanEntity()
        clearLastSyncTime()
    }

    private fun deleteAllLocalActivities() {
        Timber.d("Syncing. Deleting all local activities")
        localRepository.deleteAllActivities()
    }

    private fun deleteAllRewards() {
        Timber.d("Syncing. Deleting all local rewards")
        settingsUtils.updateSettings(
            settingsUtils.getSettingsHolder().copy(earnedBadges = arrayListOf())
        )
    }

    private fun deleteAllTargets() {
        Timber.d("Syncing. Deleting all local targets")
        settingsUtils.updateSettings(
            settingsUtils.getSettingsHolder().copy(targetList = arrayListOf())
        )
    }

    private fun deleteLocalActivitiesBetweenRemoteActivities(activities: List<NhsActivityResponse>) {
        val firstActivity = activities.minByOrNull { it.timestampInSeconds } ?: return
        val lastActivity = activities.maxByOrNull { it.timestampInSeconds } ?: return
        Timber.d("Syncing. Deleting local activities between remote activities from ${firstActivity.timestampInSeconds} to ${lastActivity.timestampInSeconds}")
        localRepository.deleteActivitiesBetween(
            DateHelper.getStartTimestampOfDay(firstActivity.timestampInSeconds * 1000),
            DateHelper.getEndTimestampOfDay(lastActivity.timestampInSeconds * 1000),
        )
    }

    private fun saveActivitiesAndRewardsToLocalDatabase(activities: List<NhsActivityResponse>) {
        Timber.d("Syncing. Saving activities and rewards to local database. Activities size: ${activities.size}")
        activities.forEach { activityResponse ->
            persistActivityFromActivityResponse(activityResponse)
            persistRewardsFromActivityResponse(activityResponse)
        }
    }

    /**
     * @return list of local targets from the last remote target date
     */
    private fun mergeRemoteTargetsToLocalTargetsAndReturnsTargetsToSyncWithBackend(targetResponses: List<NhsDailyTargetResponse>): List<TargetHolder> {
        Timber.d("Merging remote targets with local targets")
        val localTargets = settingsUtils.getSettingsHolder().targetList ?: ArrayList()
        val lastRemoteTarget = targetResponses.maxByOrNull { it.timestampInSeconds }
        // If no remote targets, return all local targets
            ?: return localTargets.toList()

        // Remove local targets that are older than the last remote target
        localTargets.removeIf { it.timestamp < lastRemoteTarget.timestampInSeconds * 1000 }
        // Copy local targets to return
        val localTargetAfterLastRemoteDate = ArrayList(localTargets)
        // Add remote targets to local targets
        targetResponses.forEach { targetResponse ->
            localTargets.add(
                TargetHolder(targetResponse.dailyTarget, targetResponse.timestampInSeconds * 1000)
            )
        }
        settingsUtils.updateSettings(
            settingsUtils.getSettingsHolder().copy(targetList = localTargets)
        )
        return localTargetAfterLastRemoteDate
    }

    private fun persistRewardsFromActivityResponse(activityResponse: NhsActivityResponse) {
        activityResponse.rewards.forEach { reward ->
            val badge = RewardBadgeEnum.entries.find { it.slug == reward.slug }
            if (getEarnedRewardBadge(reward.slug) == null && badge != null) {
                Timber.d("Saving earned badge: ${badge.name} from activity response")
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = badge,
                    timestamp = reward.earned.utcNanoSwiftToUtcMillisAndroid(),
                    preferenceRepository = preferenceRepository
                )
            }
        }
    }

    private fun persistActivityFromActivityResponse(activityResponse: NhsActivityResponse) {
        val stepOverview = StepOverview(
            totalSteps = activityResponse.steps,
            totalWalkMin = activityResponse.walkingMinutes,
            totalBriskMin = activityResponse.briskMinutes,
            timestamp = DateHelper.getStartTimestampOfDay(activityResponse.timestampInSeconds * 1000)
        )
        localRepository.persistActivity(stepOverview)
    }

    private fun getEarnedRewardBadge(slug: String) =
        settingsUtils.getSettingsHolder().earnedBadges?.find { it.slug == slug }

    private fun updateLastSyncTime(userId: String, lastSyncDate: Long) {
        Timber.d("Updating last sync time for user $userId. Last sync time: $lastSyncDate")
        val settingsHolder = settingsUtils.getSettingsHolder()
        settingsHolder.nhsLastSyncTime = lastSyncDate
        settingsHolder.nhsLastSyncUserId = userId
        settingsUtils.updateSettings(settingsHolder)
    }

    private fun clearLastSyncTime() {
        Timber.d("Clearing last sync time. NhsLastSyncTime, NhsLastSyncUserId and lastMyWalksSavedTimestamp will be set to null")
        val settingsHolder = settingsUtils.getSettingsHolder()
        settingsUtils.saveSettingsHolder(settingsHolder.copy(nhsLastSyncTime = null, nhsLastSyncUserId = null, lastMyWalksSavedTimestamp = null))
    }

    private fun updateLastMyWalksSavedTimestampIfNeeded(lastRemoteStepsTimestampInMillis: Long?) {
        val lastMyWalksSavedTimestamp = settingsUtils.getSettingsHolder().lastMyWalksSavedTimestamp
        if (lastMyWalksSavedTimestamp == null || lastMyWalksSavedTimestamp < (lastRemoteStepsTimestampInMillis ?: 0)) {
            settingsUtils.updateSettings(
                settingsUtils.getSettingsHolder().copy(lastMyWalksSavedTimestamp = lastRemoteStepsTimestampInMillis)
            )
        }
    }
}
