package com.flipsidegroup.active10.presentation.todaywalk.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.todaywalk.view.TodayWalkView



interface TodayWalkPresenter : LifecycleAwarePresenter<TodayWalkView> {

    fun persistDeviceLocation()

    fun getWalkingMessages(totalBriskMin: Int)

    fun getRewardBadges()

    fun checkPaceCheckerIntro()

    fun checkMentalHealthBanner()

    fun checkCampaignBanner()

    fun shouldDisplayCampaignBanner(bannerId: String): Boolean

    fun markCampaignBannerAsSeen(bannerId: String)

    fun checkMyWalksNewFeaturesBanner()

    fun checkForIntroducingCoachApp(briskMinutes: Int)
}