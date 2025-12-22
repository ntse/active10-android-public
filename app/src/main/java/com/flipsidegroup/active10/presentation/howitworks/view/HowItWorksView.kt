package com.flipsidegroup.active10.presentation.howitworks.view

import com.flipsidegroup.active10.data.HowItWorks
import com.flipsidegroup.active10.presentation.common.view.BaseView



interface HowItWorksView : BaseView {

    fun onHowItWorksListReceived(list: List<HowItWorks>)
}