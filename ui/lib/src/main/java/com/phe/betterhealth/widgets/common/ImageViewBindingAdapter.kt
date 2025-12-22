package com.phe.betterhealth.widgets.common

import android.content.res.ColorStateList
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.widget.ImageViewCompat
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import timber.log.Timber

@BindingAdapter("srcUrl")
fun ImageView.loadImage(url: String?) {
    url.takeIf { !it.isNullOrBlank() } ?: return
    load(url?.replace("%3A", ":")) {
        crossfade(true)
        listener(
            onError = {_,e -> Timber.e(e.throwable) }
        )
    }
}

@BindingAdapter("srcUrlSoftware")
fun ImageView.loadImageSoftware(url: String?) {
    url.takeIf { !it.isNullOrBlank() } ?: return
    load(url) {
        allowHardware(false)
    }
}

@BindingAdapter("srcUrl")
fun ImageView.loadImage(uri: Uri?) {
    uri.takeIf { !it?.toString().isNullOrBlank() } ?: return
    load(uri) {
        crossfade(true)
        listener(
            onError = {_,e -> Timber.e(e.throwable) }
        )
    }
}

@BindingAdapter("circleSrcUrl")
fun ImageView.loadCircleImage(url: String?) {
    url.takeIf { !it?.toString().isNullOrBlank() } ?: return
    load(url) {
        transformations(CircleCropTransformation())
        crossfade(true)
    }
}

@BindingAdapter("src")
fun ImageView.loadImage(@DrawableRes res: Int) {
    load(res)
}

@BindingAdapter("tint")
fun ImageView.setTint(@ColorInt colorInt: Int) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(colorInt))
}

@BindingAdapter("grayedOut")
fun ImageView.grayedOut(enabled: Boolean) {
    if (enabled) {
        colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
        imageAlpha = 127
    } else {
        colorFilter = null
        imageAlpha = 255
    }
}
