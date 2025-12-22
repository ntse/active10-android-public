package com.flipsidegroup.active10.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import com.flipsidegroup.active10.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

open class InputEditText : TextInputEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        textInputEditText.setOnFocusChangeListener { _, hasFocus ->
            updateHintPosition(hasFocus, !textInputEditText.text.isNullOrEmpty())
        }
        textInputEditText.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if ((textInputLayout?.height ?: 0) > 0) {
                    textInputLayout?.viewTreeObserver?.removeOnPreDrawListener(this)
                    updateHintPosition(
                        textInputEditText.hasFocus(),
                        !textInputEditText.text.isNullOrEmpty()
                    )
                    return false
                }
                return true
            }
        })
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)

        background = if (enabled) {
            null
        } else {
            context.getDrawable(R.drawable.input_backgroun_disabled)
        }
    }

    private var paddingBottomBackup: Int? = null
    private val textInputEditText: TextInputEditText
        get() {
            return this
        }
    private val textInputLayout: TextInputLayout?
        get() {
            return if (parent is TextInputLayout) (parent as? TextInputLayout) else (parent?.parent as? TextInputLayout)
        }

    private fun updateHintPosition(hasFocus: Boolean, hasText: Boolean) {
        if (paddingBottomBackup == null)
            paddingBottomBackup = paddingBottom

        if (hasFocus || hasText)
            textInputEditText.setPadding(
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottomBackup!!
            )
        else
            textInputEditText.setPadding(
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottomBackup!! + getTextInputLayoutTopSpace()
            )
    }

    private fun getTextInputLayoutTopSpace(): Int {
        var currentView: View = textInputEditText
        var space = 0
        do {
            space += currentView.top
            currentView = currentView.parent as View
        } while (currentView !is TextInputLayout)
        return space
    }

    fun setTextPadding(left: Float, top: Float, right: Float, bottom: Float) {
        setPadding(
            UIUtils.convertDpToPx(left),
            UIUtils.convertDpToPx(top),
            UIUtils.convertDpToPx(right),
            UIUtils.convertDpToPx(bottom)
        )
    }
}