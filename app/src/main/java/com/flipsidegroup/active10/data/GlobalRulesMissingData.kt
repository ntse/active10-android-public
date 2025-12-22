package com.flipsidegroup.active10.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import kotlinx.android.parcel.Parcelize


@Parcelize
open class GlobalRulesMissingData(
    @SerializedName("title_android")
    var title: String = "",
    @SerializedName("text_android")
    var text: String = ""
) : RealmObject(), Parcelable