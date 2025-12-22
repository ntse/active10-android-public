package com.flipsidegroup.active10.presentation.onboarding.nhs.sure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import com.flipsidegroup.active10.databinding.ActivityNoConsentSureBinding
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.onboarding.nhs.NhsLoginWebViewActivity.Companion.RESULT_NHS_LOGIN_NO_CONSENT
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

fun Context.noConsentSureIntent(): Intent {
    return Intent(this, NoConsentSureActivity::class.java)
}

class NoConsentSureActivity : BasePublicActivity<BaseView>() {

    override fun getPresenter() = null

    private var binding: ActivityNoConsentSureBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityNoConsentSureBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.toolbar.backTV.setOnClickListener { onBackPressed() }

        with(binding) {
            primaryBtn.setOnClickListener {
                finish()
            }
            secondaryBtn.setOnClickListener {
                setResult(RESULT_NHS_LOGIN_NO_CONSENT)
                finish()
            }
        }
    }
}