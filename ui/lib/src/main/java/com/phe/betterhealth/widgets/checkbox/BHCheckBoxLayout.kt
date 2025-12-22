package com.phe.betterhealth.widgets.checkbox

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.phe.betterhealth.widgets.R
import com.phe.betterhealth.widgets.utils.dpToPx

@Suppress("MemberVisibilityCanBePrivate", "unused")
class BHCheckBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bhCheckBoxLayoutStyle
) : LinearLayout(context, attrs, defStyleAttr) {

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
            attrs, R.styleable.BHCheckBoxLayout, defStyleAttr, 0
        )

        inflateErrorMessageTextView()

        try {
            errorMessage = attributes.getErrorMessageFromAttributes()
        } finally {
            attributes.recycle()
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, 0, params)
    }

    private fun TypedArray.getErrorMessageFromAttributes(): String? {
        return getString(R.styleable.BHCheckBoxLayout_errorMessage)
    }

    private fun inflateErrorMessageTextView() {
        errorView = AppCompatTextView(context, null, R.attr.bhCheckBoxLayoutErrorStyle).apply {
            id = R.id.bhCheckBoxLayoutError
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START

            isVisible = false
            val params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

            val margin = 16.dpToPx(context).toInt()
            params.setMargins(margin, margin, margin, 0)
            addView(this, 0, params)
        }
    }
}
