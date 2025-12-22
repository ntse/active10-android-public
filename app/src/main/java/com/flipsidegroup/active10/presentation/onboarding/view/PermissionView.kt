package com.flipsidegroup.active10.presentation.onboarding.view

import com.flipsidegroup.active10.data.Onboarding
import com.flipsidegroup.active10.presentation.common.view.BaseView


interface PermissionView : BaseView {

    fun onSendGoalsCompleted()

    fun onSendGoalsError()

    fun onOnboardingReceived(onboarding: Onboarding?)
}