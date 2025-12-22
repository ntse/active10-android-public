package com.flipsidegroup.active10.presentation.stayUpdated

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ActivityStayUpdatedBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.onboarding.activities.PermissionActivity
import com.flipsidegroup.active10.presentation.progressBar.getProgressBarIntent
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

fun Context.getStayUpdatedIntent(flowType: FlowType, isWhiteToolbar: Boolean = false): Intent {
    return Intent(this, StayUpdatedActivity::class.java).apply {
        putExtra(Constants.FLOW_TYPE, flowType)
        putExtra(Constants.IS_WHITE_TOOLBAR, isWhiteToolbar)
    }
}

class StayUpdatedActivity : BaseSecureActivity<BaseView>() {

    private var fragment: StayUpdatedFragment? = null
    private var flowType: FlowType? = null
    private var isWhiteToolbar: Boolean = false

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null

    private var binding: ActivityStayUpdatedBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(ActivityStayUpdatedBinding.inflate(layoutInflater).apply { binding = this }.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        flowType = intent.serializable<FlowType>(Constants.FLOW_TYPE)
        isWhiteToolbar = intent.getBooleanExtra(Constants.IS_WHITE_TOOLBAR, false)

        setUpThisToolbar()
        setUpContinueButton()
        showFragment()
    }

    private fun setUpThisToolbar() {
        flowType?.let {
            with(binding.setStayUpdatedToolbar.rootLayout) {
                setBackgroundColor(
                    ContextCompat.getColor(
                        this@StayUpdatedActivity, if (it == FlowType.ONBOARDING || isWhiteToolbar) R.color.white else R.color.colorPrimary
                    )
                )
                binding.setStayUpdatedToolbar.backTV.setIconTintResource(
                    if (it == FlowType.ONBOARDING || isWhiteToolbar) R.color.greyish_brown_two else R.color.white
                )
                title =
                    if (it == FlowType.ONBOARDING || isWhiteToolbar) "" else resources.getString(R.string.settings_communication_preference)

                if (it == FlowType.STAY_UPDATED_ONLY) {
                    binding.setStayUpdatedToolbar.backTV.setOnClickListener { onBackPressed() }
                }
            }
        }
    }

    private fun showFragment() {
        fragment = StayUpdatedFragment.newInstance(isOnboarding = false)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragment?.let {
            transaction.replace(R.id.stayUpdatedFragmentContainer, it).commit()
        }
    }

    private fun setUpContinueButton() {
        binding.continueBTN.text = flowType?.let {
            resources.getString(if (it == FlowType.ONBOARDING || isWhiteToolbar) R.string.button_continue else R.string.button_save)
        }
        binding.continueBTN.setOnClickListener {
            fragment?.let {
                (it as StayUpdatedListener).saveData()
            }
            flowType?.let {
                when(it) {
                    FlowType.STAY_UPDATED_ONLY -> finish()
                    FlowType.SETTINGS -> startActivity(getProgressBarIntent(flowType = it))
                    FlowType.ONBOARDING -> startActivity(PermissionActivity())
                    else -> Unit
                }
            }
        }
    }

}