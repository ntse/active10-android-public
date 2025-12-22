package com.phe.betterhealth.widgets.text

import android.content.Context
import android.text.Spanned
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.withStyledAttributes
import kotlin.math.max
import kotlin.math.min

class BHEllipsizeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatTextView(context, attrs) {

    private var initialMaxLines: Int = Int.MAX_VALUE

    init {
        context.withStyledAttributes(attrs, intArrayOf(android.R.attr.maxLines)) {
            initialMaxLines = getInt(0, Int.MAX_VALUE)
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        val normalizedText = if (text is Spanned) {
            // replace all new lines with one space
            text.replace("\n+".toRegex(), " ")
        } else text
        super.setText(normalizedText, type)
        requestLayout()
    }

    override fun onPreDraw(): Boolean {
        maxLines = min(initialMaxLines, max(height / lineHeight, 1))
        return super.onPreDraw()
    }
}
