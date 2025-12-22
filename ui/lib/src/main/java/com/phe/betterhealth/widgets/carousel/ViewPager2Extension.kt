package com.phe.betterhealth.widgets.carousel

import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

fun ViewPager2.setAccessibilityFocusOnPage(selectedPos: Int) {
    val recyclerView = getChildAt(0) as? RecyclerView ?: return
    for (i in 0 until recyclerView.childCount) {
        val child = recyclerView.getChildAt(i)
        val adapterPos = recyclerView.getChildAdapterPosition(child)
        if (adapterPos == selectedPos) {
            child.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            child.doOnLayout { vItem ->
                vItem.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
            }
        } else {
            child.importantForAccessibility =
                View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
        }
    }
}
