package com.flipsidegroup.active10.presentation.goals.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.goals.view.GoalsFragmentView


interface GoalsFragmentPresenter : LifecycleAwarePresenter<GoalsFragmentView> {

    fun getGoals()
}