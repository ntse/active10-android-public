package com.flipsidegroup.active10.presentation.home.view

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwareView
import com.github.mikephil.charting.components.Description

interface DeepLinkView : LifecycleAwareView {
    fun onScreenLoaded(
        title: String,
        iconUrl: String?,
        description: String,
        firstButtonTitle: String,
        secondButtonTitle: String
    )
}