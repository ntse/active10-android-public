package com.flipsidegroup.active10.presentation.licenses

import com.flipsidegroup.active10.data.AcknowledgementLicense
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface LicensesContract {
    interface View: BaseView {
        fun showLicenses(licenses: List<AcknowledgementLicense>)
        fun showError()
    }

    interface Presenter: LifecycleAwarePresenter<View> {
        fun getLicenses()
    }
}