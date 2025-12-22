package com.flipsidegroup.active10.data.models.api

import com.google.gson.annotations.SerializedName

data class NhsActivityResponse (
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    // Timestamp, seconds since epoch
    @SerializedName("date")
    val timestampInSeconds: Long,
    @SerializedName("user_postcode")
    val userPostcode: String,
    @SerializedName("user_age_range")
    val userAgeRange: String,
    @SerializedName("rewards")
    val rewards: List<NhsReward>,
    @SerializedName("steps")
    val steps: Int,
    @SerializedName("brisk_minutes")
    val briskMinutes: Int,
    @SerializedName("walking_minutes")
    val walkingMinutes: Int,
)
