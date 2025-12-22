package com.flipsidegroup.active10.presentation.onboarding.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentTransaction
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.databinding.ActivityTermsAndConditionsBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.onboarding.fragments.TermsAndConditionsFragment
import com.flipsidegroup.active10.presentation.onboarding.interfaces.TermsAndConditionsInitListener
import com.flipsidegroup.active10.presentation.onboarding.interfaces.TermsAndConditionsListener
import com.flipsidegroup.active10.presentation.stayUpdated.getStayUpdatedIntent
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

fun Context.termsAndConditionsIntent(flowType: FlowType): Intent {
    return Intent(this, TermsAndConditionsActivity::class.java).apply {
        putExtra(Constants.FLOW_TYPE, flowType)
    }
}

class TermsAndConditionsActivity : BaseSecureActivity<BaseView>(), TermsAndConditionsInitListener {

    private var termsAndConditionsListener: TermsAndConditionsListener? = null
    private var fragment: TermsAndConditionsFragment? = null
    private lateinit var flowType: FlowType

    override fun getPresenter() = null

    private var binding: ActivityTermsAndConditionsBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityTermsAndConditionsBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.termsAndConditionsToolbar.backTV.setOnClickListener { onBackPressed() }

        flowType = intent.serializable<FlowType>(Constants.FLOW_TYPE)

        setUpContinueButton()
        showFragment()
    }

    override fun initTermsAndConditionsListener(termsAndConditionsListener: TermsAndConditionsListener) {
        this.termsAndConditionsListener = termsAndConditionsListener
    }

    private fun handleTermsAndConditions() {
        if (termsAndConditionsListener?.areTermsAndConditionsAccepted() == true) {
            val termsVersion = termsAndConditionsListener?.getTermsVersion()
            termsVersion?.let {
                settingsUtils.updateSettings(SettingsDataHolder(termsAndConditionsVersion = it))
            }
            if (flowType == FlowType.ONBOARDING && preferenceRepository.isUserLoggedIn) {
                startActivity(getStayUpdatedIntent(flowType = flowType))
            } else {
                startActivity(PermissionActivity())
            }
        } else {
            termsAndConditionsListener?.showTermsAndConditionError()
        }
    }

    private fun showFragment() {
        fragment = TermsAndConditionsFragment.newInstance()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragment?.let {
            transaction.replace(R.id.termsAndConditionsFragmentContainer, it).commit()
        }
    }

    private fun setUpContinueButton() {
        binding.continueBTN.text = resources.getString(R.string.button_continue)
        binding.continueBTN.setOnClickListener {
            handleTermsAndConditions()
        }
    }
}