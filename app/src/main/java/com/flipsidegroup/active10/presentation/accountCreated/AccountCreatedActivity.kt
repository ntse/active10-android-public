package com.flipsidegroup.active10.presentation.accountCreated

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import com.flipsidegroup.active10.databinding.ActivityNhsAccountCreatedBinding
import com.flipsidegroup.active10.presentation.authentication.unlockSetup.unlockSetupInitIntent
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

fun Context.AccountCreatedIntent(flowType: FlowType): Intent {
    return Intent(this, AccountCreatedActivity::class.java).apply {
        putExtra(Constants.FLOW_TYPE, flowType)
    }
}

class AccountCreatedActivity : BasePublicActivity<BaseView>() {

    override fun getPresenter() = null

    private var binding: ActivityNhsAccountCreatedBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        firebaseAnalyticsHelper.sendViewScreenEvent("NHSLoginConnectingAccountSuccess")

        setContentView(
            ActivityNhsAccountCreatedBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        onBackPressedDispatcher.addCallback(this) {}

        val flowType = intent.serializable<FlowType>(Constants.FLOW_TYPE)

        binding.continueBTN.setOnClickListener {
            navigateToNextScreen(flowType)
        }
    }

    private fun navigateToNextScreen(flowType: FlowType) {
        startActivity(unlockSetupInitIntent(flowType))
    }

}