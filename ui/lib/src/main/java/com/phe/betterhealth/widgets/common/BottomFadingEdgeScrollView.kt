package com.phe.betterhealth.widgets.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePaddingRelative
import com.phe.betterhealth.widgets.utils.dpToPx
import com.phe.betterhealth.widgets.utils.onWindowInsetsChange
import kotlin.math.max

class BottomFadingEdgeScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr), FadingEdgeScrollView {

    init {
        isVerticalFadingEdgeEnabled = true
        setFadingEdgeLength(50.dpToPx(context).toInt())
    }

    override fun getTopFadingEdgeStrength() = 0f

    fun applyBottomPaddingToBottomInsetOf(view: View) {
        view.onWindowInsetsChange(WindowInsetsCompat.Type.ime()) { insets ->
            val keyboardHeight = insets.bottom
            val parentHeight = (parent as View).measuredHeight
            val scrollViewHeight = measuredHeight
            val newBottomPadding = keyboardHeight - (parentHeight - scrollViewHeight)
            updatePaddingRelative(bottom = max(newBottomPadding, 0))
        }
    }

    override fun scrollToBottom(afterScroll: () -> Unit) {
        postOnAnimationDelayed({
            fullScroll(View.FOCUS_DOWN)
            afterScroll()
        }, 100)
    }

    override fun scrollToChild(child: View) {
        postOnAnimationDelayed({
            child.parent.requestChildFocus(child, child)
        }, 100)
    }
}
