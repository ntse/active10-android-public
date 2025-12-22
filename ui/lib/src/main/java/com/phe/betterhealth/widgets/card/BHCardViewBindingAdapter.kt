package com.phe.betterhealth.widgets.card

import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import com.phe.betterhealth.widgets.utils.dpToPx

@BindingAdapter("cardElevation")
fun MaterialCardView.setCardElevation(elevation: Float?) {
    cardElevation = (elevation ?: 0f).dpToPx(context)
}

@BindingAdapter("cardRadius")
fun MaterialCardView.setCardRadius(radius: Float?) {
    setRadius((radius ?: 0f).dpToPx(context))
}

@BindingAdapter("strokeWidth")
fun MaterialCardView.setStrokeWidth(width: Float?) {
    strokeWidth = (width ?: 0f).dpToPx(context).toInt()
}
