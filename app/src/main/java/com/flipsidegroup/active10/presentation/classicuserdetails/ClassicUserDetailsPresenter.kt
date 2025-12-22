package com.flipsidegroup.active10.presentation.classicuserdetails

import com.flipsidegroup.active10.data.models.ClassicUser
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface ClassicUserDetailsPresenter : LifecycleAwarePresenter<ClassicUserDetailsView> {

    fun loadContent()

    fun loadData()

    fun saveData(user: ClassicUser)

}