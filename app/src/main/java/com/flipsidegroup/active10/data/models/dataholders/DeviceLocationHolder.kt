package com.flipsidegroup.active10.data.models.dataholders

import com.google.gson.annotations.SerializedName



data class DeviceLocationHolder(

    @SerializedName("a")
    val latitude: Double = 0.0,

    @SerializedName("b")
    val longitude: Double = 0.0
)