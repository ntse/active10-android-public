package com.phe.betterhealth.demo

import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.banner.BHBannerIconType

data class BannerItem(
    val infoText: String? = null,
    val infoTextColor: Int = R.color.bhWhite,
    val imageSrc: String? = null,
    val iconColor: Int = R.color.bhWhite,
    val iconType: BHBannerIconType = BHBannerIconType.INDICATOR,
)
