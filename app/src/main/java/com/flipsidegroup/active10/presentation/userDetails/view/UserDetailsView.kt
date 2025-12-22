package com.flipsidegroup.active10.presentation.userDetails.view

import com.flipsidegroup.active10.data.Onboarding
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface UserDetailsView: BaseView {

    fun onAboutYouDescriptionReceived(onboarding: Onboarding?)
}