package com.flipsidegroup.active10.presentation.progressBar

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface ProgressBarPresenter : LifecycleAwarePresenter<ProgressBarView> {

    fun loadContent()

    fun startSynchronization()

}