package com.phe.betterhealth.widgets.utils

import android.content.Context
import android.view.View
import android.view.accessibility.AccessibilityManager
import androidx.core.content.getSystemService
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

fun Context.isTouchExplorationEnabled(): Boolean {
    return getSystemService<AccessibilityManager>()?.isTouchExplorationEnabled ?: false
}

fun View.initializeA11yNodeInfo(block: AccessibilityNodeInfoCompat.() -> Unit) {
    ViewCompat.setAccessibilityDelegate(this, object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityNodeInfo(
            host: View, info: AccessibilityNodeInfoCompat
        ) {
            block(info)
            super.onInitializeAccessibilityNodeInfo(host, info)
        }
    })
}
