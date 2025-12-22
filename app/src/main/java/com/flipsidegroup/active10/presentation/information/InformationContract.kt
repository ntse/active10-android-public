package com.flipsidegroup.active10.presentation.information

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwareView

interface InformationContract {

    interface View: LifecycleAwareView {
        fun configureView(
            title: String,
            description: String,
            imageUrl: String?,
            primaryButtonText: String?,
            secondaryButtonText: String?,
            useDarkBackground: Boolean = false
        )

        fun openNextInformationScreen(screenId: String)

        fun close()

        fun openPlayStore(packageName: String)
    }

    interface Presenter: LifecycleAwarePresenter<View> {
        fun loadContent(screenId: String)
        fun onPrimaryButtonClicked()
        fun onSecondaryButtonClicked()
    }
}