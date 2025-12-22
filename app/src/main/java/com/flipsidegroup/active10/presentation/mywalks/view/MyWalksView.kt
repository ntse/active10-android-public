package com.flipsidegroup.active10.presentation.mywalks.view

import com.flipsidegroup.active10.data.*
import com.flipsidegroup.active10.data.models.StepOverview
import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface MyWalksView : BaseView {

    fun setUpPeriods(intervals: ArrayList<IntervalWalk>)

    fun setUpRewards(
        walk: Int,
        briskWalk: Int,
        rewards: List<Reward>,
        daysTargetHit: Int,
        active10: Int
    )

    fun setUpTodayRewards(rewards: List<Reward>, todayTargetHit: Int, active10: Int)

    fun onMessagesReceived(
        targetHit: Int,
        briskWalk: Int,
        walkingMessageResponse: WalkingMessageResponse?,
        active10: Int
    )

    fun loadBarChart(stepData: MutableList<StepOverview>, interval: IntervalWalk)

    fun onGlobalRulesRetrieved(globalRules: GlobalRules?)

    fun onRewardBadgesReceived(rewards: List<RewardBadge>)

    fun onWalkDataChanged()

    fun goToArticle(infoPage: InfoPage)

    fun showChildrenItems(items: List<ScreenContent>)
}
