package com.flipsidegroup.active10.utils.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.google.android.material.tabs.TabLayout

class CenteringTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : TabLayout(context, attrs, defStyle) {

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val firstTab: View = (getChildAt(0) as ViewGroup).getChildAt(0)
        val lastTab: View =
            (getChildAt(0) as ViewGroup).getChildAt((getChildAt(0) as ViewGroup).childCount - 1)
        ViewCompat.setPaddingRelative(
            getChildAt(0),
            width / 2 - firstTab.width / 2,
            0,
            width / 2 - lastTab.width / 2,
            0
        )
    }
}