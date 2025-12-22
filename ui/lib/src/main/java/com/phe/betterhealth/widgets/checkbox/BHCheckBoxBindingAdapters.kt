package com.phe.betterhealth.widgets.checkbox

import androidx.databinding.BindingAdapter

@BindingAdapter("errorMessage")
fun BHCheckBoxLayout.setErrorStringRes(error: Int?) {
    errorMessage = if (error != null) context.getString(error) else null
}

@BindingAdapter("errorMessage")
fun BHCheckBoxLayout.setErrorString(error: String?) {
    errorMessage = error
}
