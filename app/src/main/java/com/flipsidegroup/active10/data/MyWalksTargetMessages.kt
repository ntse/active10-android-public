package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class MyWalksTargetMessages(

    @SerializedName("0_a10s_0_mins") var noActive0Mins: String = "",
    @SerializedName("0_a10s_x_mins") var noActiveXMins: String = "",

    @SerializedName("1_a10s_0_mins") var oneTarget0Mins: String = "",
    @SerializedName("1_a10s_x_mins") var oneTargetXMins: String = "",

    @SerializedName("2_a10s_0_mins") var twoTargets0Mins: String = "",
    @SerializedName("2_a10s_x_mins") var twoTargetsXMins: String = "",

    @SerializedName("3_a10s_0_mins") var threeTargets0Mins: String = "",
    @SerializedName("3_a10s_x_mins") var threeTargetsXMins: String = "",

    @SerializedName("4_a10s_0_mins") var fourTargets0Mins: String = "",
    @SerializedName("4_a10s_x_mins") var fourTargetsXMins: String = "",

    @SerializedName("5_a10s_0_mins") var fiveTargets0Mins: String = "",
    @SerializedName("5_a10s_x_mins") var fiveTargetsXMins: String = "",

    @SerializedName("more_than_3_active_tens") var moreThreeTargets: String = "",
    @SerializedName("more_than_5_active_tens") var moreFiveTargets: String = ""
) : RealmObject()