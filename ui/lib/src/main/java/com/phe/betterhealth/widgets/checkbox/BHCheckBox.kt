package com.phe.betterhealth.widgets.checkbox

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import com.phe.betterhealth.widgets.R

class BHCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bhCheckBoxStyle,
) : AppCompatCheckBox(context, attrs, defStyleAttr)
