package com.flipsidegroup.active10.data.models.requests

import com.google.gson.annotations.SerializedName

data class CuratedWalksRequest(
    @SerializedName("title_unit_format")
    val titleUnitFormat: String = "km",

    @SerializedName("amount")
    val amount: Int = 50,

    @SerializedName("radius")
    val radius: Int = 10,

    @SerializedName("userLocation")
    val userLocation: Boolean = false,

    @SerializedName("types")
    val types: List<String> = listOf("normal", "track"),

    @SerializedName("postcode")
    val postcode: String,

    @SerializedName("lang")
    val lang: String = "en-GB",

    @SerializedName("excludingIds")
    val excludingIds: List<Int> = emptyList(),

    @SerializedName("page")
    val page: Int = 0
)