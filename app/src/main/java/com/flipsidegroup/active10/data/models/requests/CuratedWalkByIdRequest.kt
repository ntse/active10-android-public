package com.flipsidegroup.active10.data.models.requests

import com.google.gson.annotations.SerializedName

data class CuratedWalkByIdRequest(
    @SerializedName("includeSteps")
    val includeSteps: Boolean = true,

    @SerializedName("includeRelated")
    val includeRelated: Boolean = false,

)