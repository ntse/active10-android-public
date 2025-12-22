package com.phe.betterhealth.widgets.button

import androidx.databinding.BindingAdapter
import com.phe.betterhealth.widgets.utils.dpToPx

@BindingAdapter("strokeWidth")
fun BHButton.setStrokeWidth(strokeWidth: Float?) {
    setStrokeWidth(strokeWidth?.dpToPx(context)?.toInt() ?: 0)
}
