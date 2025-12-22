package com.flipsidegroup.active10.presentation.walksnear.adapters

enum class WalksNearType {
    NO_POSTCODE,
    CREDENTIALS,
    INTRO,
    BANNER,
    CIRCULAR_WALK,
    CURATED_WALK,
}

sealed class WalksNearPart(
    val type: WalksNearType,
    open val id: Int,
) {
    data class NoPostcode(
        val description: String,
    ) : WalksNearPart(WalksNearType.NO_POSTCODE, -4)

    class Credentials : WalksNearPart(WalksNearType.CREDENTIALS, -3)

    data class Intro(
        val title: String,
        val description: String,
    ) : WalksNearPart(WalksNearType.INTRO, -2)

    data class Banner(
        val description: String,
        val postcode: String,
    ) : WalksNearPart(WalksNearType.BANNER, -1)

    data class CircularWalk(
        override val id: Int,
        val image: String,
        val title: String,
        val duration: Double,
        val distance: Double,
        val btnTitle: String,
        val onClickCallback: (Long) -> Unit,
    ) : WalksNearPart(WalksNearType.CIRCULAR_WALK, id)

    data class CuratedWalk(
        override val id: Int,
        val image: String,
        val title: String,
        val location: String,
        val duration: Double,
        val distance: Double,
        val summary: String,
        val btnTitle: String,
        val onClickCallback: (Long) -> Unit,
        val postcodeDistance: Double?,
    ) : WalksNearPart(WalksNearType.CURATED_WALK, id)
}