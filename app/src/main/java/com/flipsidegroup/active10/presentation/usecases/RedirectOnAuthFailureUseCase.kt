package com.flipsidegroup.active10.presentation.usecases

import android.content.Context
import android.content.Intent
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.signIn.signInIntent
import com.flipsidegroup.active10.utils.FlowType
import javax.inject.Inject

class RedirectOnAuthFailureUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
) {

    operator fun invoke(context: Context) {
        // preferenceRepository.logoutNhsUserAuthFlag is used in HomeActivity and SignInActivity,
        // because we need to call the /logout endpoint and remove the NHS user data
        with(context) {
            if (preferenceRepository.isOnboardingCompleted) {
                startActivity(
                    Intent(this, HomeActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    }
                )
            } else {
                startActivity(
                    signInIntent(flowType = FlowType.ONBOARDING).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    }
                )
            }
        }
    }
}
