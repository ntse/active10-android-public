package com.flipsidegroup.active10.presentation.nhsuserdetails.webview

import android.webkit.WebView
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface NhsUpdateWebViewContract {

    interface View : BaseView {
        fun updateSuccess()
        fun updateFailure()
    }

    interface Presenter : LifecycleAwarePresenter<View> {
        fun handleWebView(webView: WebView)
    }

}