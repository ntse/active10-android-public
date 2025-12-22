package com.flipsidegroup.active10.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.lifecycle.MutableLiveData
import timber.log.Timber

class BiometricPromptManager(
    private val activity: AppCompatActivity
) {
    val result = MutableLiveData<BiometricResult>()

    init {
        observeBiometricPromptResult()
    }

    fun showBiometricPrompt() {
        val manager = BiometricManager.from(activity)
        val authenticators = BIOMETRIC_WEAK or DEVICE_CREDENTIAL

        when (val res = manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> Unit
            else -> {
                result.value = (BiometricResult.CanAuthenticateError(res))
                return
            }
        }

        val promptInfo = PromptInfo.Builder()
            .setTitle("Log in to Active10")
            .setDescription("Use your biometric credential")
            .setAllowedAuthenticators(authenticators)

        val prompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode == BiometricPrompt.ERROR_CANCELED) return
                    result.value = (BiometricResult.AuthenticationError(errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    this@BiometricPromptManager.result.value =
                        (BiometricResult.AuthenticationSuccess)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    result.value = (BiometricResult.AuthenticationFailed)
                }
            }
        )

        prompt.authenticate(promptInfo.build())
    }

    private fun observeBiometricPromptResult() {
        result.observe(activity) { authResult ->
            when (authResult) {
                is BiometricResult.CanAuthenticateError -> {
                    Timber.d("CanAuthenticate error: ${authResult.error}")
                }

                is BiometricResult.AuthenticationError -> {
                    Timber.d("Biometric error: ${authResult.error}")
                }

                BiometricResult.AuthenticationFailed -> {
                    Timber.d("Authentication failed")
                }

                BiometricResult.AuthenticationSuccess -> {
                    Timber.d("Authentication success")
                }
            }
        }
    }

    sealed interface BiometricResult {
        data class CanAuthenticateError(val error: Int) : BiometricResult
        data class AuthenticationError(val error: String) : BiometricResult
        data object AuthenticationFailed : BiometricResult
        data object AuthenticationSuccess : BiometricResult
    }
}