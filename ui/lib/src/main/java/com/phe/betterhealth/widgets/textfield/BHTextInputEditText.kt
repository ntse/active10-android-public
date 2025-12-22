package com.phe.betterhealth.widgets.textfield

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.utils.dpToPx

class BHTextInputEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bhTextInputEditTextStyle,
) : TextInputEditText(context, attrs, defStyleAttr) {

    init {
        minimumHeight = 48.dpToPx(context).toInt()
        doOnTextChanged { _, _, _, _ ->
            (parent.parent as? BHTextInputLayout)?.isErrorEnabled = false
        }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        setSelection(text?.length ?: 0)
    }
}
