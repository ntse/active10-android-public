package com.flipsidegroup.active10.data.persistance

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
import com.flipsidegroup.active10.data.*
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.models.api.DiscoverCategory
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.network.AppApi
import com.flipsidegroup.active10.data.network.AppV3Api
import com.flipsidegroup.active10.data.network.NewAppApi
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.newapi.PLATFORM_IOS
import com.flipsidegroup.active10.data.persistance.newapi.SLUG_WIDGET_IOS
import com.flipsidegroup.active10.utils.MyWalkRewardMessageHelper
import com.flipsidegroup.active10.utils.TodayWalkHeaderHelper
import io.reactivex.Single

class AppRepositoryImpl(
    private val appApi: AppApi,
    private val appV3Api: AppV3Api,
    private val newAppApi: NewAppApi,
    private val localRepository: LocalRepository,
    private val myWalkRewardMessageHelper: MyWalkRewardMessageHelper,
    private val todayWalkHeaderHelper: TodayWalkHeaderHelper,
) : AppRepository {

    companion object {
        private const val DISCOVER_TIP_ID_BUFFER = 1000
    }

    override fun getAboutCommunity(): Single<AboutCommunity> {
        return appApi.getAboutCommunity()
            .doOnSuccess { localRepository.persistAboutCommunity(it) }
    }

    override fun getTips(): Single<List<Tip>> {
        return appApi.getTips()
            .doOnSuccess { localRepository.persistTipsContent(it) }
    }

    override fun getFaqList(): Single<List<FaqItem>> {
        return appApi.getFaqList()
            .doOnSuccess { localRepository.persistFaqContent(it) }
    }

    override fun getHowItWorksList(): Single<List<HowItWorks>> {
        return appApi.getHowItWorksList()
            .doOnSuccess { localRepository.persistHowItWorksContent(it) }
    }

    override fun getWalkingMessages(): Single<WalkingMessageResponse> {
        return appApi.getWalkingMessages()
            .doOnSuccess {
                localRepository.persistWalkingMessages(it)
            }
    }

    override fun getGoalsList(): Single<List<Goal>> {
        return appApi.getGoalsList()
            .doOnSuccess { localRepository.persistGoalsContent(it) }
    }

    override fun getOnboarding(): Single<Onboarding> {
        return appApi.getOnboarding()
            .doOnSuccess { localRepository.persistOnboarding(it) }
    }


    override fun getNotifications(): Single<Notifications> {
        return appApi.getNotifications()
            .doOnSuccess { localRepository.persistNotifications(it) }
    }

    override fun getGlobalRules(): Single<GlobalRules> {
        return appApi.getGlobalRules()
            .doOnSuccess { localRepository.persistGlobalRules(it) }
    }

    override fun getRewardList(): Single<List<RewardBadge>> {
        return appV3Api.getRewardList()
            .doOnSuccess { localRepository.persistRewardBadges(it) }
    }

    override fun getLegalList(): Single<List<LegalRules>> {
        return appApi.getLegalList()
            .doOnSuccess { localRepository.persistLegalRules(it) }
    }

    override fun getScreenContents(): Single<List<ScreenContent>> {
        return newAppApi.getScreenContents()
            .doOnSuccess { localRepository.persistScreenContents(it) }
    }

    override fun getWalkingPlans(): Single<List<WalkingPlan>> {
        return newAppApi.getWalkingPlans()
            .doOnSuccess { localRepository.persistWalkingPlans(it) }
    }

    override fun getDiscoverArticles(): Single<List<InfoPage>> {
        return newAppApi.getDiscoverArticles()
            .map { it.filter { page -> page.slug != SLUG_WIDGET_IOS && page.platform != PLATFORM_IOS } }
            .doOnSuccess { localRepository.persistDiscoveryArticles(it) }
    }

    override fun getDiscoverCategories(): Single<List<DiscoverCategory>> {
        return newAppApi.getDiscoverCategory()
            .doOnSuccess { localRepository.persistDiscoveryCategories(it) }
    }
}