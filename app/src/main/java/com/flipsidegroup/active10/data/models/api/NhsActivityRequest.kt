package com.flipsidegroup.active10.data.models.api

import com.google.gson.annotations.SerializedName

data class NhsActivityRequest (
    @SerializedName("date")
    val timestampInSeconds: Long,
    @SerializedName("user_postcode")
    val userPostcode: String,
    @SerializedName("user_age_range")
    val userAgeRange: String,
    @SerializedName("rewards")
    val rewards: List<NhsReward>,
    @SerializedName("activity")
    val activity: NhsActivity
)

data class  NhsActivity(
    @SerializedName("steps")
    val steps: Int,
    @SerializedName("brisk_minutes")
    val briskMinutes: Int,
    @SerializedName("walking_minutes")
    val walkingMinutes: Int,
)

data class NhsReward(
    @SerializedName("date") // "date" == earned
    val earned: Double,
    @SerializedName("reward") // "reward" == slug
    val slug: String
)

data class NhsActivityBulkRequest(
    @SerializedName("month")
    val month: Long,
    @SerializedName("activities")
    val activities: List<NhsActivityRequest>
)