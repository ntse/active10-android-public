package com.flipsidegroup.active10.presentation.onboarding.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.OnboardingPermission
import com.flipsidegroup.active10.data.TermsConditions
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.databinding.FragmentTermsAndConditionsBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.legals.activity.LegalsActivity
import com.flipsidegroup.active10.presentation.onboarding.interfaces.TermsAndConditionsInitListener
import com.flipsidegroup.active10.presentation.onboarding.interfaces.TermsAndConditionsListener
import com.flipsidegroup.active10.presentation.onboarding.presenter.TermsAndConditionsPresenter
import com.flipsidegroup.active10.presentation.onboarding.view.TermsAndConditionsView
import com.flipsidegroup.active10.utils.Constants.IN_IS_NEW_USER
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.announceHeader
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.visible
import javax.inject.Inject

private const val DEFAULT_TERMS_AND_CONDITIONS_VERSION_DEBUG = 0
private const val DEFAULT_TERMS_AND_CONDITIONS_VERSION = 1
const val PRIVACY_POLICY = "privacy_policy"
const val ACCESSIBILITY = "accessibility"
const val TERMS_AND_CONDITIONS = "terms_and_conditions"

class TermsAndConditionsFragment : BaseFragment<TermsAndConditionsView>(),
    TermsAndConditionsView, TermsAndConditionsListener {

    companion object {
        fun newInstance(isNewUser: Boolean = true): TermsAndConditionsFragment {
            val instance = TermsAndConditionsFragment()
            instance.arguments = Bundle().apply {
                putBoolean(IN_IS_NEW_USER, isNewUser)
            }
            return instance
        }
    }

    @Inject
    internal lateinit var presenter: TermsAndConditionsPresenter

    @Inject
    internal lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    @Inject
    internal lateinit var preferenceRepository: PreferenceRepository

    private var termsAndConditionVersion: Int? = null
    private val defaultTcVersion: Int
        get() = if (BuildConfig.DEBUG) {
            DEFAULT_TERMS_AND_CONDITIONS_VERSION_DEBUG
        } else {
            DEFAULT_TERMS_AND_CONDITIONS_VERSION
        }

    private var binding: FragmentTermsAndConditionsBinding by lifecycleAwareVariable()

    override fun getPresenter(): LifecycleAwarePresenter<TermsAndConditionsView>? = presenter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is TermsAndConditionsInitListener) {
            context.initTermsAndConditionsListener(this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logScreenViewEvent()
        return inflater.inflate(R.layout.fragment_terms_and_conditions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTermsAndConditionsBinding.bind(view)

        presenter.getTermsAndCondLastVersion()
        presenter.getContent()
        setTermsAndConditionsAccessibility()
    }

    override fun onContentReceived(onboardingPermission: OnboardingPermission?) =
        setUpTermsAndConditions(onboardingPermission)

    override fun onTermsRulesRetrieved(terms: TermsConditions?) {
        termsAndConditionVersion =
            if (terms?.latestVersion != "null")
                terms?.latestVersion?.toInt() ?: defaultTcVersion
            else defaultTcVersion
    }

    override fun areTermsAndConditionsAccepted(): Boolean {
        return binding.termsConditionsCheckbox.isChecked
    }

    override fun showTermsAndConditionError() {
        scrollDown()
        binding.termsConditionsNeedTooAgreeError.visible()
        binding.termsConditionsNeedTooAgreeError.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
    }

    override fun scrollDown() {
        binding.scrollContainer.fullScroll(View.FOCUS_DOWN)
    }

    override fun getTermsVersion(): Int {
        return termsAndConditionVersion
            ?: if (BuildConfig.DEBUG) {
                DEFAULT_TERMS_AND_CONDITIONS_VERSION_DEBUG
            } else {
                DEFAULT_TERMS_AND_CONDITIONS_VERSION
            }
    }

    private fun setTermsAndConditionsAccessibility() {
        binding.termsConditionsTermsContainer.apply {
            contentDescription = "${binding.termsConditionsTermsTv.text}, Button."
        }

        binding.termsConditionsPolicyContainer.apply {
            contentDescription = "${binding.termsConditionsPolicyTv.text}, Button."
        }
    }

    private fun setUpTermsAndConditions(onboardingPermission: OnboardingPermission?) {
        onboardingPermission?.let {
            binding.termsConditionsSubtitle.text = it.introNewUser
        }

        binding.termsConditionsTermsContainer.setOnClickListener {
            startActivity(requireContext().LegalsActivity(TERMS_AND_CONDITIONS))
        }

        binding.termsConditionsPolicyContainer.setOnClickListener {
            startActivity(requireContext().LegalsActivity(PRIVACY_POLICY))
        }

        binding.termsConditionsCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) binding.termsConditionsNeedTooAgreeError.visible(false)
        }
    }

    private fun logScreenViewEvent() {
        firebaseAnalyticsHelper.sendViewScreenEvent(if (preferenceRepository.isUserLoggedIn) "NHSLoginTermsAndConditions" else "TermsAndConditions")
    }
}
