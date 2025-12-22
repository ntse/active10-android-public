package com.flipsidegroup.active10.presentation.goals.presenter

import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.goals.view.GoalsFragmentView
import javax.inject.Inject


class GoalsFragmentPresenterImpl @Inject constructor(
    private val localRepository: LocalRepository,
    private var settingsUtils: SettingsUtils
) : BasePresenter<GoalsFragmentView>(),
    GoalsFragmentPresenter {

    override fun getGoals() {
        localRepository.getGoalsContent(object : AppDatabase.OnDataLoadedListener<List<Goal>> {
            override fun onDataLoaded(data: List<Goal>) {
                val goals = (settingsUtils.getSettingsHolder().goalsList ?: emptyList()).toMutableList()
                data.forEach { apiGoal ->
                    val existing = goals.find { it.goalId == apiGoal.goalId && !it.isCustomGoal }
                    if (existing != null) {
                        // Update existing goal with CMS data
                        existing.goal = apiGoal.goal
                    } else {
                        goals.add(apiGoal)
                    }
                }
                goals.removeAll { goal ->
                    !goal.isCustomGoal &&
                    data.none { it.goalId == goal.goalId && it.goal == goal.goal }
                }
                val sortedGoals = goals.sortedWith(compareBy({ it.goalId == -1 }, { it.goalId }))
                view?.onGoalsReceived(sortedGoals)
            }
        })
    }
}