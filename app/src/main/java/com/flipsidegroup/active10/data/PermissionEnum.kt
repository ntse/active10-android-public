package com.flipsidegroup.active10.data

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.flipsidegroup.active10.R

enum class PermissionEnum(
    val toolbarVisibility: Boolean,
    val declineBtnVisibility: Int,
    @DrawableRes val image: Int = 0,
    @StringRes val title: Int = 0,
    @StringRes val subtitle: Int = 0,
    @StringRes val buttonCTA: Int = 0,
    val translateAnimation: Boolean = false,
    val reverseButtons: Boolean = false
) {

    NOTIFICATIONS_PERMISSION(
        toolbarVisibility = true,
        declineBtnVisibility = View.VISIBLE,
        image = R.drawable.ic_iconalert,
        title = R.string.permission_notifications,
        subtitle = R.string.permission_notifications_description,
        buttonCTA = R.string.button_allow_notifications,
        translateAnimation = false,
    ),
    GOAL_LIST(
        toolbarVisibility = true,
        declineBtnVisibility = View.VISIBLE,
        title = R.string.permission_goal_list_title,
        buttonCTA = R.string.add_custom_motivation,
        reverseButtons = true
    ),
    FITNESS_PERMISSION(
        toolbarVisibility = false,
        declineBtnVisibility = View.VISIBLE,
        image = R.drawable.ic_motionfitness,
        title = R.string.motion_fitness,
        subtitle = R.string.permission_fitness_data_description,
        buttonCTA = R.string.button_allow_motion_fitness,
    ),
    USER_DETAILS(
        toolbarVisibility = true,
        declineBtnVisibility = View.GONE,
    );

}
