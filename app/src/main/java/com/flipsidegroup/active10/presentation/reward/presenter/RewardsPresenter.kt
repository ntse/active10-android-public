package com.flipsidegroup.active10.presentation.reward.presenter

import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.reward.view.RewardsView

interface RewardsPresenter : LifecycleAwarePresenter<RewardsView> {

    fun selectBadge(rewardBadge: RewardBadge)

    fun getRewardBadges()
}