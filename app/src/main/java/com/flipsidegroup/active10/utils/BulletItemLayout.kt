package com.flipsidegroup.active10.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.flipsidegroup.active10.databinding.LayoutBulletItemBinding

class BulletItemLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    val binding: LayoutBulletItemBinding

    init {
        binding = LayoutBulletItemBinding.inflate(LayoutInflater.from(context), this, true)
    }
}