package com.flipsidegroup.active10.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import kotlinx.android.parcel.Parcelize

@Parcelize
open class NotificationUserInfo(
    @SerializedName("action")
    var action: String = "",
    @SerializedName("not_same_day")
    var notSameDay: String? = null
) : RealmObject(), Parcelable