package com.flipsidegroup.active10.presentation.accountLoggedOut

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ActivityNhsAccountLoggedOutBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.signIn.signInIntent
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

enum class AccountLogOutType {
    LOGOUT,
    DISCONNECT
}

fun Context.AccountLoggedOutActivityIntent(type: AccountLogOutType): Intent {
    return Intent(this, AccountLoggedOutActivity::class.java).apply {
        putExtra("type", type.name)
    }
}

class AccountLoggedOutActivity: BaseSecureActivity<BaseView>() {

    override fun getPresenter() = null
    private var binding: ActivityNhsAccountLoggedOutBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContentView(
            ActivityNhsAccountLoggedOutBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        val isLogout = intent.getStringExtra("type") == AccountLogOutType.LOGOUT.name

        with(binding) {
            top.setGuidelineBeginToTopInset()

            accountLoggedOutToolbar.backTV.setOnClickListener { onBackPressed() }

            accountLoggedOutTitle.text = if (isLogout) {
                getString(R.string.logged_out_account_title)
            } else {
                getString(R.string.disconnected_account_title)
            }
            accountLoggedOutMessage.text = if (isLogout) {
                getString(R.string.logged_out_account_message)
            } else {
                getString(R.string.disconnected_account_message)
            }
        }

        binding.closeBtn.setOnClickListener {
            finish()
        }

        binding.loginBTN.setOnClickListener {
            startActivity(signInIntent(flowType = FlowType.SETTINGS))
            finish()
        }
    }

}