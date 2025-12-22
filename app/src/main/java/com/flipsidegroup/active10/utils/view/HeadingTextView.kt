package com.flipsidegroup.active10.utils.view

import android.content.Context
import android.util.AttributeSet
import com.flipsidegroup.active10.utils.setHeading

class HeadingTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): androidx.appcompat.widget.AppCompatTextView(context, attrs) {


    init {
        this.setHeading()
    }

}
