package com.flipsidegroup.active10.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import kotlinx.parcelize.Parcelize

@Parcelize
open class LocalNotification(
    @SerializedName("slug")
    var slug: String = "",
    @SerializedName("title")
    var title: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("destination")
    var destination: String = "",
    @SerializedName("isLapsed")
    var isLapsed: Boolean = false,
) : RealmObject(), Parcelable