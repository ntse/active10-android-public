package com.flipsidegroup.active10.presentation.onboarding.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.onboarding.view.TermsAndConditionsView


interface TermsAndConditionsPresenter : LifecycleAwarePresenter<TermsAndConditionsView> {

    fun getContent()

    fun getTermsAndCondLastVersion()
}