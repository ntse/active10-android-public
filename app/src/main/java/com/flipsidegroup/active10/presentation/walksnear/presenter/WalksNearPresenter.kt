package com.flipsidegroup.active10.presentation.walksnear.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.walksnear.view.WalksNearView

interface WalksNearPresenter : LifecycleAwarePresenter<WalksNearView> {
    fun loadContent()

}