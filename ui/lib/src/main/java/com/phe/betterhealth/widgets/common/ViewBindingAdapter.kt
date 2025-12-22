package com.phe.betterhealth.widgets.common

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import androidx.core.view.updatePaddingRelative
import androidx.databinding.BindingAdapter
import com.phe.betterhealth.widgets.utils.dpToPx

@BindingAdapter("android:visibility")
fun View.setVisibility(visible: Boolean) {
    isVisible = visible
}

@BindingAdapter("android:layout_marginHorizontal")
fun View.setMarginHorizontal(dp: Float?) {
    dp ?: return

    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        updateMarginsRelative(start = dp.dpToPx(context).toInt(), end = dp.dpToPx(context).toInt())
    }
}

@BindingAdapter("android:layout_marginBottom")
fun View.setMarginBottom(dp: Float?) {
    dp ?: return

    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        updateMarginsRelative(bottom = dp.dpToPx(context).toInt())
    }
}

@BindingAdapter("android:layout_marginTop")
fun View.setMarginTop(dp: Float?) {
    dp ?: return

    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        updateMarginsRelative(top = dp.toInt())
    }
}

@BindingAdapter("android:layout_marginTop")
fun View.setMarginTop(dp: Int?) {
    dp ?: return

    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        updateMarginsRelative(top = dp.dpToPx(context).toInt())
    }
}

@BindingAdapter("android:paddingHorizontal")
fun View.setHorizontalPadding(dp: Float?) {
    dp ?: return
    val value = dp.dpToPx(context).toInt()
    updatePaddingRelative(start = value, end = value)
}

@BindingAdapter("paddingStart")
fun View.setPaddingStart(dp: Float?) {
    dp ?: return
    updatePaddingRelative(start = dp.dpToPx(context).toInt())
}

@BindingAdapter("paddingBottom")
fun View.setPaddingBottom(dp: Float?) {
    dp ?: return
    updatePaddingRelative(bottom = dp.dpToPx(context).toInt())
}

@BindingAdapter("layout_height")
fun View.setHeight(dp: Float?) {
    dp ?: return

    updateLayoutParams<ViewGroup.LayoutParams> {
        height = dp.dpToPx(context).toInt()
    }
}

@BindingAdapter("isVisible")
fun View.setViewVisible(isViewVisible: Boolean) {
    isVisible = isViewVisible
}
