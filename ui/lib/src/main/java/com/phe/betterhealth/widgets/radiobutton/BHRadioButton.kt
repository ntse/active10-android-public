package com.phe.betterhealth.widgets.radiobutton

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.radiobutton.MaterialRadioButton
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.utils.dpToPx

class BHRadioButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bhRadioButtonStyle
) : MaterialRadioButton(context, attrs, defStyleAttr) {

    init {
        val minHeightPx = 48.dpToPx(context).toInt()
        setMinimumHeight(minHeightPx)
    }
}
