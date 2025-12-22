package com.flipsidegroup.active10.presentation.authentication.unlockSetup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import com.flipsidegroup.active10.databinding.ActivityUnlockSetupSuccessBinding
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.onboarding.activities.termsAndConditionsIntent
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

fun Context.unlockSetupSuccessIntent(flowType: FlowType): Intent {
    return Intent(this, UnlockSetupSuccessActivity::class.java).apply {
        putExtra(Constants.FLOW_TYPE, flowType)
    }
}

class UnlockSetupSuccessActivity : BasePublicActivity<BaseView>() {

    override fun getPresenter() = null

    private var binding: ActivityUnlockSetupSuccessBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityUnlockSetupSuccessBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        onBackPressedDispatcher.addCallback(this) {}

        val flowType = intent.serializable<FlowType>(Constants.FLOW_TYPE)

        with(binding) {
            continueBtn.setOnClickListener {
                if (flowType == FlowType.ONBOARDING) {
                    startActivity(termsAndConditionsIntent(flowType))
                } else {
                    val intent = Intent(this@UnlockSetupSuccessActivity, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                }
            }
        }
    }
}