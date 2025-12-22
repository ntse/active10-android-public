package com.flipsidegroup.active10.presentation.activitylevel

import com.flipsidegroup.active10.data.ActivityLevelEnum
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface ActivityLevelPresenter : LifecycleAwarePresenter<ActivityLevelView> {

    fun loadContent()

    fun loadData()

    fun saveData(activityLevel: ActivityLevelEnum?)
}