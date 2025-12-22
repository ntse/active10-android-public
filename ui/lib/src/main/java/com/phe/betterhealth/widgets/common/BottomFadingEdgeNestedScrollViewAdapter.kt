package com.phe.betterhealth.widgets.common

import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter

@BindingAdapter("fadingEdgeBackgroundColor")
fun BottomFadingEdgeNestedScrollView.setFadingEdgeBackgroundColor(@ColorRes fadingEdgeBackgroundColor: Int?) {
    this.fadingEdgeBackgroundColor = fadingEdgeBackgroundColor
}
