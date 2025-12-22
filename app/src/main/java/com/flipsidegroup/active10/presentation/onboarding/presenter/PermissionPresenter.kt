package com.flipsidegroup.active10.presentation.onboarding.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.onboarding.view.PermissionView


interface PermissionPresenter : LifecycleAwarePresenter<PermissionView> {

    fun sendGoals()

    fun getOnboarding()
}