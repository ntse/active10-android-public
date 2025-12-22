package com.flipsidegroup.active10.data.models.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CircularWalkResponse(
    @SerializedName("distance") val distance: Double,
    @SerializedName("uuid") val uuid: String,
    @SerializedName("ascend") val ascend: Int,
    @SerializedName("descend") val descend: Int,
    @SerializedName("url") val url: String?,
    @SerializedName("safety") val safety: String,
    @SerializedName("notices") val notices: List<Notice>,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String?,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: Double
): Parcelable

@Parcelize
data class Notice(
    @SerializedName("type") val type: String,
    @SerializedName("message") val message: String
): Parcelable