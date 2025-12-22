package com.phe.betterhealth.widgets.utils

import android.content.res.ColorStateList
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat

var TextView.textColor
    get() = currentTextColor
    set(color) {
        colorStateList = ContextCompat.getColorStateList(context, color)
    }

var TextView.colorStateList: ColorStateList?
    get() = textColors
    set(colorStateList) {
        setTextColor(colorStateList)
        TextViewCompat.setCompoundDrawableTintList(this, colorStateList)
    }
