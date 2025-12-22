package com.flipsidegroup.active10.presentation.reward.presenter

import com.flipsidegroup.active10.data.EarnRewardBadge
import com.flipsidegroup.active10.data.RewardBadge
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.reward.view.RewardsView
import javax.inject.Inject

private val REWARDS_CATEGORY_ORDER =
    listOf("goalGetter", "targetChaser", "steppingUp", "highAchiever", "briskMinutes")

class RewardsPresenterImpl @Inject constructor(
    settingsUtils: SettingsUtils,
    private val localRepository: LocalRepository
) : BasePresenter<RewardsView>(), RewardsPresenter {

    private val earnedBadges: List<EarnRewardBadge> =
        settingsUtils.getSettingsHolder().earnedBadges?.sortedByDescending { it.timestamp }
            ?: emptyList()

    override fun getRewardBadges() {
        localRepository.getRewardBadges(object :
            AppDatabase.OnDataLoadedListener<List<RewardBadge>> {
            override fun onDataLoaded(data: List<RewardBadge>) {
                data.forEach { rewardBadge ->
                    if (earnedBadges.find { it.id == rewardBadge.id } != null) {
                        rewardBadge.isEarned = true
                    }
                    rewardBadge.repetitions = earnedBadges.count { it.id == rewardBadge.id }
                }
                view?.onRewardBadgesReceived(
                    data.groupBy(RewardBadge::category)
                        .entries
                        .map { Pair(it.key, it.value) }
                        .sortedBy { REWARDS_CATEGORY_ORDER.indexOf(it.first) }
                )
            }
        })
    }

    override fun selectBadge(rewardBadge: RewardBadge) {
        view?.showDialog(rewardBadge, earnedBadges)
    }
}