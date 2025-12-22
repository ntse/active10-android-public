package com.flipsidegroup.active10.presentation.onboarding.view

import com.flipsidegroup.active10.data.OnboardingPermission
import com.flipsidegroup.active10.data.TermsConditions
import com.flipsidegroup.active10.presentation.common.view.BaseView



interface TermsAndConditionsView : BaseView {

    fun onContentReceived(onboardingPermission: OnboardingPermission?)

    fun onTermsRulesRetrieved(terms: TermsConditions?)
}