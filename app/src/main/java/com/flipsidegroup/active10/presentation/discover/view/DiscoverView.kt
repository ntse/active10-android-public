package com.flipsidegroup.active10.presentation.discover.view

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.discover.adapter.GeneralScreen



interface DiscoverView : BaseView {

    fun showData(data: List<GeneralScreen>, showEmptyState: Boolean)

    fun showTabs(tabs: List<ScreenContent>)

}