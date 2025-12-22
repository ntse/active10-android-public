package com.flipsidegroup.active10.presentation.onboarding.nhs

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.flipsidegroup.active10.databinding.ActivityNhsLoginWebViewBinding
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.onboarding.nhs.sure.noConsentSureIntent
import com.flipsidegroup.active10.presentation.progressBar.getProgressBarIntent
import com.flipsidegroup.active10.utils.Constants.FLOW_TYPE
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

fun Context.NhsLoginIntent(flowType: FlowType): Intent {
    return Intent(this, NhsLoginWebViewActivity::class.java).apply {
        putExtra(FLOW_TYPE, flowType)
    }
}

class NhsLoginWebViewActivity : BasePublicActivity<NhsLoginWebViewContract.View>(), NhsLoginWebViewContract.View {

    @Inject
    internal lateinit var presenter: NhsLoginWebViewPresenter
    override fun getPresenter(): LifecycleAwarePresenter<NhsLoginWebViewContract.View> = presenter

    private var binding: ActivityNhsLoginWebViewBinding by lifecycleAwareVariable()
    private lateinit var flowType: FlowType
    private val sureActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            when (activityResult.resultCode) {
                RESULT_NHS_LOGIN_NO_CONSENT -> {
                    setResult(RESULT_NHS_LOGIN_NO_CONSENT)
                    finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityNhsLoginWebViewBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
            root.setBottomPaddingToBottomInset()
        }
        flowType = intent.serializable<FlowType>(FLOW_TYPE)

        presenter.handleWebView(binding.webView)
    }

    override fun loginSuccess() {
        preferenceRepository.continuousUsageNhsTime = System.currentTimeMillis()

        when(flowType) {
            FlowType.ONBOARDING -> {
                startActivity(getProgressBarIntent(flowType))
                finish()
            }
            else -> {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun loginFailure() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun showNoConsentScreen() {
        sureActivityLauncher.launch(noConsentSureIntent())
    }

    companion object {
        const val RESULT_NHS_LOGIN_NO_CONSENT = 100
    }
}