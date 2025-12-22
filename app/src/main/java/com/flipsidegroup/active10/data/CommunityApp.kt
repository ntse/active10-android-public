package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject


open class CommunityApp(
    @SerializedName("name") var appName: String = "",
    @SerializedName("description") var appDescription: String = "",
    @SerializedName("android") var storeLink: String = "",
    @SerializedName("icon") var icon: String = ""
) : RealmObject()