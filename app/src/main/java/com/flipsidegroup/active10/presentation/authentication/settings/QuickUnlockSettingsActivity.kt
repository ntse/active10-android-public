package com.flipsidegroup.active10.presentation.authentication.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ActivityQuickUnlockSettingsBinding
import com.flipsidegroup.active10.presentation.authentication.setPin.setPinIntent
import com.flipsidegroup.active10.presentation.authentication.sure.authSureIntent
import com.flipsidegroup.active10.presentation.authentication.systemAuth.systemAuthSignInFlowIntent
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.isBiometricAvailable
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

fun Context.getQuickUnlockSettingsIntent(): Intent {
    return Intent(this, QuickUnlockSettingsActivity::class.java)
}

class QuickUnlockSettingsActivity : BaseSecureActivity<BaseView>() {

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null

    private var binding: ActivityQuickUnlockSettingsBinding by lifecycleAwareVariable()
    private val stepType = MutableLiveData<QuickUnlockSettingsContentType>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityQuickUnlockSettingsBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.toolbar.backTV.setOnClickListener { onBackPressed() }
        binding.toolbar.titleTV.text = getString(R.string.settings_quick_unlock)

        stepType.value = if (preferenceRepository.isBiometricAllowed) {
            QuickUnlockSettingsContentType.DISABLE_BIOMETRIC
        } else if (!preferenceRepository.authPinCode.isNullOrBlank()) {
            QuickUnlockSettingsContentType.ENABLE_BIOMETRIC
        } else {
            QuickUnlockSettingsContentType.ENABLE_QUICK_UNLOCK
        }

        stepType.observe(this) { type ->
            with(binding) {
                title.text = getString(type.title)
                description.text = getString(type.description)
                primaryBtn.text = getString(type.primaryBtnText)
                secondaryBtn.text = getString(type.secondaryBtnText)
            }

            when (type!!) {
                QuickUnlockSettingsContentType.ENABLE_QUICK_UNLOCK -> {
                    binding.primaryBtn.isVisible = isBiometricAvailable()
                    binding.primaryBtn.setOnClickListener {
                        startActivity(systemAuthSignInFlowIntent(flowType = FlowType.SETTINGS))
                    }
                    binding.secondaryBtn.setOnClickListener {
                        startActivity(setPinIntent(flowType = FlowType.SETTINGS))
                    }
                }

                QuickUnlockSettingsContentType.ENABLE_BIOMETRIC -> {
                    binding.primaryBtn.isVisible = isBiometricAvailable()
                    binding.primaryBtn.setOnClickListener {
                        startActivity(systemAuthSignInFlowIntent(flowType = FlowType.SETTINGS))
                    }
                    binding.secondaryBtn.setOnClickListener {
                        startActivity(authSureIntent(parentStepType = type))
                    }
                }

                QuickUnlockSettingsContentType.DISABLE_BIOMETRIC -> {
                    binding.primaryBtn.setOnClickListener {
                        QuickUnlockSettingsDialog(
                            onPrimaryBtnClick = {
                                startActivity(setPinIntent(flowType = FlowType.SETTINGS))
                            },
                            onSecondaryBtnClick = {
                                startActivity(authSureIntent(parentStepType = type))
                            }
                        ).show(
                            supportFragmentManager,
                            QuickUnlockSettingsDialog::class.java.simpleName
                        )
                    }
                    binding.secondaryBtn.setOnClickListener {
                        startActivity(authSureIntent(parentStepType = type))
                    }
                }
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(STEP_TYPE, stepType.value)
    }

    companion object {
        private const val STEP_TYPE = "step_type"
    }

}