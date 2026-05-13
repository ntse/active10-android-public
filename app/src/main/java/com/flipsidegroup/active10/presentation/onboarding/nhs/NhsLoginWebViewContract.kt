package com.flipsidegroup.active10.presentation.onboarding.nhs

import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface NhsLoginWebViewContract {

    interface View : BaseView {
        fun loginSuccess()
        fun loginFailure()
        fun showNoConsentScreen()
    }

    interface Presenter : LifecycleAwarePresenter<View> {
        fun completeLogin(accessToken: String)
    }

}
