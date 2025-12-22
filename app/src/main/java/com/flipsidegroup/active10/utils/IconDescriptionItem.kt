package com.flipsidegroup.active10.utils

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.flipsidegroup.active10.R

class IconDescriptionItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    init {
        inflate(context, R.layout.item_sign_in_icon_description, this)
    }

}