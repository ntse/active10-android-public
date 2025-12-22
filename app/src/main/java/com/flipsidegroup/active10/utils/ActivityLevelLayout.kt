package com.flipsidegroup.active10.utils

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.R

class ActivityLevelLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    init {
        inflate(context, R.layout.layout_activity_level, this)
        ViewCompat.setAccessibilityHeading(findViewById(R.id.titleTV), true)
    }
}