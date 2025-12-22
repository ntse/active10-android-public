package com.flipsidegroup.active10.data.models.dataholders

import com.google.gson.annotations.SerializedName
import java.util.*


class TargetHolder(

    @SerializedName("a")
    val target: Int?,

    @SerializedName("b")
    val timestamp: Long = Date().time
)