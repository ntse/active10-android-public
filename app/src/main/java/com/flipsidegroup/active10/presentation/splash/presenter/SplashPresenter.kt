package com.flipsidegroup.active10.presentation.splash.presenter

import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface SplashPresenter : LifecycleAwarePresenter<BaseView> {

    fun registerDevice()

    fun checkForGoalsBackwardsCompatibility()

    fun checkNhsLoginStatus()
}