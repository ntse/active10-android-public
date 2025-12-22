package com.flipsidegroup.active10.data

import androidx.annotation.StringRes
import com.flipsidegroup.active10.R

enum class OnboardingNotificationEnum(
    val id: Int,
    val timeSinceInstallation: Int,
    @StringRes val message: Int,
    val needTodayCheck: Boolean,
    val nextNotificationId: Int?
) {

    DAY_3(
        3,
        3,
        R.string.day_3_notification,
        false,
        5
    ),
    DAY_5(
        5,
        5,
        R.string.day_5_notification,
        false,
        7
    ),
    DAY_7(
        7,
        7,
        R.string.day_7_notification,
        false,
        10
    ),
    DAY_10(
        10,
        10,
        R.string.day_10_notification,
        true,
        14
    ),
    DAY_14(
        14,
        14,
        R.string.day_14_notification,
        false,
        18
    ),
    DAY_18(
        18,
        18,
        R.string.day_18_notification,
        true,
        21
    ),
    DAY_21(
        21,
        21,
        R.string.day_21_notification,
        false,
        null
    );

    companion object {
        fun getOnboardingNotificationById(id: Int): OnboardingNotificationEnum? =
            OnboardingNotificationEnum.values().firstOrNull {
                it.id == id
            }
    }
}