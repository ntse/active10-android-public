package com.flipsidegroup.active10.data.models.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NhsUserDetails(
    @SerializedName("id")
    val id: String? = "", // TODO: Check user's parameters when backend is ready
    @SerializedName("first_name")
    val firstName: String? = "",
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("age")
    val age: Int? = null,
    @SerializedName("age_range")
    val ageRange: String? = "",
    @SerializedName("postcode")
    val postcode: String? = null,
    @SerializedName("is_email_updates_allowed")
    val isEmailUpdatesAllowed: Boolean = false,
    @SerializedName("gender")
    val gender: String? = "",
) : Parcelable