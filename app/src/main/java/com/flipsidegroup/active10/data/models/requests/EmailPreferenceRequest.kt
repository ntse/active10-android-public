package com.flipsidegroup.active10.data.models.requests

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmailPreferenceRequest(
    @SerializedName("name")
    val name: String,
) : Parcelable