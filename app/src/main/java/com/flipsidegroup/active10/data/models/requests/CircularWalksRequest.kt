package com.flipsidegroup.active10.data.models.requests

import com.google.gson.annotations.SerializedName

data class CircularWalksRequest (
    @SerializedName("locale")
    val locale: String = "en-GB",

    @SerializedName("max_paths")
    val maxPaths: Int = 2,

    @SerializedName("details")
    val details: List<String> = listOf(
        "road_environment",
        "potentially_unsuitable",
        "potentially_private"
    ),

    @SerializedName("points_encoded")
    val pointsEncoded: Boolean = true,

    @SerializedName("instructions")
    val instructions: Boolean = false,

    @SerializedName("postcode")
    val postcode: String,

    @SerializedName("title_unit_format")
    val titleUnitFormat: String = "km",

    @SerializedName("distances")
    val distances: List<Int> = listOf(2000, 10000),

    @SerializedName("categorise")
    val categorise: Boolean = false,

    @SerializedName("store")
    val store: Boolean = true
)