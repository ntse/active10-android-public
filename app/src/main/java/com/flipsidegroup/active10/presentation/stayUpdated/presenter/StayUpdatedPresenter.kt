package com.flipsidegroup.active10.presentation.stayUpdated.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.stayUpdated.StayUpdatedView
import io.reactivex.Completable

interface StayUpdatedPresenter : LifecycleAwarePresenter<StayUpdatedView> {

    fun loadData()

    fun saveData(isAllowReceiveEmail: Boolean)

}