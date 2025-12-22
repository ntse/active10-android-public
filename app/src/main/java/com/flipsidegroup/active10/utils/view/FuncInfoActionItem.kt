package com.flipsidegroup.active10.utils.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import com.flipsidegroup.active10.databinding.ItemFuncInfoActionBinding

class FuncInfoActionItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CardView(context, attrs) {

    val binding: ItemFuncInfoActionBinding

    init {
        binding = ItemFuncInfoActionBinding.inflate(LayoutInflater.from(context), this, true)
        ViewCompat.setAccessibilityHeading(binding.title, true)
    }
}