package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class EarnRewardBadge(

    @SerializedName("id")
    val id: Int,

    @SerializedName("slug")
    val slug: String,

    @SerializedName("timestamp")
    val timestamp: Long = Date().time,

    @SerializedName("wasShown")
    var wasShown: Boolean = false,
): java.io.Serializable