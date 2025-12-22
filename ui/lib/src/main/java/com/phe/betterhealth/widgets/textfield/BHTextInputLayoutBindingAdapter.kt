package com.phe.betterhealth.widgets.textfield

import androidx.databinding.BindingAdapter

@BindingAdapter("errorMessage")
fun BHTextInputLayout.setErrorStringRes(error: Int?) {
    errorMessage = if (error != null) context.getString(error) else null
}

@BindingAdapter("errorMessage")
fun BHTextInputLayout.setErrorString(error: String?) {
    errorMessage = error
}
