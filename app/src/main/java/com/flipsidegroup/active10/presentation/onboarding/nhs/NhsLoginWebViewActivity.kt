package com.flipsidegroup.active10.presentation.onboarding.nhs

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.databinding.ActivityNhsLoginWebViewBinding
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.onboarding.nhs.sure.noConsentSureIntent
import com.flipsidegroup.active10.presentation.progressBar.getProgressBarIntent
import com.flipsidegroup.active10.utils.Constants.FLOW_TYPE
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import timber.log.Timber
import javax.inject.Inject

fun Context.NhsLoginIntent(flowType: FlowType): Intent {
    return Intent(this, NhsLoginWebViewActivity::class.java).apply {
        putExtra(FLOW_TYPE, flowType)
    }
}

class NhsLoginWebViewActivity : BasePublicActivity<NhsLoginWebViewContract.View>(), NhsLoginWebViewContract.View {

    @Inject
    internal lateinit var presenter: NhsLoginWebViewPresenter
    override fun getPresenter(): LifecycleAwarePresenter<NhsLoginWebViewContract.View> = presenter

    private var binding: ActivityNhsLoginWebViewBinding by lifecycleAwareVariable()
    private lateinit var flowType: FlowType
    private lateinit var authService: AuthorizationService
    private var loginStarted = false

    private val authLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            handleAuthResult(activityResult.data)
        }

    private val sureActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            when (activityResult.resultCode) {
                RESULT_NHS_LOGIN_NO_CONSENT -> {
                    setResult(RESULT_NHS_LOGIN_NO_CONSENT)
                    finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityNhsLoginWebViewBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
            root.setBottomPaddingToBottomInset()
        }
        flowType = intent.serializable<FlowType>(FLOW_TYPE)
        authService = AuthorizationService(this)

        loginStarted = savedInstanceState?.getBoolean(KEY_LOGIN_STARTED) ?: false

        if (!loginStarted) {
            startOidcLogin()
        }
    }

    private fun startOidcLogin() {
        loginStarted = true
        AuthorizationServiceConfiguration.fetchFromUrl(Uri.parse(BuildConfig.OIDC_DISCOVERY_URL)) { configuration, error ->
            runOnUiThread {
                if (configuration == null || error != null) {
                    Timber.e(error, "OIDC login failed: discovery configuration could not be fetched")
                    loginFailure()
                    return@runOnUiThread
                }

                val authRequest = AuthorizationRequest.Builder(
                    configuration,
                    CLIENT_ID,
                    ResponseTypeValues.CODE,
                    Uri.parse(REDIRECT_URI)
                )
                    .setScopes("openid", "profile")
                    .build()

                authLauncher.launch(authService.getAuthorizationRequestIntent(authRequest))
            }
        }
    }

    private fun handleAuthResult(data: Intent?) {
        val authResponse = data?.let { AuthorizationResponse.fromIntent(it) }
        val authException = data?.let { AuthorizationException.fromIntent(it) }

        if (authResponse == null) {
            if (authException?.error == ACCESS_DENIED) {
                showNoConsentScreen()
                return
            }
            Timber.e(authException, "OIDC login failed: authorization response missing")
            loginFailure()
            return
        }

        if (!hasValidState(authResponse)) {
            loginFailure()
            return
        }

        authService.performTokenRequest(authResponse.createTokenExchangeRequest()) { tokenResponse, tokenException ->
            runOnUiThread {
                if (tokenResponse == null) {
                    Timber.e(tokenException, "OIDC login failed: token exchange failed")
                    loginFailure()
                    return@runOnUiThread
                }

                val nonce = authResponse.request.nonce
                if (nonce.isNullOrBlank() || !OidcIdTokenValidator.hasExpectedNonce(tokenResponse.idToken, nonce)) {
                    loginFailure()
                    return@runOnUiThread
                }

                val accessToken = tokenResponse.accessToken
                if (accessToken.isNullOrBlank()) {
                    Timber.w("OIDC login failed: access token missing")
                    loginFailure()
                    return@runOnUiThread
                }

                presenter.completeLogin(accessToken)
            }
        }
    }

    private fun hasValidState(authResponse: AuthorizationResponse): Boolean {
        val state = authResponse.request.state
        if (state.isNullOrBlank()) {
            Timber.w("OIDC login failed: expected state missing")
            return false
        }
        if (authResponse.state != state) {
            Timber.w("OIDC login failed: returned state did not match")
            return false
        }
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_LOGIN_STARTED, loginStarted)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        if (::authService.isInitialized) {
            authService.dispose()
        }
        super.onDestroy()
    }

    override fun loginSuccess() {
        preferenceRepository.continuousUsageNhsTime = System.currentTimeMillis()

        when(flowType) {
            FlowType.ONBOARDING -> {
                startActivity(getProgressBarIntent(flowType))
                finish()
            }
            else -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun loginFailure() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun showNoConsentScreen() {
        sureActivityLauncher.launch(noConsentSureIntent())
    }

    companion object {
        const val RESULT_NHS_LOGIN_NO_CONSENT = 100
        private const val CLIENT_ID = "active10"
        private const val REDIRECT_URI = "active10://callback"
        private const val ACCESS_DENIED = "access_denied"
        private const val KEY_LOGIN_STARTED = "OIDC_LOGIN_STARTED"
    }
}
