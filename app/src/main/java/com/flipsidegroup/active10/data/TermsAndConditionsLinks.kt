package com.flipsidegroup.active10.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import kotlinx.android.parcel.Parcelize



@Parcelize
open class TermsAndConditionsLinks(
    @SerializedName("url")
    var url: String = "",
    @SerializedName("link")
    var link: String = ""
) : RealmObject(), Parcelable