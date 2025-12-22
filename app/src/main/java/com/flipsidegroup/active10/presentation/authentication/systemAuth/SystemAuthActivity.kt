package com.flipsidegroup.active10.presentation.authentication.systemAuth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isInvisible
import androidx.lifecycle.MutableLiveData
import com.flipsidegroup.active10.databinding.ActivitySystemAuthBinding
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.usecases.RedirectOnAuthFailureUseCase
import com.flipsidegroup.active10.utils.BiometricPromptManager
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.serializable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import timber.log.Timber
import javax.inject.Inject

fun Context.systemAuthIntent(): Intent {
    return Intent(this, SystemAuthActivity::class.java).apply {
        setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    }
}

class SystemAuthActivity : BasePublicActivity<SystemAuthView>(), SystemAuthView {

    @Inject
    internal lateinit var presenter: SystemAuthPresenter

    @Inject
    internal lateinit var redirectOnAuthFailureUseCase: RedirectOnAuthFailureUseCase

    override fun getPresenter(): LifecycleAwarePresenter<SystemAuthView> = presenter

    private var binding: ActivitySystemAuthBinding by lifecycleAwareVariable()
    private val stepType = MutableLiveData(ContentType.INIT)
    private val biometricAuth = BiometricPromptManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivitySystemAuthBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        savedInstanceState?.let { stepType.value = it.serializable<ContentType>(STEP_TYPE) }
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        onBackPressedDispatcher.addCallback(this) {}

        stepType.observe(this) { type ->
            when(type!!) {
                ContentType.INIT -> {
                    binding.initLayout.isInvisible = false
                    binding.failedLayout.isInvisible = true
                    observeBiometricPromptResult()
                }
//                ContentType.FAILED -> {
//                    binding.initLayout.isInvisible = true
//                    binding.failedLayout.isInvisible = false
//                    removeNhsUserDataUseCase()
//                }
            }
        }

//        binding.continueBtn.setOnClickListener {
//            redirectOnAuthFailureUseCase(context = this)
//        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(STEP_TYPE, stepType.value)
    }

    override fun onResume() {
        super.onResume()
        if (stepType.value == ContentType.INIT) {
            biometricAuth.showBiometricPrompt()
        }
    }

    override fun onSuccessLogout() {
        redirectOnAuthFailureUseCase(context = this)
    }

    private fun observeBiometricPromptResult() {
        biometricAuth.result.observe(this) { authResult ->
            when(authResult) {
                is BiometricPromptManager.BiometricResult.CanAuthenticateError -> {
                    Timber.d("Biometric error: ${authResult.error}")
                    presenter.logout()
                }
                is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                    Timber.d("Biometric error: ${authResult.error}")
                    presenter.logout()
                }
                BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                    Timber.d("Authentication failed")
                }
                BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                    Timber.d("Authentication success")
                    checkAuthManager.invokeAuthScreen.value = false
                    finish()
                }
            }
        }
    }

    private enum class ContentType {
        INIT,
//        FAILED
    }

    companion object {
        private const val STEP_TYPE = "step_type"
    }
}