package com.flipsidegroup.active10.presentation.common.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import androidx.core.widget.NestedScrollView.OnScrollChangeListener

@SuppressLint("RestrictedApi")
class NotifyNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    private var mOnEndScrollListener: (() -> Unit)? = null

    init {
        setOnScrollChangeListener(OnScrollChangeListener { _, _, _, _, _ ->
            val isScrolling = computeVerticalScrollRange() > height
                    && computeVerticalScrollExtent() + computeVerticalScrollOffset() != computeVerticalScrollRange()

            if (!isScrolling) {
                mOnEndScrollListener?.invoke()
                mOnEndScrollListener = null
            }
        })
    }

    fun smoothScrollToAndNotify(scrollPosition: Int, onEndScroll: () -> Unit) {
        mOnEndScrollListener = onEndScroll
        smoothScrollTo(0, scrollPosition)
    }
}