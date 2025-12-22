package com.flipsidegroup.active10.presentation.onboarding.nhs

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber

class NhsLoginWebViewPresenter(
    private val settingsUtils: SettingsUtils,
    private val loginRepository: LoginRepository,
    private val localNotificationRepository: LocalNotificationRepository,
    private val screenRepository: ScreenRepository,
) : BasePresenter<NhsLoginWebViewContract.View>(), NhsLoginWebViewContract.Presenter {


    private val jsInterface = object {
        @Suppress("unused")
        @JavascriptInterface
        fun postMessage(message: String) {
            Timber.d("NhsLoginWebViewPresenter postMessage message: $message")
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
                    val url = webResourceRequest.url
                    Timber.d("NhsLoginWebViewPresenter shouldOverrideUrlLoading url: $url")
                    if (url.authority == LOGGED_IN) {
                        val token = url.getQueryParameter("token") ?: ""
                        settingsUtils.updateSettings(SettingsDataHolder(nhsToken = token))
                        getUserDetails()
                        return true
                    } else if (url.authority == NO_CONSENT) {
                        view?.showNoConsentScreen()
                        return true
                    }
                    return false
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError
                ) {
                    Timber.d("NhsLoginWebViewPresenter onReceivedError error: $error")
                }

                @Deprecated("Deprecated for API 23 and later", ReplaceWith("viewModel.showError()"))
                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    Timber.d("NhsLoginWebViewPresenter onReceivedError error: $errorCode: $description")
                }
            }
            loadUrl("${BuildConfig.NHS_LOGIN_URL}nhs_login/active10/app-internal-id") // TODO: change to correct url, or use constant for suffix
        }
    }

    private fun getUserDetails() {
        loginRepository.getUserDetails()
            .subscribeOn(io.reactivex.schedulers.Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { userDetails ->
                    userDetails.ifPresent {
                        localNotificationRepository.cancelNoAccountUser()
                        view?.loginSuccess()
                    }
                    if (userDetails.isEmpty) {
                        view?.loginFailure()
                    }
                },
                {
                    Timber.e(it)
                    view?.loginFailure()
                }
            ).addToDisposables()
    }

    override fun bind(view: NhsLoginWebViewContract.View) {
        this.view = view
    }

    override fun unbind() {
        this.view = null
    }

    companion object {
        const val LOGGED_IN = "nhs_user_logged_in"
        const val NO_CONSENT = "nhs_noconsent"
    }
}