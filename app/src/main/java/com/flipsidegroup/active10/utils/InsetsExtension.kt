package com.flipsidegroup.active10.utils

import android.view.View
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePaddingRelative
import com.phe.betterhealth.widgets.utils.onWindowInsetsChange

fun View.setBottomPaddingToBottomInset() {
    onWindowInsetsChange(WindowInsetsCompat.Type.navigationBars()) {
        updatePaddingRelative(bottom = it.bottom)
    }
}

fun View.setBottomPaddingToBottomInset(guideline: Guideline) {
    onWindowInsetsChange(WindowInsetsCompat.Type.systemBars()) {
        guideline.setGuidelineBegin(it.top)
        updatePaddingRelative(bottom = it.bottom)
    }
}
