package com.flipsidegroup.active10.presentation.common

import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.utils.setHeading

open class FragmentToolbar : Fragment() {

    protected fun setUpDefaultToolbar(toolbar: Toolbar, showBack: Boolean = false, title: String? = null) {
        toolbar.isVisible = true
        ViewCompat.setAccessibilityHeading(toolbar, true)
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        title?.let { activity.supportActionBar?.title = it }
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(showBack)
    }

    protected fun setUpSupportToolbar(@StringRes title: Int) {
        requireView().findViewById<TextView>(R.id.toolbarCenterTV)?.let {
            it.text = getString(title)
            it.visibility = View.VISIBLE
            it.setHeading()
        }
    }

    protected fun setUpLeftToolbar(@StringRes left: Int, @DrawableRes icon: Int) {
        val toolbarLeftTV = requireView().findViewById<AppCompatButton>(R.id.toolbarLeftTV)
        toolbarLeftTV?.let {
            it.text = getString(left)
            it.visibility = View.VISIBLE
        }
        toolbarLeftTV?.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0)
    }

    protected fun setUpRightToolbar(@StringRes right: Int) {
        requireView().findViewById<AppCompatButton>(R.id.toolbarRightTV).let {
            it.text = getString(right)
            it.visibility = View.VISIBLE
        }
    }

    protected fun setToolbarVisibility(toolbar: Toolbar, visible: Boolean) {
        if (visible) {
            toolbar.visibility = View.VISIBLE
        } else {
            toolbar.visibility = View.GONE
        }
    }

    protected fun setBackButton() {
        val toolbarLeftTV = requireView().findViewById<AppCompatButton>(R.id.toolbarLeftTV)
        toolbarLeftTV?.setOnClickListener { activity?.onBackPressed() }
    }
}