package com.flipsidegroup.active10.presentation.common.activities

import android.os.Bundle
import com.flipsidegroup.active10.presentation.authentication.pinAuth.pinAuthIntent
import com.flipsidegroup.active10.presentation.authentication.systemAuth.systemAuthIntent
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwareView
import com.flipsidegroup.active10.presentation.usecases.RedirectOnAuthFailureUseCase
import com.flipsidegroup.active10.utils.isBiometricAvailable
import javax.inject.Inject

abstract class BaseSecureActivity<V : LifecycleAwareView> : BaseActivity<V>() {

    @Inject
    internal lateinit var redirectOnAuthFailureUseCase: RedirectOnAuthFailureUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeCheckAuthManager()
    }

    private fun observeCheckAuthManager() {
        checkAuthManager.invokeAuthScreen.observe(this) { showAuthScreen ->
            if (!showAuthScreen || !preferenceRepository.isUserLoggedIn) return@observe

            if (!preferenceRepository.authPinCode.isNullOrBlank()) {
                startActivity(pinAuthIntent())
            } else if (preferenceRepository.isBiometricAllowed && isBiometricAvailable()) {
                startActivity(systemAuthIntent())
            } else {
                // preferenceRepository.logoutNhsUserAuthFlag is used in HomeActivity and SignInActivity,
                // because we need to call the /logout endpoint and remove the NHS user data
                preferenceRepository.logoutNhsUserAuthFlag = true
                redirectOnAuthFailureUseCase(context = this)
            }
        }
    }
}