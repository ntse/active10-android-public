package com.flipsidegroup.active10.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import kotlinx.android.parcel.Parcelize

@Parcelize
open class AccessibilityStatement(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("device")
    var device: String = "",
    @SerializedName("text")
    var text: String = ""
) : RealmObject(), Parcelable