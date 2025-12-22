package com.flipsidegroup.active10.presentation.dialogs

import androidx.annotation.StringRes

data class DialogButtonModel(
    @StringRes val text: Int,
    val onClick: () -> Unit = {}
)