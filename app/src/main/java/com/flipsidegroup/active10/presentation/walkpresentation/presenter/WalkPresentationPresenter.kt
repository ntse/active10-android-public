package com.flipsidegroup.active10.presentation.walkpresentation.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.walkpresentation.view.WalkPresentationView

interface WalkPresentationPresenter : LifecycleAwarePresenter<WalkPresentationView> {

    fun getWalkData(start: Long, end: Long)
}