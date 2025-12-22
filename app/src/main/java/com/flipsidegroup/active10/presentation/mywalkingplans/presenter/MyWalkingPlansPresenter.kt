package com.flipsidegroup.active10.presentation.mywalkingplans.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.mywalkingplans.view.MyWalkingPlansView

interface MyWalkingPlansPresenter : LifecycleAwarePresenter<MyWalkingPlansView> {
    fun loadContent()

}