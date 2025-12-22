package com.flipsidegroup.active10.utils

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Issue A10N-1636
 * Fatal Exception: java.lang.RuntimeException: Unable to destroy activity {uk.ac.shef.oak.pheactiveten/com.flipsidegroup.active10.presentation.home.activities.HomeActivity}: java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
 *
 * https://stackoverflow.com/a/64317413
 */
fun DialogFragment.showAllowingStateLoss(fragmentManager: FragmentManager, tag: String?) {
    fragmentManager.beginTransaction().apply {
        add(this@showAllowingStateLoss, tag)
        commitAllowingStateLoss()
    }
}