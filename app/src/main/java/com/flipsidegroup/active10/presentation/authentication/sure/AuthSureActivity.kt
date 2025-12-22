package com.flipsidegroup.active10.presentation.authentication.sure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ActivityAuthSureBinding
import com.flipsidegroup.active10.presentation.authentication.setPin.setPinIntent
import com.flipsidegroup.active10.presentation.authentication.settings.QuickUnlockSettingsContentType
import com.flipsidegroup.active10.presentation.authentication.systemAuth.systemAuthSignInFlowIntent
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.isBiometricAvailable
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.flipsidegroup.active10.utils.startFromSettingsFragment
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

fun Context.authSureIntent(parentStepType: QuickUnlockSettingsContentType): Intent {
    return Intent(this, AuthSureActivity::class.java).apply {
        putExtra(Constants.PARENT_STEP_TYPE, parentStepType)
    }
}

class AuthSureActivity : BasePublicActivity<BaseView>() {

    override fun getPresenter() = null

    private var binding: ActivityAuthSureBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityAuthSureBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.toolbar.backTV.setOnClickListener { onBackPressed() }

        val contentType =
            intent.serializable<QuickUnlockSettingsContentType>(Constants.PARENT_STEP_TYPE)

        with(binding) {
            primaryBtn.text = getString(contentType.primaryBtnText)
            secondaryBtn.text = getString(contentType.secondaryBtnText)

            when (contentType) {
                QuickUnlockSettingsContentType.ENABLE_QUICK_UNLOCK -> Unit

                QuickUnlockSettingsContentType.ENABLE_BIOMETRIC -> {
                    primaryBtn.isVisible = isBiometricAvailable()
                    primaryBtn.setOnClickListener {
                        startActivity(systemAuthSignInFlowIntent(flowType = FlowType.SETTINGS))
                    }
                    secondaryBtn.setOnClickListener {
                        removeQuickUnlockAuth()
                        startFromSettingsFragment()
                    }
                }

                QuickUnlockSettingsContentType.DISABLE_BIOMETRIC -> {
                    primaryBtn.text = getString(R.string.use_pin_id)
                    primaryBtn.setOnClickListener {
                        startActivity(setPinIntent(flowType = FlowType.SETTINGS))
                    }
                    secondaryBtn.setOnClickListener {
                        removeQuickUnlockAuth()
                        startFromSettingsFragment()
                    }
                }
            }
        }
    }

    private fun removeQuickUnlockAuth() {
        preferenceRepository.isBiometricAllowed = false
        preferenceRepository.authPinCode = null
    }
}