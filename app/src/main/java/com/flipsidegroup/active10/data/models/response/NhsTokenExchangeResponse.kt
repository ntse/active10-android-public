package com.flipsidegroup.active10.data.models.response

import com.google.gson.annotations.SerializedName

data class NhsTokenExchangeResponse(
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("token")
    val token: String? = null,
)
