package com.flipsidegroup.active10.data

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.flipsidegroup.active10.R


enum class NewUserEnum(
    val toolbarVisibility: Boolean,
    @ColorRes val backgroundColor: Int = 0,
    @DrawableRes val image: Int = 0,
    @StringRes val title: Int = 0,
    val highlightedWord: Int = 0,
    @StringRes val subtitle: Int = 0,
    @StringRes val buttonCTA: Int = 0,
) {

    NEW_INTRO_1(
        false,
        android.R.color.white,
        R.drawable.ic_brisk,
        R.string.intro_what_is_brisk,
        R.string.brisk_highlight,
        R.string.intro_brisk_description,
        R.string.button_continue,
    ),
    NEW_INTRO_2(
        true,
        android.R.color.white,
        R.drawable.ic_iconminute,
        R.string.intro_every_minute_counts,
        R.string.minute_highlight,
        R.string.intro_minute_description,
        R.string.button_continue,
    ),
    NEW_INTRO_3(
        true,
        android.R.color.white,
        R.drawable.ic_icon_trophy,
        R.string.intro_aim,
        R.string.aim_highlight,
        R.string.intro_aim_description,
        R.string.button_lets_get_walking,
    ),
    NEW_INTRO_4(
        toolbarVisibility = true,
        backgroundColor = R.color.white,
        buttonCTA = R.string.button_continue,
    ),
    NEW_INTRO_5(
        toolbarVisibility = true,
        backgroundColor = R.color.light_gray,
        buttonCTA = R.string.button_continue,
    ),
    NEW_INTRO_6(
        toolbarVisibility = true,
        backgroundColor = R.color.light_gray,
        buttonCTA = R.string.button_continue,
    )
}