package com.flipsidegroup.active10.presentation.legals.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.legals.view.LegalsView

interface LegalsPresenter : LifecycleAwarePresenter<LegalsView> {

    fun getLegalRules(legalScreenType: String)
}