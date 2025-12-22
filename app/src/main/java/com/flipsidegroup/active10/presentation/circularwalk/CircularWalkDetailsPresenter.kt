package com.flipsidegroup.active10.presentation.circularwalk

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface CircularWalkDetailsPresenter : LifecycleAwarePresenter<CircularWalkDetailsView> {
    fun loadContent()
}