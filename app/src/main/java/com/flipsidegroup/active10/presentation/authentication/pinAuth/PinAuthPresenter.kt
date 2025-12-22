package com.flipsidegroup.active10.presentation.authentication.pinAuth

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface PinAuthPresenter : LifecycleAwarePresenter<PinAuthView> {

    fun logout()
}