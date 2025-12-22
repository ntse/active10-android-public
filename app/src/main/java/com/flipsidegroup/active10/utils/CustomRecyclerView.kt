package com.flipsidegroup.active10.utils

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.accessibility.AccessibilityNodeInfo
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class CustomRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    override fun performAccessibilityAction(action: Int, arguments: Bundle?): Boolean {
        when (action) {
            AccessibilityNodeInfo.ACTION_SCROLL_FORWARD -> {
                Timber.d("UniversalSwitch: ACTION_SCROLL_FORWARD detected")
                return handleLimitedScroll(true)
            }
            AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD -> {
                Timber.d("UniversalSwitch: ACTION_SCROLL_BACKWARD detected")
                return handleLimitedScroll(false)
            }
        }
        return super.performAccessibilityAction(action, arguments)
    }

    private fun handleLimitedScroll(scrollDown: Boolean): Boolean {
        val scrollDistance = if (scrollDown) measuredHeight else -measuredHeight

        smoothScrollBy(0, scrollDistance)
        return true
    }

}