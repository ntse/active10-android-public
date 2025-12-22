package com.flipsidegroup.active10.data.models.requests

import com.flipsidegroup.active10.BuildConfig
import com.google.gson.annotations.SerializedName
import java.util.*


data class AddUserRequest(
    @SerializedName("ProductToken") val paragonToken: String = BuildConfig.PARAGON_TOKEN,
    @SerializedName("Firstname") val firstName: String,
    @SerializedName("Lastname") val lastName: String,
    @SerializedName("Email") val emailAddress: String,
    @SerializedName("PostCode") val postCode: String,
    @SerializedName("PHEOptIn") val optIn: String = "",
    @SerializedName("Gender") val gender: String,
    @SerializedName("DaysSinceDownload") val daysSinceDownload: Int = 0,
    @SerializedName("UserAppId") val userAppId: String = UUID.randomUUID().toString()
)