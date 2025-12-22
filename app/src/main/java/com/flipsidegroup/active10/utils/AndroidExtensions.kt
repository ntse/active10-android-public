package com.flipsidegroup.active10.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.home.adapters.SETTINGS_SCREEN_POSITION
import java.io.Serializable

inline fun <reified T> Activity.startActivity(bundle: Bundle = Bundle()) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(bundle)
    startActivity(intent)
}

inline fun <reified T> Fragment.startActivity(bundle: Bundle = Bundle()) {
    val intent = Intent(activity, T::class.java)
    intent.putExtras(bundle)
    startActivity(intent)
}

fun AppCompatActivity.replaceFragment(
    viewId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false,
    backStackTag: String? = null
) {
    val tx = supportFragmentManager.beginTransaction().replace(viewId, fragment)
    if (addToBackStack) tx.addToBackStack(backStackTag)
    tx.commit()
}

fun View.addGlobalLayoutListenerToHideViewWhenKeyboardShown(
    buttonView: View,
    containerView: View
): ViewTreeObserver.OnGlobalLayoutListener {

    val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val heightDiff: Int = containerView.rootView.height - containerView.height

        if (heightDiff > 0.15 * containerView.rootView.height) { // keyboard height shouldn't be smaller than 15% of screen height
            // keyboard will show
            buttonView.hide()
        } else {
            buttonView.showWithDelay(100)
            // keyboard will hide
        }
    }
    viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
    return keyboardLayoutListener
}

fun View.removeGlobalLayoutListener(listener: ViewTreeObserver.OnGlobalLayoutListener) {
    viewTreeObserver.removeOnGlobalLayoutListener(listener)
}

fun View.show() {
    isVisible= true
}

fun View.visible(isVisible: Boolean = true) {
    visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

fun View.hide() {
    isVisible = false
}

fun View.showWithDelay(delay: Long) {
    handler.postDelayed({ show() }, delay)
}

fun FragmentActivity.closeKeyboard() {
    if (currentFocus != null) {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}

fun shortenLogs(sentence: String): String {
    return when (sentence) {
        "I want to feel fitter and healthier" -> "1"
        "I've been advised to get more active" -> "2"
        "I’ve been diagnosed with a health condition" -> "3"
        "I want to improve my mood" -> "4"
        "I want to make regular walks a part of my lifestyle" -> "5"
        "I'm recovering from an injury" -> "7"
        else -> ""
    }
}

inline fun <T1 : Any, T2 : Any, R : Any> doubleLet(
    first: T1?,
    second: T2?,
    action: (T1, T2) -> R?
): R? {
    return if (first != null && second != null) action(first, second) else null
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T = when {
    Build.VERSION.SDK_INT >= 33 -> getSerializableExtra(key, T::class.java)!!
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as T
}

fun Activity.startFromSettingsFragment() {
    val settingsIntent = HomeActivity.getHomeIntent(this, screenPosition = SETTINGS_SCREEN_POSITION).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    }

    startActivity(settingsIntent)
}

