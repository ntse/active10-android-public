package com.flipsidegroup.active10.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.widget.EditText
import com.flipsidegroup.active10.presentation.goals.utils.ProfanityFilter


@SuppressLint("AppCompatCustomView")
class ProfanityFreeEditText : EditText, TextWatcher {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var profanityFilter: ProfanityFilter? = null

    internal var textChanged: ((hasBadWords: Boolean, charCount: Int) -> (Unit))? = null

    init {
        addTextChangedListener(this)
    }

    override fun afterTextChanged(s: Editable?) {
        s ?: return

        profanityFilter?.filter(s) { badWords, containsBadWords ->
            textChanged?.invoke(containsBadWords, s.length)
            clearExistingSpans(s)

            if (containsBadWords) {
                badWords.forEach { badWord ->
                    s.setSpan(ForegroundColorSpan(Color.RED), badWord.major, badWord.minor, 0)
                }
            }
        }
    }

    private fun clearExistingSpans(s: Editable) {
        val spansToRemovedListener = s.getSpans(0, s.length, ForegroundColorSpan::class.java)
        for (i in 0 until spansToRemovedListener.size) {
            s.removeSpan(spansToRemovedListener[i])
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
    }

    fun setBadWordsList(badWordsList: List<String>) {
        profanityFilter = ProfanityFilter(badWordsList)
    }
}