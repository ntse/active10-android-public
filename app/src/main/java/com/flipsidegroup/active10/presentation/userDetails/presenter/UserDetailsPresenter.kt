package com.flipsidegroup.active10.presentation.userDetails.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.userDetails.view.UserDetailsView

interface UserDetailsPresenter: LifecycleAwarePresenter<UserDetailsView> {

    fun getAboutYouDescription()

}