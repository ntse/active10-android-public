package com.flipsidegroup.active10.utils

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.flipsidegroup.active10.R
import com.google.android.material.textfield.TextInputLayout

open class FormsInput : TextInputLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setErrorTextColor(getErrorColorStateList(R.color.error_red))
    }

    private fun getErrorColorStateList(color: Int): ColorStateList? {
        return ColorStateList(
            arrayOf(intArrayOf()),
            intArrayOf(ContextCompat.getColor(context, color))
        )
    }
}