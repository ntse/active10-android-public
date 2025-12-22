package com.flipsidegroup.active10.presentation.goals.view

import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.presentation.common.view.BaseView


interface GoalsActivityView : BaseView {

    fun onGoalsReceived(goals: List<Goal>)

    fun onSendGoalsCompleted()

    fun onSendGoalsError()
}