package com.phe.betterhealth.widgets.utils

import android.view.View
import androidx.constraintlayout.widget.Guideline
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

inline fun View.onWindowInsetsChange(
    @WindowInsetsCompat.Type.InsetsType type: Int,
    crossinline callback: (Insets) -> Unit
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
        callback(insets.getInsets(type))
        insets
    }
}

fun Guideline.setGuidelineBeginToTopInset() {
    onWindowInsetsChange(WindowInsetsCompat.Type.statusBars()) {
        setGuidelineBegin(it.top)
    }
}
