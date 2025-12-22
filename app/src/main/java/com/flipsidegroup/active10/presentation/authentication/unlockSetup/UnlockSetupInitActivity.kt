package com.flipsidegroup.active10.presentation.authentication.unlockSetup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import com.flipsidegroup.active10.databinding.ActivityUnlockSetupInitBinding
import com.flipsidegroup.active10.presentation.authentication.setPin.setPinIntent
import com.flipsidegroup.active10.presentation.authentication.sure.authSureSignInIntent
import com.flipsidegroup.active10.presentation.authentication.systemAuth.systemAuthSignInFlowIntent
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.isBiometricAvailable
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

fun Context.unlockSetupInitIntent(flowType: FlowType): Intent {
    return Intent(this, UnlockSetupInitActivity::class.java).apply {
        putExtra(Constants.FLOW_TYPE, flowType)
    }
}

class UnlockSetupInitActivity : BasePublicActivity<BaseView>() {

    override fun getPresenter() = null

    private var binding: ActivityUnlockSetupInitBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityUnlockSetupInitBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        onBackPressedDispatcher.addCallback(this) {}

        val flowType = intent.serializable<FlowType>(Constants.FLOW_TYPE)

        with(binding) {
            biometricBtn.isVisible = isBiometricAvailable()
            biometricBtn.setOnClickListener {
                startActivity(systemAuthSignInFlowIntent(flowType))
            }
            pinBtn.setOnClickListener {
                startActivity(setPinIntent(flowType))
            }
            sureBtn.setOnClickListener {
                startActivity(authSureSignInIntent(flowType))
            }
        }
    }
}