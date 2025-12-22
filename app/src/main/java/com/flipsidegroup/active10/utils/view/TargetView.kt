package com.flipsidegroup.active10.utils.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.utils.UIUtils

class TargetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    var targetId: Int = 0
        set(value) {
            field = value
            updateViews()
        }

    var selectedTargetId: Int = 0
        set(value) {
            field = value
            updateViews()
        }

    private val checked: ImageView get() = findViewById(R.id.checked)

    private val checkedBackground: View get() = findViewById(R.id.checkedBackground)

    private val targetIV: ImageView get() = findViewById(R.id.targetIV)

    private fun updateViews() {
        val isSelected = selectedTargetId >= targetId
        targetIV.setImageResource(if (isSelected) R.drawable.ic_trophy_on else R.drawable.ic_trophy_off)
        checked.isVisible = isSelected
        checkedBackground.isVisible = isSelected
        updateContentDescription()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.item_target, this, true)
        isClickable = true
    }

}

private fun TargetView.updateContentDescription() {
    val targetString = UIUtils.getQuantityString(R.plurals.target_plural, targetId)
    contentDescription = if (targetId == selectedTargetId) {
        context.getString(R.string.accessibility_selected, targetString)
    } else {
        context.getString(R.string.accessibility_unselected, targetString)
    }
}
