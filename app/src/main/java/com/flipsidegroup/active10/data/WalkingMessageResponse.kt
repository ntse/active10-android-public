package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class WalkingMessageResponse(
    @SerializedName("id") @PrimaryKey var id: Int = 0,
    @SerializedName("my_walks_dynamic_text") var myWalksTexts: MyWalksMessages? = null,
    @SerializedName("todays_walks_dynamic_text") var todayWalkTexts: TodayWalkMessages? = null

) : RealmObject()