@file:JvmName("GraphicUtils")

package com.phe.betterhealth.widgets.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

fun Context.getCompatDrawable(@DrawableRes drawableRes: Int) =
    ContextCompat.getDrawable(this, drawableRes)

@ColorInt
fun Context.getCompatColor(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

@ColorInt
fun Context.getThemeAttrColor(@AttrRes colorAttr: Int): Int {
    val array = obtainStyledAttributes(null, intArrayOf(colorAttr))
    return try {
        array.getColor(0, 0)
    } finally {
        array.recycle()
    }
}

fun Int.dpToPx(context: Context): Float = toFloat().dpToPx(context)

fun Float.dpToPx(context: Context): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)

fun Float.pxToDp(context: Context): Float =
    (this / context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)

fun Int.spToPx(context: Context): Float = toFloat().spToPx(context)

fun Float.spToPx(context: Context): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics)

fun Float.pxToSp(context: Context): Float = this / context.resources.displayMetrics.scaledDensity
