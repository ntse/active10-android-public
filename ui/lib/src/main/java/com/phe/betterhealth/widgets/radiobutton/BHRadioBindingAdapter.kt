package com.phe.betterhealth.widgets.radiobutton

import androidx.databinding.BindingAdapter

@BindingAdapter("errorMessage")
fun BHRadioGroup.setErrorStringRes(error: Int?) {
    errorMessage = if (error != null) context.getString(error) else null
}

@BindingAdapter("errorMessage")
fun BHRadioGroup.setErrorString(error: String?) {
    errorMessage = error
}
