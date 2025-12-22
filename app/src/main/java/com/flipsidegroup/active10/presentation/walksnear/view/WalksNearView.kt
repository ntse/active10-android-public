package com.flipsidegroup.active10.presentation.walksnear.view

import com.flipsidegroup.active10.data.models.response.CircularWalkResponse
import com.flipsidegroup.active10.data.models.response.CuratedWalk
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.walksnear.adapters.WalksNearPart

interface WalksNearView : BaseView {
    fun showData(content: List<WalksNearPart>)
    fun navigateToCuratedWalkDetails(walk: CuratedWalk)
    fun navigateToCircularWalkDetails(walk: CircularWalkResponse)
}