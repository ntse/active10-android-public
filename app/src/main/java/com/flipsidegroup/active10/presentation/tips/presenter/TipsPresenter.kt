package com.flipsidegroup.active10.presentation.tips.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.tips.view.TipsView


interface TipsPresenter : LifecycleAwarePresenter<TipsView> {

    fun getTips()
}