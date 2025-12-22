package com.flipsidegroup.active10.utils

import com.flipsidegroup.active10.data.EarnRewardBadge
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper

object EarnBadgeHelper {

    fun saveEarnedBadgeWithoutCheck(
        settingsUtils: SettingsUtils,
        preferenceRepository: PreferenceRepository,
        badge: RewardBadgeEnum,
        timestamp: Long? = null
    ) {
        val badges = settingsUtils.getSettingsHolder().earnedBadges ?: arrayListOf()
        badges.add(EarnRewardBadge(badge.id, badge.slug, timestamp ?: System.currentTimeMillis()))
        settingsUtils.updateSettings(SettingsDataHolder(earnedBadges = badges))
        FirebaseAnalyticsHelper(settingsUtils, preferenceRepository).saveRewardEvent(
            Constants.FirebaseAnalytics.EARNED_REWARD_EVENT,
            badge.slug
        )
    }

    fun saveEarnedBadge(
        settingsUtils: SettingsUtils,
        badge: RewardBadgeEnum,
        timestamp: Long? = null,
        preferenceRepository: PreferenceRepository,
    ) {
        val badges = settingsUtils.getSettingsHolder().earnedBadges ?: arrayListOf()
        val earnedBadge = badges.find { it.id == badge.id }
        if (earnedBadge == null
            || earnedBadge.timestamp > (timestamp ?: System.currentTimeMillis())
        ) {
            if (earnedBadge != null) {
                badges.remove(earnedBadge)
            }
            badges.add(
                EarnRewardBadge(
                    badge.id,
                    badge.slug,
                    timestamp ?: System.currentTimeMillis()
                )
            )
            settingsUtils.updateSettings(SettingsDataHolder(earnedBadges = badges))
            FirebaseAnalyticsHelper(settingsUtils, preferenceRepository).saveRewardEvent(
                Constants.FirebaseAnalytics.EARNED_REWARD_EVENT,
                badge.slug
            )
        }
    }

    fun markBadgeAsShown(rewardId: Int, settingsUtils: SettingsUtils) {
        val currentEarnedRewards = settingsUtils.getSettingsHolder().earnedBadges
        currentEarnedRewards?.find { it.id == rewardId }?.wasShown = true
        settingsUtils.updateSettings(SettingsDataHolder(earnedBadges = currentEarnedRewards))
    }
}
