package com.flipsidegroup.active10.data

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.flipsidegroup.active10.R


enum class MigratingUserEnum(
    val toolbarVisibility: Boolean,
    @ColorRes val backgroundColor: Int = 0,
    @StringRes val title: Int = 0,
    val highlightedWord: Int = 0,
    @StringRes val subtitle: Int = 0,
    @StringRes val buttonCTA: Int = 0,
) {
    MIGRATING_INTRO_1(
        false,
        android.R.color.white,
        R.string.migrating_active10_title,
        R.string.migrating_active10_highlight,
        R.string.migrating_active10_description,
        R.string.button_continue,
    ),
    MIGRATING_INTRO_2(
        true,
        android.R.color.white,
        R.string.intro_every_minute_counts,
        R.string.minute_highlight,
        R.string.migrating_intro_every_minute_counts,
        R.string.button_continue,
    ),
    MIGRATING_INTRO_3(
        true,
        android.R.color.white,
        R.string.intro_aim,
        R.string.aim_highlight,
        R.string.migrating_intro_aim_description,
        R.string.button_lets_get_walking,
    ),
    MIGRATING_INTRO_4(
        toolbarVisibility = true,
        backgroundColor = R.color.light_gray,
        buttonCTA = R.string.button_continue,
    ),
}