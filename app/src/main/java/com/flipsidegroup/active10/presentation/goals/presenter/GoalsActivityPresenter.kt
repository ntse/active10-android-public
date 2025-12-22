package com.flipsidegroup.active10.presentation.goals.presenter

import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.goals.view.GoalsActivityView


interface GoalsActivityPresenter : LifecycleAwarePresenter<GoalsActivityView> {

    fun saveGoals(goals: List<Goal>)
}