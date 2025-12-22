package com.flipsidegroup.active10.presentation.signIn

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface SignInPresenter : LifecycleAwarePresenter<SignInView> {

    fun loadData()

    fun loadTripDialogContent()

    fun logout()

}