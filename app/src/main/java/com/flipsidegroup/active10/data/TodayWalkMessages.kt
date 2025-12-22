package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class TodayWalkMessages(

    @SerializedName("target_1_a10s") var myWalksTarget1: MyWalksTargetMessages? = null,
    @SerializedName("target_2_a10s") var myWalksTarget2: MyWalksTargetMessages? = null,
    @SerializedName("target_3_a10s") var myWalksTarget3: MyWalksTargetMessages? = null,
    @SerializedName("target_4_a10s") var myWalksTarget4: MyWalksTargetMessages? = null,
    @SerializedName("target_5_a10s") var myWalksTarget5: MyWalksTargetMessages? = null

) : RealmObject()