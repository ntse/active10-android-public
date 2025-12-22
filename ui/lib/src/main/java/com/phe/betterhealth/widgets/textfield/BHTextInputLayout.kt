package com.phe.betterhealth.widgets.textfield

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePaddingRelative
import com.google.android.material.textfield.TextInputLayout
import com.phe.betterhealth.widgets.R

class BHTextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bhTextInputLayoutStyle,
) : TextInputLayout(context, attrs, defStyleAttr) {

    private lateinit var titleView: TextView

    private var editText: BHTextInputEditText? = null

    private var spinner: BHSpinner? = null

    private var inputTitleMarginBottom: Int = 0

    var inputTitle: String? = null
        set(title) {
            if (title == field) return
            field = title
            with(titleView) {
                text = title
                isVisible = !title.isNullOrEmpty()
                if (isVisible) {
                    when {
                        editText != null -> labelFor = editText?.id!!
                        spinner != null -> labelFor = spinner?.id!!
                    }
                }
            }
        }

    var errorMessage: String? = null
        set(error) {
            field = error
            setError(error)
        }

    init {
        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.BHTextInputLayout, defStyleAttr, 0
        )

        inflateInputTitleTextView()

        with(attributes) {
            try {
                inputTitle = getInputTitleFromAttributes()
                inputTitleMarginBottom = getInputTitleBottomMarginFromAttributes()
                errorMessage = getErrorMessageFromAttributes()
            } finally {
                recycle()
            }
        }

        setTitleBottomMargin(inputTitleMarginBottom)
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        when {
            child.id == R.id.bhTextInputLayoutTitle -> super.addView(child, 0, params)
            child is BHTextInputEditText -> {
                if (!inputTitle.isNullOrBlank()) titleView.labelFor = child.id
                editText = child
                super.addView(child, index, params)
            }
            child is BHSpinner -> {
                if (!inputTitle.isNullOrBlank()) titleView.labelFor = child.id
                spinner = child
                super.addView(child, index, params)
            }
            else -> super.addView(child, index, params)
        }

        adjustIndicatorPadding()
    }

    override fun setErrorEnabled(enabled: Boolean) {
        super.setErrorEnabled(enabled)
        adjustIndicatorPadding()
    }

    override fun setError(errorText: CharSequence?) {
        val previousErrorText = error
        super.setError(errorText)

        if (errorText.isNullOrBlank() && previousErrorText != errorText) {
            findViewById<TextView>(R.id.textinput_error)?.visibility = View.GONE
        }
    }

    override fun setHelperTextEnabled(enabled: Boolean) {
        super.setHelperTextEnabled(enabled)
        adjustIndicatorPadding()
    }

    private fun TypedArray.getErrorMessageFromAttributes(): String? {
        return getString(R.styleable.BHTextInputLayout_errorMessage)
    }

    private fun TypedArray.getInputTitleFromAttributes(): String? {
        return getString(R.styleable.BHTextInputLayout_inputTitle)
    }

    private fun TypedArray.getInputTitleBottomMarginFromAttributes(): Int {
        return getDimensionPixelSize(R.styleable.BHTextInputLayout_inputTitleMarginBottom, 0)
    }

    private fun setTitleBottomMargin(bottomMarginInPixels: Int) {
        titleView.updateLayoutParams<LayoutParams> {
            bottomMargin = bottomMarginInPixels
        }
    }

    private fun adjustIndicatorPadding() {
        if (!isErrorEnabled && !isHelperTextEnabled) return

        val errorView: TextView? = findViewById(R.id.textinput_error) ?: findViewById(R.id.textinput_helper_text)
        (errorView?.parent?.parent as? LinearLayout)?.run {
            updatePaddingRelative(start = 0)
        }
    }

    private fun inflateInputTitleTextView() {
        titleView = AppCompatTextView(context, null, R.attr.bhTextInputLayoutTitleStyle).apply {
            id = R.id.bhTextInputLayoutTitle
            textAlignment = View.TEXT_ALIGNMENT_VIEW_START

            isVisible = false
            addView(this, 0, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        }
    }
}
