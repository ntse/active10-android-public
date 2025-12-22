package com.phe.betterhealth.widgets.textfield

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.databinding.BindingAdapter

@BindingAdapter("onEditorEnterAction")
fun BHTextInputEditText.onEditorEnterAction(f: Function0<Unit>?) {

    if (f == null) setOnEditorActionListener(null)
    else setOnEditorActionListener { _, actionId, event ->

        val imeAction = when (actionId) {
            EditorInfo.IME_ACTION_DONE,
            EditorInfo.IME_ACTION_SEND,
            EditorInfo.IME_ACTION_GO -> true
            else -> false
        }

        val keydownEvent = event?.keyCode == KeyEvent.KEYCODE_ENTER
            && event.action == KeyEvent.ACTION_DOWN

        if (imeAction or keydownEvent)
            true.also { f() }
        else false
    }
}
