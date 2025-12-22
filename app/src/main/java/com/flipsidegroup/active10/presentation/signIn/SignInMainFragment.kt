package com.flipsidegroup.active10.presentation.signIn

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.FragmentSignInMainBinding
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.onboarding.nhs.NhsLoginIntent
import com.flipsidegroup.active10.presentation.onboarding.nhs.NhsLoginWebViewActivity.Companion.RESULT_NHS_LOGIN_NO_CONSENT
import com.flipsidegroup.active10.presentation.stayUpdated.getStayUpdatedIntent
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.accessibilityFocusRequest
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.flipsidegroup.active10.utils.setTextHtml
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class SignInMainFragment : BaseFragment<BaseView>() {

    private var screenContent: ScreenContent? = null

    private var binding: FragmentSignInMainBinding by lifecycleAwareVariable()

    private var flowType: FlowType? = null

    @Inject
    lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    private val loginActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            when (activityResult.resultCode) {
                RESULT_OK -> {
                    flowType?.let {
                        if (it == FlowType.SETTINGS) startActivity(
                            requireContext().getStayUpdatedIntent(
                                flowType = it,
                                isWhiteToolbar = true
                            )
                        )
                        else finishActivity(isLoginSuccess = true)
                    }
                }

                RESULT_NHS_LOGIN_NO_CONSENT -> {
                    finishActivity(isLoginSuccess = false)
                }
            }
        }

    companion object {

        fun newInstance(content: ScreenContent): SignInMainFragment {
            val instance = SignInMainFragment()
            instance.arguments = Bundle().apply {
                putParcelable(Constants.SIGN_IN_MAIN_CONTENT, content)
            }
            return instance
        }
    }

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAnalyticsHelper.sendViewScreenEvent("NHSLoginOnboarding")
        return inflater.inflate(R.layout.fragment_sign_in_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignInMainBinding.bind(view)
        flowType = requireActivity()
            .intent
            .serializable<FlowType>(Constants.FLOW_TYPE)

        initializeContent()
        initializeButtonListener()
    }

    override fun onResume() {
        super.onResume()
        binding.signInTitle.accessibilityFocusRequest(300)
    }

    private fun initializeContent() {
        screenContent = arguments?.let {
            BundleCompat.getParcelable(
                it,
                Constants.SIGN_IN_MAIN_CONTENT,
                ScreenContent::class.java
            )
        }
        showContentInViews()
    }

    private fun showContentInViews() {
        screenContent?.let {
            binding.signInIMG.load(it.firstImageUrl)
            binding.signInTitle.setTextHtml(it.title)

            val layoutManager = LinearLayoutManager(context)
            binding.signInRV.layoutManager = layoutManager

            val adapter = SignInMainAdapter().apply {
                linkClickListener = {
                    firebaseAnalyticsHelper.sendButtonClickedEvent("NHSLoginOnboardingFindOutMore")
                    (activity as SignInListener).loadTripDialog()
                }
            }
            adapter.addData(it)
            binding.signInRV.adapter = adapter

            binding.signInTipLayout.image.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_green_warning_v2)
            )
            binding.signInTipLayout.description.setTextHtml(
                it.description
            )

            binding.continueBTN.setOnClickListener {
                firebaseAnalyticsHelper.sendButtonClickedEvent("NHSLoginOnboardingContinue")
                goToLoginWebView()
            }

            binding.skipBTN.setOnClickListener {
                firebaseAnalyticsHelper.sendButtonClickedEvent("NHSLoginOnboardingAskMeLater")
                finishActivity(isLoginSuccess = false)
            }
        }
    }

    private fun finishActivity(isLoginSuccess: Boolean) {
        val intent = Intent()
        intent.putExtra(Constants.IS_LOGIN_SUCCESS, isLoginSuccess)
        requireActivity().setResult(AppCompatActivity.RESULT_OK, intent)
        requireActivity().finish()
    }

    private fun goToLoginWebView() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_sign_in_web_view_title)
            .setMessage(R.string.dialog_sign_in_web_view_subtitle)
            .setPositiveButton(R.string.button_continue) { _, _ ->
                launchNhsLoginWebView()
            }
            .setNegativeButton(R.string.button_cancel) { _, _ -> }
            .show()
    }

    private fun initializeButtonListener() {
        binding.signInGoToInfoBTN.setOnClickListener {
            firebaseAnalyticsHelper.sendButtonClickedEvent("NHSLoginOnboardingHowWeUseYourData")
            (activity as SignInListener).goToSignInInfo()
        }
    }

    private fun launchNhsLoginWebView() {
        loginActivityLauncher.launch(
            requireContext().NhsLoginIntent(flowType = flowType!!)
        )
    }
}