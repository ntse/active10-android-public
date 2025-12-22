package com.phe.betterhealth.widgets.textfield

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.phe.betterhealth.widgets.R

class BHSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bhSpinnerStyle,
) : MaterialAutoCompleteTextView(context, attrs, defStyleAttr) {

    init {
        doOnTextChanged { _, _, _, _ ->
            (parent.parent as? BHTextInputLayout)?.isErrorEnabled = false
        }
    }
}
