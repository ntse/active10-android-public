package com.flipsidegroup.active10.presentation.tips.view

import com.flipsidegroup.active10.data.Tip
import com.flipsidegroup.active10.presentation.common.view.BaseView



interface TipsView : BaseView {

    fun onTipsListReceived(tipsList: List<Tip>)
}