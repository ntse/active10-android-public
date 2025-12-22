package com.flipsidegroup.active10.presentation.authentication.systemAuth

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface SystemAuthPresenter : LifecycleAwarePresenter<SystemAuthView> {

    fun logout()
}