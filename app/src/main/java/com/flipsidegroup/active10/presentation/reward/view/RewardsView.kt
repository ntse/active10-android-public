package com.flipsidegroup.active10.presentation.reward.view

import com.flipsidegroup.active10.data.EarnRewardBadge
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface RewardsView : BaseView {

    fun onRewardBadgesReceived(data: List<Pair<String, List<RewardBadge>>>)

    fun showDialog(rewardBadge: RewardBadge, earnedBadges: List<EarnRewardBadge>)
}