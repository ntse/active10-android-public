package com.flipsidegroup.active10.presentation.howitworks.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.howitworks.view.HowItWorksView


interface HowItWorksPresenter : LifecycleAwarePresenter<HowItWorksView> {

    fun getHowItWorks()
}