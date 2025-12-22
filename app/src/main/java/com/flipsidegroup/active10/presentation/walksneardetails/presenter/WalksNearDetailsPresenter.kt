package com.flipsidegroup.active10.presentation.walksneardetails.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.walksneardetails.view.WalksNearDetailsView

interface WalksNearDetailsPresenter : LifecycleAwarePresenter<WalksNearDetailsView> {

    fun loadWalk(id: String)

}