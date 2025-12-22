package com.flipsidegroup.active10.presentation.authentication.systemAuth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import com.flipsidegroup.active10.databinding.ActivitySystemAuthSignInBinding
import com.flipsidegroup.active10.presentation.authentication.unlockSetup.unlockSetupSuccessIntent
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.utils.BiometricPromptManager
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import timber.log.Timber

fun Context.systemAuthSignInFlowIntent(flowType: FlowType): Intent {
    return Intent(this, SystemAuthSignInActivity::class.java).apply {
        putExtra(Constants.FLOW_TYPE, flowType)
    }
}

class SystemAuthSignInActivity : BasePublicActivity<BaseView>() {

    override fun getPresenter() = null

    private var binding: ActivitySystemAuthSignInBinding by lifecycleAwareVariable()
    private val biometricAuth = BiometricPromptManager(this)
    private lateinit var flowType: FlowType

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivitySystemAuthSignInBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
            root.setBottomPaddingToBottomInset()
        }
        onBackPressedDispatcher.addCallback(this) {}
        flowType = intent.serializable<FlowType>(Constants.FLOW_TYPE)

        observeBiometricPromptResult()
    }

    private fun observeBiometricPromptResult() {
        biometricAuth.result.observe(this) { authResult ->
            when(authResult) {
                is BiometricPromptManager.BiometricResult.CanAuthenticateError -> {
                    Timber.d("Initial canAuthentication error: ${authResult.error}")
                    finish()
                }
                is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                    Timber.d("Initial authentication error: ${authResult.error}")
                    finish()
                }
                BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                    Timber.d("Initial authentication failed")
                }
                BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                    Timber.d("Initial authentication succeeded")
                    checkAuthManager.invokeAuthScreen.value = false
                    preferenceRepository.isBiometricAllowed = true
                    preferenceRepository.authPinCode = null
                    startActivity(unlockSetupSuccessIntent(flowType))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        biometricAuth.showBiometricPrompt()
    }
}