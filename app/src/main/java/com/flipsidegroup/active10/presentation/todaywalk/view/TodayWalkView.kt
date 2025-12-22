package com.flipsidegroup.active10.presentation.todaywalk.view

import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView


interface TodayWalkView : BaseView {
    fun onMessagesReceived(
        message: String
    )

    fun onRewardsReceived(data: List<RewardBadge>)

    fun showPaceCheckerIntro(screenContent: ScreenContent)

    fun showMyWalksNewFeaturesBanner(screenContent: ScreenContent)

    fun showMentalHealthBanner(screenContent: ScreenContent?)

    fun showCampaignBanner(screenContent: ScreenContent?)

    fun introduceCoachApp()

}
