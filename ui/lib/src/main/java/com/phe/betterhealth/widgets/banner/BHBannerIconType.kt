package com.phe.betterhealth.widgets.banner

enum class BHBannerIconType(val id: Int) {
    CLOSE(0),
    INDICATOR(1),
    NONE(2);

    companion object {
        fun fromParams(id: Int): BHBannerIconType {
            return when (id) {
                0 -> CLOSE
                1 -> INDICATOR
                2 -> NONE
                else -> throw IllegalAccessException("Unsupported BHBannerIconType")
            }
        }
    }
}
