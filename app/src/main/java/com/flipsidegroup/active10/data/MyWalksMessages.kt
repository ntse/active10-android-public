package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class MyWalksMessages(

    @SerializedName("days_today_no_walking") var todayNoWalking: String = "",
    @SerializedName("days_target_hit") var daysTargetHit: String = "",
    @SerializedName("days_target_no_hit") var daysTargetNoHit: String = "",
    @SerializedName("days_no_brisk_walking") var daysNoBrisk: String = "",

    @SerializedName("weeks_current") var weekCurrent: String = "",
    @SerializedName("weeks_last_target_hit_can_increase") var lastWeekTargetHitIncreaseTarget: String = "",
    @SerializedName("weeks_last_target_4_to_6_can_increase") var lastWeek4_6IncreaseTarget: String = "",
    @SerializedName("weeks_last_target_1_days_1") var lastWeekTargetOneDaysOne: String = "",
    @SerializedName("weeks_last_target_x_days_1") var lastWeekTargetXDaysOne: String = "",
    @SerializedName("weeks_last_target_1_days_x") var lastWeekTargetOneDaysX: String = "",
    @SerializedName("weeks_last_target_x_days_x") var lastWeekTargetXDaysX: String = "",
    @SerializedName("weeks_brisk_150") var weekBrisk150: String = "",

    @SerializedName("weeks_days_7") var weeksDays7: String = "",
    @SerializedName("weeks_days_4_6") var weeksDays4_6: String = "",
    @SerializedName("weeks_days_0") var weekDays0: String = "",
    @SerializedName("weeks_days_1") var weekDays1: String = "",
    @SerializedName("weeks_days_2_3") var weekDays2_3: String = "",

    @SerializedName("no_rewards_today") var noRewardsToday: String = "",
    @SerializedName("no_rewards_this_day") var noRewardsThisDay: String = "",
    @SerializedName("no_rewards_this_week") var noRewardsThisWeek: String = "",
    @SerializedName("no_rewards_this_month") var noRewardsThisMonth: String = "",

    @SerializedName("breakdown_accessibility_text") var breakdownAccessibilityText: String = ""

) : RealmObject()