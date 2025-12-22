package com.flipsidegroup.active10.data.models.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
open class DiscoverCategory(
    @SerializedName("id") @PrimaryKey var id: Int = 0,
    @SerializedName("name") var name: String = "",
    @SerializedName("position") var position: Int = 0,
): Parcelable, RealmObject()
