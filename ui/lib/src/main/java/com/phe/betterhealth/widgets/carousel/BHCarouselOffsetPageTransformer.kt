package com.phe.betterhealth.widgets.carousel

import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.LAYOUT_DIRECTION_RTL
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class BHCarouselOffsetPageTransformer(
    @Px private val offsetPx: Float,
    @Px private val pageMarginPx: Float,
) : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        val viewPager = requireViewPager(page)
        if (viewPager.orientation == ViewPager2.ORIENTATION_VERTICAL) {
            return
        }

        val itemCount = viewPager.adapter?.itemCount ?: 3
        var offset = position * -(2 * offsetPx + pageMarginPx)
        val totalMargin = offsetPx + pageMarginPx

        val mStart: Float
        val mEnd: Float

        when (viewPager.currentItem) {
            0 -> {
                mStart = 0f
                mEnd = totalMargin + totalMargin
                offset += +pageMarginPx
            }
            itemCount - 1 -> {
                mStart = totalMargin + totalMargin - pageMarginPx
                mEnd = pageMarginPx
            }
            else -> {
                mStart = totalMargin
                mEnd = totalMargin
            }
        }

        viewPager.post {
            page.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginStart = mStart.toInt()
                marginEnd = mEnd.toInt()
            }

            page.translationX =
                if (ViewCompat.getLayoutDirection(viewPager) == LAYOUT_DIRECTION_RTL) {
                    -offset
                } else {
                    offset
                }
        }
    }

    private fun requireViewPager(page: View): ViewPager2 {
        val parent = page.parent
        val parentParent = parent.parent
        if (parent is RecyclerView && parentParent is ViewPager2) {
            return parentParent
        }
        throw IllegalStateException(
            "Expected the page view to be managed by a ViewPager2 instance."
        )
    }
}
