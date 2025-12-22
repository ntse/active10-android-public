package com.flipsidegroup.active10.utils.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.flipsidegroup.active10.databinding.TooltipWidgetBinding

class TooltipWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    val binding: TooltipWidgetBinding

    init {
        binding = TooltipWidgetBinding.inflate(LayoutInflater.from(context), this, true)
    }
}