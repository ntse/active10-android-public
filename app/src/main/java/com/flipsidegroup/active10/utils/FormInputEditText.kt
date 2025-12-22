package com.flipsidegroup.active10.utils

import android.content.Context
import android.content.res.TypedArray
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.flipsidegroup.active10.R

open class FormsInputEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val input: FormsInput get() = findViewById(R.id.input)
    private val inputEdit: InputEditText get() = findViewById(R.id.input_edit)
    private val label: TextView get() = findViewById(R.id.label)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        inputEdit.setTextPadding(16f, 12f, 16f, 12f)
    }

    init {
        inflate()
        processAttributes(attrs)
    }

    open fun inflate() {
        LayoutInflater.from(context).inflate(R.layout.item_input_layout, this, true)
    }

    fun setLabel(text: String?) {
        label.text = text
    }

    fun setPlaceholder(text: String?) {
        inputEdit.hint = text
    }

    fun getFormInput(): InputEditText {
        return this.inputEdit
    }

    fun setAccessibilityLabel(label: String?) {
        label?.let {
            this.inputEdit.contentDescription = "Text field, $it)"
        }
    }

    fun setMaxLines(maxLines: Int) {
        inputEdit.maxLines = maxLines
    }

    fun setMaxCharacters(maxCharacters: Int) {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(maxCharacters)
        inputEdit.filters = filterArray
    }

    fun setTextSize(@DimenRes textSizeStringRes: Int) {
        inputEdit.textSize = resources.getDimension(textSizeStringRes)
    }

    fun setInputType(inputType: Int) {
        inputEdit.inputType = inputType
    }

    fun clear() {
        inputEdit.text?.clear()
    }

    private fun processAttributes(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.FormInputEditText, 0, 0)

            processLabelAttribute(typedArray)
            processValueAttribute(typedArray)
            processPlaceholderAttribute(typedArray)
            processCaptionAttribute(typedArray)
            processErrorAttribute(typedArray)
            processDisabledAttribute(typedArray)
            processImeOptionsAttribute(typedArray)
            processInputTypeAttribute(typedArray)
            processBackgroundAttribute(typedArray)
            processMaxCharactersAttribute(typedArray)
            processContentDescriptionAttribute(typedArray)
            typedArray.recycle()
        }
    }

    private fun processDisabledAttribute(typedArray: TypedArray) {
        if (typedArray.getBoolean(R.styleable.FormInputEditText_disabled, false)) {
            inputEdit.isEnabled = false
        }
    }

    private fun processValueAttribute(typedArray: TypedArray) {
        inputEdit.setText(typedArray.getString(R.styleable.FormInputEditText_text))
    }

    private fun processLabelAttribute(typedArray: TypedArray) {
        label.text = typedArray.getString(R.styleable.FormInputEditText_label)
    }

    private fun processPlaceholderAttribute(typedArray: TypedArray) {
        typedArray.getString(R.styleable.FormInputEditText_placeholder)?.let {
            inputEdit.hint = it
        }
    }

    private fun processCaptionAttribute(typedArray: TypedArray) {
        input.isHelperTextEnabled = false
        typedArray.getString(R.styleable.FormInputEditText_caption)?.let {
            input.helperText = it
            input.isHelperTextEnabled = it.isNotEmpty()
        }
    }

    private fun processErrorAttribute(typedArray: TypedArray) {
        input.error = null
        typedArray.getString(R.styleable.FormInputEditText_error)?.let {
            input.error = it
        }
    }

    private fun processImeOptionsAttribute(typedArray: TypedArray) {
        typedArray.getInt(R.styleable.FormInputEditText_android_imeOptions, 0).let {
            inputEdit.imeOptions = EditorInfo.IME_ACTION_DONE or EditorInfo.IME_FLAG_NO_EXTRACT_UI
        }
    }

    private fun processInputTypeAttribute(typedArray: TypedArray) {
        typedArray.getInt(R.styleable.FormInputEditText_android_inputType, 0).let {
            inputEdit.inputType = typedArray.getInt(it, EditorInfo.TYPE_TEXT_VARIATION_NORMAL)
        }
    }

    private fun processBackgroundAttribute(typedArray: TypedArray) {
        typedArray.getDrawable(R.styleable.FormInputEditText_formInputBackground).let {
            if (it != null) {
                inputEdit.background = it
            }
        }
    }

    private fun processContentDescriptionAttribute(typedArray: TypedArray) {
        typedArray.getString(R.styleable.FormInputEditText_editTextContentDescription).let {
            if (it != null) {
                inputEdit.contentDescription = it
            }
        }
    }

    private fun processMaxCharactersAttribute(typedArray: TypedArray) {
        typedArray.getInt(R.styleable.FormInputEditText_maxCharacters, 0).let {
            val maxCharacters = it
            if (maxCharacters > 0) {
                setMaxCharacters(maxCharacters)
            }
        }
    }

    fun getText(): String {
        return inputEdit.text.toString()
    }

    fun setText(text: String) {
        inputEdit.setText(text)
    }

    fun addTextChangedListener(watcher: TextWatcher) {
        inputEdit.addTextChangedListener(watcher)
    }

    fun setError(error: String?) {
        input.error = error
    }
}
