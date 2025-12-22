package com.flipsidegroup.active10.utils

import androidx.core.view.doOnLayout
import androidx.core.widget.NestedScrollView

fun NestedScrollView.onScrollTo(tooltipPos: Int, callback: () -> Unit) {
    doOnLayout {
        setOnScrollChangeListener { _, _, _, _, _ ->
            callback()
        }
        smoothScrollTo(0, tooltipPos)
    }
}

