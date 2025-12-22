package com.flipsidegroup.active10.presentation.nhsuserdetails.webview

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import timber.log.Timber

class NhsUpdateWebViewPresenter(
    private val preferenceRepository: PreferenceRepository,
    private val settingsUtils: SettingsUtils
) : NhsUpdateWebViewContract.Presenter {

    private var view: NhsUpdateWebViewContract.View? = null

    private val jsInterface = object {
        @Suppress("unused")
        @JavascriptInterface
        fun postMessage(message: String) {
            Timber.d("NhsUpdateWebViewPresenter postMessage message: $message")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun handleWebView(webView: WebView) {
        webView.run {
            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true)
            settings.javaScriptCanOpenWindowsAutomatically = true
            addJavascriptInterface(jsInterface, "appInterface")
            settings.domStorageEnabled = true
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    webView: WebView,
                    webResourceRequest: WebResourceRequest
                ): Boolean {
                    // TODO: fix when backend will be ready
                    val url = webResourceRequest.url
//                    if (url.authority == "testtest") {
//                        return true
//                    }
                    return false
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError
                ) {
                    Timber.d("NhsUpdateWebViewPresenter onReceivedError error: $error")
                }
            }
            loadUrl(BuildConfig.NHS_SETTINGS_URL)
        }
    }

    override fun bind(view: NhsUpdateWebViewContract.View) {
        this.view = view
    }

    override fun unbind() {
        this.view = null
    }

}