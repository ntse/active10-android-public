package com.flipsidegroup.active10.presentation.onboarding.nhs

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.utils.PkceUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class NhsLoginWebViewPresenter(
    private val loginRepository: LoginRepository,
    private val localNotificationRepository: LocalNotificationRepository,
    private val screenRepository: ScreenRepository,
) : BasePresenter<NhsLoginWebViewContract.View>(), NhsLoginWebViewContract.Presenter {

    private var codeVerifier: String? = null

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
                        val code = url.getQueryParameter("code")
                        val currentCodeVerifier = codeVerifier
                        if (code.isNullOrBlank() || currentCodeVerifier.isNullOrBlank()) {
                            Timber.w("Missing authorization code or code verifier")
                            view?.loginFailure()
                            return true
                        }
                        exchangeAuthorizationCode(code, currentCodeVerifier)
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
            codeVerifier = PkceUtils.generateCodeVerifier()
            loadUrl(
                PkceUtils.buildLoginUrl(
                    baseUrl = BuildConfig.NHS_LOGIN_URL,
                    path = LOGIN_PATH,
                    codeChallenge = PkceUtils.createCodeChallenge(codeVerifier.orEmpty()),
                )
            )
        }
    }

    private fun exchangeAuthorizationCode(code: String, codeVerifier: String) {
        loginRepository.exchangeAuthorizationCode(code, codeVerifier)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    getUserDetails()
                },
                {
                    Timber.e(it, "Failed to exchange authorization code")
                    view?.loginFailure()
                }
            ).addToDisposables()
    }

    private fun getUserDetails() {
        loginRepository.getUserDetails()
            .subscribeOn(Schedulers.io())
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
        super.bind(view)
    }

    override fun unbind() {
        super.unbind()
    }

    companion object {
        private const val LOGIN_PATH = "nhs_login/active10/app-internal-id"
        const val LOGGED_IN = "nhs_user_logged_in"
        const val NO_CONSENT = "nhs_noconsent"
    }
}
