package com.flipsidegroup.active10.presentation.walksneardetails.view

import com.flipsidegroup.active10.data.models.response.CuratedWalk
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface WalksNearDetailsView : BaseView {
    fun updateWalk(result: CuratedWalk?)
}