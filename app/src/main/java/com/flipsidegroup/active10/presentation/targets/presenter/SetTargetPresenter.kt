package com.flipsidegroup.active10.presentation.targets.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.targets.view.SetTargetView

interface SetTargetPresenter : LifecycleAwarePresenter<SetTargetView> {

    fun onSelectTarget(int: Int)

    fun onContinue()
}