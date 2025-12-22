package com.phe.betterhealth.components.share

interface ShareButton {
    val shareButtonStyle: String
    val shareButtonTitle: String
    val shareButtonDescription: String
    val shareButtonAccessibilityLabel: String
    val shareButtonExtendedStyle: ShareButtonExtendedStyle
        get() = ShareButtonExtendedStyle.PRIMARY
}

enum class ShareButtonStyle {
    NONE, SMALL, STANDARD, EXTENDED;

    val value: String get() = name.lowercase()

    companion object {
        fun byName(value: String): ShareButtonStyle = values().find { it.value == value } ?: NONE
    }
}

enum class ShareButtonExtendedStyle {
    PRIMARY,
    SECONDARY,
}
