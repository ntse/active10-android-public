package com.flipsidegroup.active10.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import kotlinx.android.parcel.Parcelize


@Parcelize
open class GlobalRulesAppAndroid(
    @SerializedName("latest_version")
    var latestVersion: String = "",
    @SerializedName("title")
    var title: String = "",
    @SerializedName("text")
    var text: String = "",
    @SerializedName("button")
    var button: String = ""
) : RealmObject(), Parcelable