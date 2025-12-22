package com.flipsidegroup.active10.presentation.discover.presenter

import com.flipsidegroup.active10.data.models.api.DiscoverCategory
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.discover.view.DiscoverView


interface DiscoverPresenter : LifecycleAwarePresenter<DiscoverView> {

    fun loadData()

    fun selectTab(tab: ScreenContent)

    fun enterSearchText(text: String)

}