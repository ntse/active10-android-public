package com.flipsidegroup.active10.data.persistance

import android.os.Handler
import com.flipsidegroup.active10.data.AboutCommunity
import com.flipsidegroup.active10.data.FaqItem
import com.flipsidegroup.active10.data.GlobalRules
import com.flipsidegroup.active10.data.HowItWorks
import com.flipsidegroup.active10.data.LegalRules
import com.flipsidegroup.active10.data.Notifications
import com.flipsidegroup.active10.data.Onboarding
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.data.Tip
import com.flipsidegroup.active10.data.WalkingMessageResponse
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.data.models.api.DiscoverCategory
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.CurrentWalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.PauseResume
import com.flipsidegroup.active10.data.models.dataholders.WalkingPlanEntity
import com.flipsidegroup.active10.utils.DateHelper
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.Sort
import timber.log.Timber
import java.util.concurrent.Executors

class AppDatabase(private val config: RealmConfiguration) {

    private var entries: RealmResults<StepOverview>? = null
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler()

    fun persistActivity(stepOverview: StepOverview) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                val value: StepOverview? = if (stepOverview.timestamp == null) null
                else {
                    realm.where(StepOverview::class.java)
                        .between(
                            StepOverview::timestamp.name,
                            DateHelper.getStartTimestampOfDay(stepOverview.timestamp!!),
                            DateHelper.getEndTimestampOfDay(stepOverview.timestamp!!)
                        ).findFirst()
                }
                if (value == null) {
                    realm.executeTransaction { realm.copyToRealmOrUpdate(stepOverview) }
                }
            }
        }
    }

    fun deleteWalkingPlanEntity() {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction {
                    realm.where(WalkingPlanEntity::class.java).findAll().deleteAllFromRealm()
                }
            }
        }
    }

    fun getAllPauseResume(
        listener: OnDataLoadedListener<List<PauseResume>>
    ) {
        getAll(PauseResume::class.java, listener)
    }

    fun getActivitiesOnDays(
        startDay: Long,
        endDay: Long,
        listener: OnDataLoadedListener<List<StepOverview>>
    ) {
        getAll(StepOverview::class.java, listener) {
            between(StepOverview::timestamp.name, startDay, endDay)
        }
    }

    fun getAllActivities(
        listener: OnDataLoadedListener<List<StepOverview>>
    ) {
        getAll(StepOverview::class.java, listener) {
            sort(StepOverview::timestamp.name, Sort.ASCENDING)
        }
    }

    fun deleteActivitiesBetween(startDay: Long, endDay: Long) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction {
                    realm.where(StepOverview::class.java)
                        .between(StepOverview::timestamp.name, startDay, endDay)
                        .findAll()
                        .deleteAllFromRealm()
                }
            }
        }
    }

    fun deleteAllActivities() {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction {
                    realm.where(StepOverview::class.java).findAll().deleteAllFromRealm()
                }
            }
        }
    }

    fun getSortedActivitiesOnDays(
        startDay: Long,
        endDay: Long,
        listener: OnDataLoadedListener<List<StepOverview>>
    ) {
        getAll(StepOverview::class.java, listener) {
            between(StepOverview::timestamp.name, startDay, endDay)
                .sort(StepOverview::timestamp.name, Sort.ASCENDING)
        }
    }

    fun getActivitiesDaysNumber(
        startDay: Long,
        endDay: Long
    ): Int {
        return Realm.getInstance(config).let { realm ->
            val list =
                realm.where(StepOverview::class.java)
                    .between(StepOverview::timestamp.name, startDay, endDay)
                    .findAll() ?: return 0

            return@let list.size
        }
    }

    fun hasActivityOnDay(
        day: Long
    ): Boolean {
        return Realm.getInstance(config).let { realm ->
            val data = realm.where(StepOverview::class.java)
                .between(
                    StepOverview::timestamp.name,
                    DateHelper.getStartTimestampOfDay(day),
                    DateHelper.getEndTimestampOfDay(day)
                )
                .findFirst()
            return@let data != null
        }
    }

    fun persistGoalsContent(goalsList: List<Goal>) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(goalsList) }
            }
        }
    }

    fun getGoalsContent(listener: OnDataLoadedListener<List<Goal>>) {
        getAll(Goal::class.java, listener)
    }

    fun getGoalsContent(): Single<List<Goal>> {
        return getAllSingle(Goal::class.java)
    }

    fun persistFaqContent(list: List<FaqItem>) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(list) }
            }
        }
    }

    fun getFaqContent(onDataLoaded: OnDataLoadedListener<List<FaqItem>>) {
        getAll(FaqItem::class.java, onDataLoaded)
    }

    fun persistHowItWorksContent(list: List<HowItWorks>) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(list) }
            }
        }
    }

    fun getHowItWorksContent(onDataLoaded: OnDataLoadedListener<List<HowItWorks>>) {
        getAll(HowItWorks::class.java, onDataLoaded)
    }

    fun persistTipContent(list: List<Tip>) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(list) }
            }
        }
    }

    fun getTipsContent(onDataLoaded: OnDataLoadedListener<List<Tip>>) {
        getAll(Tip::class.java, onDataLoaded)
    }

    fun persistWalkingMessages(walkingMessageResponse: WalkingMessageResponse) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(walkingMessageResponse) }
            }
        }
    }

    fun getWalkingMessages(onDataLoaded: OnDataLoadedListener<WalkingMessageResponse?>) {
        getFirst(WalkingMessageResponse::class.java, onDataLoaded)
    }

    fun getFirstActivity(): StepOverview? {
        return Realm.getInstance(config).let { realm ->
            val first =
                realm.where(StepOverview::class.java)
                    .sort(StepOverview::timestamp.name, Sort.ASCENDING)
                    .findFirst() ?: return null

            return@let realm.copyFromRealm(first)
        }
    }

    fun getFirstActivityStartFrom(start: Long): StepOverview? {
        return Realm.getInstance(config).let { realm ->
            val first =
                realm.where(StepOverview::class.java)
                    .greaterThanOrEqualTo(StepOverview::timestamp.name, start)
                    .sort(StepOverview::timestamp.name, Sort.ASCENDING)
                    .findFirst() ?: return null

            return@let realm.copyFromRealm(first)
        }
    }

    fun registerDataListener(listener: OnDataChange) {
        Timber.d("Registering listener...")
        entries = Realm.getInstance(config).where(StepOverview::class.java).findAll()
        entries?.addChangeListener { _ ->
            handler.post {
                Timber.d("DATA CHANGED")
                listener.onDataChange()
            }
        }
    }

    fun persistAboutCommunity(aboutCommunity: AboutCommunity) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(aboutCommunity) }
            }
        }
    }

    fun getAboutCommunity(listener: OnDataLoadedListener<AboutCommunity?>) {
        getFirst(AboutCommunity::class.java, listener)
    }

    fun unregisterDataListener() {
        Timber.d("Unregistering listener...")
        entries?.removeAllChangeListeners()
    }


    fun persistOnboarding(onboarding: Onboarding) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(onboarding) }
            }
        }
    }

    fun getOnboarding(listener: OnDataLoadedListener<Onboarding?>) {
        getFirst(Onboarding::class.java, listener)
    }

    fun persistNotifications(notifications: Notifications) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(notifications) }
            }
        }
    }

    fun getNotifications(listener: OnDataLoadedListener<Notifications?>) {
        getFirst(Notifications::class.java, listener)
    }

    fun persistGlobalRules(globalRules: GlobalRules) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(globalRules) }
            }
        }
    }

    fun getGlobalRules(listener: OnDataLoadedListener<GlobalRules?>) {
        getFirst(GlobalRules::class.java, listener)
    }

    fun getLegalRules(listener: OnDataLoadedListener<List<LegalRules>>) {
        getAll(LegalRules::class.java, listener)
    }

    fun persistRewardBadges(rewardBadge: List<RewardBadge>) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(rewardBadge) }
            }
        }
    }

    fun persistLegalRules(legalRules: List<LegalRules>) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction {
                    realm.delete(LegalRules::class.java)
                    realm.copyToRealmOrUpdate(legalRules)
                }
            }
        }
    }

    fun removeRewardBadges() {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.beginTransaction()
                realm.where(RewardBadge::class.java).findAll().deleteAllFromRealm()
                realm.commitTransaction()
            }
        }
    }

    fun getRewardBadges(onDataLoaded: OnDataLoadedListener<List<RewardBadge>>) {
        val validSlugs = RewardBadgeEnum.entries.map { it.slug }.toTypedArray()

        getAll(RewardBadge::class.java, onDataLoaded) {
            `in`("slug", validSlugs)
                .sort("position")
        }
    }

    fun persistScreenContents(screenContents: List<ScreenContent>) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(screenContents) }
            }
        }
    }

    fun getScreenContents(listener: OnDataLoadedListener<List<ScreenContent>>) {
        getAll(ScreenContent::class.java, listener)
    }


    fun persistWalkingPlans(walkingPlans: List<WalkingPlan>) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(walkingPlans) }
            }
        }
    }

    fun getWalkingPlans(listener: OnDataLoadedListener<List<WalkingPlan>>) {
        getAll(WalkingPlan::class.java, listener)
    }

    fun subscribeUserWalkingPlans(): Flowable<List<WalkingPlanEntity>> {
        return Realm.getInstance(config).let { realm ->
            realm.where(WalkingPlanEntity::class.java)
            .findAllAsync()
            .asFlowable()
            .filter { it.isLoaded }
            .map { realmResults ->
                realm.copyFromRealm(realmResults)
            }
            .subscribeOn(Schedulers.io())
        }
    }

    fun getWalkingPlanEntity(listener: OnDataLoadedListener<List<WalkingPlanEntity>>) {
        getAll(WalkingPlanEntity::class.java, listener)
    }

    fun getUserWalkingPlans(listener: OnDataLoadedListener<List<CurrentWalkingPlan>>) {
        getAll(CurrentWalkingPlan::class.java, listener)
    }

    fun persistDiscoveryArticles(discoveryArticles: List<InfoPage>) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(discoveryArticles) }
            }
        }
    }

    fun getDiscoveryArticles(listener: OnDataLoadedListener<List<InfoPage>>) {
        getAll(InfoPage::class.java, listener)
    }

    fun persistDiscoveryCategories(discoveryCategories: List<DiscoverCategory>) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(discoveryCategories) }
            }
        }
    }

    fun getDiscoveryCategories(listener: OnDataLoadedListener<List<DiscoverCategory>>) {
        getAll(DiscoverCategory::class.java, listener)
    }

    private fun <T : RealmObject> getAll(
        clazz: Class<T>,
        listener: OnDataLoadedListener<List<T>>,
        queryManipulation: (RealmQuery<T>.() -> RealmQuery<T>)? = null
    ) {
        executor.execute {
            val data = Realm.getInstance(config).let { realm ->
                realm.copyFromRealm(
                    realm.where(clazz)
                        .let { query -> queryManipulation?.let { it(query) } ?: query }
                        .findAll()
                )
            }

            handler.post {
                listener.onDataLoaded(data)
            }
        }
    }

    private fun <T : RealmObject> getAllSingle(
        clazz: Class<T>,
        queryManipulation: (RealmQuery<T>.() -> RealmQuery<T>)? = null
    ): Single<List<T>> {
        return Single.create { emitter ->
            executor.execute {
                try {
                    val data = Realm.getInstance(config).let { realm ->
                        realm.copyFromRealm(
                            realm.where(clazz)
                                .let { query -> queryManipulation?.let { it(query) } ?: query }
                                .findAll()
                        )
                    }

                    handler.post {
                        emitter.onSuccess(data)
                    }
                } catch (e: Exception) {
                    handler.post {
                        emitter.onError(e)
                    }
                }
            }
        }
    }

    private fun <T : RealmObject> getFirst(
        clazz: Class<T>,
        listener: OnDataLoadedListener<T?>,
        queryManipulation: (RealmQuery<T>.() -> RealmQuery<T>)? = null
    ) {
        executor.execute {
            val data = Realm.getInstance(config).let { realm ->
                realm.where(clazz)
                    .let { query -> queryManipulation?.let { it(query) } ?: query }
                    .findFirst()
                    ?.let { realm.copyFromRealm(it) }
            }

            handler.post {
                listener.onDataLoaded(data)
            }
        }
    }

    fun persistWalkingPlanEntity(userPlans: WalkingPlanEntity) {
        executor.execute {
            Realm.getInstance(config).let { realm ->
                realm.executeTransaction { realm.copyToRealmOrUpdate(userPlans) }
            }
        }
    }


    interface OnDataLoadedListener<Data> {
        fun onDataLoaded(data: Data)
    }

    interface OnDataChange {
        fun onDataChange()
    }
}