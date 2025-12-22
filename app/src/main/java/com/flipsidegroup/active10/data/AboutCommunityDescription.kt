package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class AboutCommunityDescription(
    @SerializedName("text") var text: String = ""
) : RealmObject()