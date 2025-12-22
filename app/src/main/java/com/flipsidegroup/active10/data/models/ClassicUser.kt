package com.flipsidegroup.active10.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class ClassicUser(
    @SerializedName("gender")
    var gender: String? = null,
    @SerializedName("age")
    var age: Int? = null,
    @SerializedName("activity_level")
    var activityLevel: String? = null,
) : Parcelable