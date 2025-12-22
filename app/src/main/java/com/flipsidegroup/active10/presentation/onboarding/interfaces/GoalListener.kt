package com.flipsidegroup.active10.presentation.onboarding.interfaces

import com.flipsidegroup.active10.data.models.Goal


interface GoalListener {

    fun onGoalChangeListener(goalsList: List<Goal>)
}