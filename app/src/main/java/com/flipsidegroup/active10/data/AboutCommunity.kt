package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class AboutCommunity(
    @SerializedName("id") @PrimaryKey var id: Int = 1,
    @SerializedName("about") var communityDescription: AboutCommunityDescription? = null,
    @SerializedName("apps") var appList: RealmList<CommunityApp> = RealmList()
) : RealmObject()