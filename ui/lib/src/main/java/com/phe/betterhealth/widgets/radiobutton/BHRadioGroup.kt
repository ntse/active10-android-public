package com.phe.betterhealth.widgets.radiobutton

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.LinearLayout
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.utils.dpToPx

class BHRadioGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : RadioGroup(context, attrs) {

    private lateinit var errorView: AppCompatTextView

    private var errorMessageText: String? = null

    var errorMessage: String?
        get() = errorMessageText
        set(error) {
            if (error == errorMessageText) return
            setError(error)
            errorView.accessibilityLiveRegion = ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE
        }

    fun setErrorAndRequestFocus(error: String?) {
        if (error != errorMessageText) {
            setError(error)
        }

        if (error != null) {
            errorView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        }
    }

    fun setError(message: String?) {
        errorMessageText = message
        with(errorView) {
            text = message
            isVisible = !message.isNullOrBlank()
        }
    }

    init {
        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.BHRadioGroup, 0, 0
        )

        inflateErrorMessageTextView()

        with(attributes) {
            try {
                errorMessage = getErrorMessageFromAttributes()
            } finally {
                recycle()
            }
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, 0, params)
    }

    private fun TypedArray.getErrorMessageFromAttributes(): String? {
        return getString(R.styleable.BHRadioGroup_errorMessage)
    }

    private fun inflateErrorMessageTextView() {
        errorView = AppCompatTextView(context, null, R.attr.bhRadioGroupErrorStyle).apply {
            id = R.id.bhRadioGroupError
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START

            isVisible = false
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val margin = 16.dpToPx(context).toInt()

            params.setMargins(margin, margin, margin, 0)
            addView(this, 0, params)
        }
    }
}
