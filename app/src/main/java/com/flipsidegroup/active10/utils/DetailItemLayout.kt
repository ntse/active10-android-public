package com.flipsidegroup.active10.utils

import android.content.Context
import android.content.res.TypedArray
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.LayoutDetailItemBinding


class DetailItemLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val binding: LayoutDetailItemBinding =
        LayoutDetailItemBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        ViewCompat.setAccessibilityHeading(binding.titleTV, true)
        processAttributes(attrs)
    }

    private fun processAttributes(attrs: AttributeSet?) {
        attrs?.let {
            context.withStyledAttributes(it, R.styleable.DetailItemLayout, 0, 0) {
                processTitleAttribute(this)
                processLabelAttribute(this)
                processValueAttribute(this)
                processOtherAttributes(this)
            }
        }
    }

    private fun processTitleAttribute(typedArray: TypedArray) {
        setTitle(typedArray.getString(R.styleable.DetailItemLayout_detailTitle))
    }

    private fun processLabelAttribute(typedArray: TypedArray) {
        setLabel(typedArray.getString(R.styleable.DetailItemLayout_detailLabel))
    }

    private fun processValueAttribute(typedArray: TypedArray) {
        setValue(typedArray.getString(R.styleable.DetailItemLayout_detailValue))
    }

    private fun processOtherAttributes(typedArray: TypedArray) {
        setIsEditable(typedArray.getBoolean(R.styleable.DetailItemLayout_isEditable, true))
    }

    fun setTitle(title: String? = "") {
        binding.titleTV.setTextHtml(title)
    }

    fun setLabel(label: String? = "") {
        binding.labelTV.setTextHtml(label)
    }

    fun setValue(value: String? = "") {
        binding.valueET.setText(value)
    }

    private fun setIsEditable(isEnabled: Boolean) {
        val typeface = ResourcesCompat.getFont(context, if (isEnabled) R.font.roboto_medium else R.font.roboto_regular)
        val color = ContextCompat.getColor(context, if (isEnabled) R.color.input_text_title else R.color.light_grey_two)

        binding.inputET.isErrorEnabled = isEnabled
        binding.valueET.isEnabled = isEnabled
        binding.valueET.setTypeface(typeface)
        binding.valueET.setTextColor(color)
    }

    fun setEmail(value: String? = "") {
        binding.valueET.setText(
            value?.let {
                val atIndex = it.indexOf('@')
                if (atIndex == -1) it else "${it[0]}${"*".repeat(atIndex - 1)}${it.substring(atIndex)}"
            } ?: ""
        )
    }

    fun setPostcode(value: String? = "") {
        binding.valueET.setText(
            value?.let {
                if (it.length <= 2) "*".repeat(it.length)
                else it.dropLast(2) + "**"
            } ?: ""
        )
    }

    fun setMaxCharacters(maxCharacters: Int) {
        val filterArray = arrayOfNulls<InputFilter>(1)
        filterArray[0] = InputFilter.LengthFilter(maxCharacters)
        binding.valueET.filters = filterArray
    }
}