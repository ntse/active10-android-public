package com.flipsidegroup.active10.presentation.authentication.sure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import com.flipsidegroup.active10.databinding.ActivityAuthSureSignInBinding
import com.flipsidegroup.active10.presentation.authentication.setPin.setPinIntent
import com.flipsidegroup.active10.presentation.authentication.systemAuth.systemAuthSignInFlowIntent
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.onboarding.activities.termsAndConditionsIntent
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.isBiometricAvailable
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

fun Context.authSureSignInIntent(flowType: FlowType): Intent {
    return Intent(this, AuthSureSignInActivity::class.java).apply {
        putExtra(Constants.FLOW_TYPE, flowType)
    }
}

class AuthSureSignInActivity : BasePublicActivity<BaseView>() {

    override fun getPresenter() = null

    private var binding: ActivityAuthSureSignInBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityAuthSureSignInBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.toolbar.backTV.setOnClickListener { onBackPressed() }

        val flowType = intent.serializable<FlowType>(Constants.FLOW_TYPE)

        with(binding) {
            biometricBtn.isVisible = isBiometricAvailable()
            biometricBtn.setOnClickListener {
                startActivity(systemAuthSignInFlowIntent(flowType))
                finish()
            }
            pinBtn.setOnClickListener {
                startActivity(setPinIntent(flowType))
                finish()
            }
            sureBtn.setOnClickListener {
                //30 sec instead 12h Continuous Usage of app by NHS user is too short for testing re-auth. This is workaround
                with(preferenceRepository) {
                    if (set30SecForAuthTest != null) continuousUsageNhsTime = System.currentTimeMillis()
                }

                checkAuthManager.invokeAuthScreen.value = false
                if (flowType == FlowType.ONBOARDING) {
                    startActivity(termsAndConditionsIntent(flowType = flowType))
                    finish()
                } else {
                    val intent = Intent(this@AuthSureSignInActivity, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
            }
        }
    }
}