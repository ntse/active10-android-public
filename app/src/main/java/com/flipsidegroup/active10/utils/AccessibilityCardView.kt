package com.flipsidegroup.active10.utils

import android.content.Context
import android.util.AttributeSet
import android.view.accessibility.AccessibilityEvent
import androidx.cardview.widget.CardView


class AccessibilityCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    override fun dispatchPopulateAccessibilityEvent(event: AccessibilityEvent?): Boolean {
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
            this.requestFocus()
        }

        return super.dispatchPopulateAccessibilityEvent(event)
    }
}