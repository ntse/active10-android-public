package com.phe.betterhealth.widgets.button

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.utils.ActionDebouncer

open class BHButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bhButtonStyle,
) : MaterialButton(context, attrs, defStyleAttr) {

    private val actionDebouncer = ActionDebouncer()

    override fun performClick() = actionDebouncer.onAction(false) {
        super.performClick()
    }
}
