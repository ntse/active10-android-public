package com.flipsidegroup.active10.data.models.requests

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LatestActivityLevelRequest(
    @SerializedName("level")
    val level: String
) : Parcelable

@Parcelize
data class LatestMotivationRequest(
    @SerializedName("goals")
    val goals: List<NhsUserGoalRequest> = emptyList()
) : Parcelable

@Parcelize
data class NhsUserGoalRequest(
    @SerializedName("id")
    val id: Int,
    @SerializedName("text")
    val text: String
) : Parcelable