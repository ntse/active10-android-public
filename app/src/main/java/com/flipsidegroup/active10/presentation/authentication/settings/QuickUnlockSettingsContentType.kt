package com.flipsidegroup.active10.presentation.authentication.settings

import androidx.annotation.StringRes
import com.flipsidegroup.active10.R

enum class QuickUnlockSettingsContentType(
    @StringRes val title: Int,
    @StringRes val description: Int,
    @StringRes val primaryBtnText: Int,
    @StringRes val secondaryBtnText: Int,
) {
    ENABLE_QUICK_UNLOCK(
        R.string.quick_unlock_enable_title,
        R.string.quick_unlock_enable_description,
        R.string.enable_biometric,
        R.string.set_up_a_pin,
    ),
    ENABLE_BIOMETRIC(
        R.string.biometric_enable_title,
        R.string.biometric_enable_description,
        R.string.enable_biometric,
        R.string.remove_quick_unlock,
    ),
    DISABLE_BIOMETRIC(
        R.string.biometric_disable_title,
        R.string.biometric_disable_description,
        R.string.disable_biometric,
        R.string.remove_quick_unlock,
    ),
}