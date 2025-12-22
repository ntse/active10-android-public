package com.flipsidegroup.active10.utils.view

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.children
import com.google.android.material.appbar.MaterialToolbar
import timber.log.Timber

class HeadingToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
): MaterialToolbar(context, attrs) {

    override fun setTitle(resId: Int) {
        super.setTitle(resId)
        if (title.isNotBlank()) {
            setTitleAsHeading()
        } else {
            setTitleUnimportantForAccessibility()
        }
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        if (title?.isNotBlank() == true) {
            setTitleAsHeading()
        } else {
            setTitleUnimportantForAccessibility()
        }
    }

    private fun setTitleAsHeading() {
        children.firstOrNull { it is TextView }?.let {
            ViewCompat.setAccessibilityHeading(it, true)
        }
    }

    private fun setTitleUnimportantForAccessibility() {
        children.firstOrNull { it is TextView }?.let {
            ViewCompat.setImportantForAccessibility(it, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO)
        }
    }

}
