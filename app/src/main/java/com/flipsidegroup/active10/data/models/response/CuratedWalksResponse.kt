package com.flipsidegroup.active10.data.models.response

import android.location.Address
import android.location.Location
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CuratedWalk(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("location") val location: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("duration") val duration: Int,
    @SerializedName("distance") val distance: Double,
    @SerializedName("image") val image: String,
    @SerializedName("thumbnailImage") val thumbnailImage: String,
    @SerializedName("attributes") val attributes: List<String>,
    @SerializedName("type") val type: String,
    @SerializedName("url") val url: String?,
    @SerializedName("steps") val steps: List<CuratedWalkStep>?,
    @SerializedName("mapImage") val mapImage: MapImage?

): Parcelable {

    fun getDistanceFromPostcode(coordinates: Address): Double {
        return Location("userLocation").apply {
            latitude = coordinates.latitude
            longitude = coordinates.longitude
        }.distanceTo(Location("walkLocation").apply {
            latitude = lat
            longitude = lon
        }).toDouble()
    }
}

@Parcelize
data class CuratedWalkStep(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("type") val type: String,
    @SerializedName("image") val image: String,
    @SerializedName("summary") val summary: String,
): Parcelable

data class CuratedWalksResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("walks") val walks: List<CuratedWalk>
)

@Parcelize
data class MapImage(
    @SerializedName("small") val small: Size,
    @SerializedName("large") val large: Size
): Parcelable

@Parcelize
data class Size(
    @SerializedName("jpeg") val jpeg: Jpeg
): Parcelable

@Parcelize
data class Jpeg(
    @SerializedName("1x") val oneX: String,
    @SerializedName("2x") val twoX: String
): Parcelable