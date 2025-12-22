package com.flipsidegroup.active10.presentation.common.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible

abstract class ToolbarActivity: AppCompatActivity() {

    protected fun setUpToolbar(toolbar: Toolbar, showBack: Boolean = false, title: String? = null) {
        toolbar.isVisible = true
        ViewCompat.setAccessibilityHeading(toolbar, true)
        setSupportActionBar(toolbar)
        title?.let { supportActionBar?.title = it }
        supportActionBar?.setDisplayHomeAsUpEnabled(showBack)
    }
}