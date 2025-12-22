package com.flipsidegroup.active10.presentation.signIn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.ActivitySignInBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.dialogs.WalkingPlanTripDialog
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

fun Context.signInIntent(flowType: FlowType): Intent {
    return Intent(this, SignInActivity::class.java).apply {
        putExtra(Constants.FLOW_TYPE, flowType)
    }
}

class SignInActivity : BaseSecureActivity<SignInView>(), SignInView, SignInListener {

    @Inject
    internal lateinit var presenter: SignInPresenter

    override fun getPresenter(): LifecycleAwarePresenter<SignInView> = presenter

    private var signInAdapter: SignInAdapter? = null

    private var binding: ActivitySignInBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(ActivitySignInBinding.inflate(layoutInflater).apply { binding = this }.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
            root.setBottomPaddingToBottomInset()
        }
        onCustomBackPressed()

        presenter.loadData()
    }

    override fun onResume() {
        super.onResume()

        if (preferenceRepository.logoutNhsUserAuthFlag) {
            presenter.logout()
            preferenceRepository.logoutNhsUserAuthFlag = false
        }
    }

    private fun onCustomBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackButtonPressed()
            }
        })
    }

    override fun showData(contentMap: Map<String, ScreenContent>) {
        setUpViews(contentMap)
    }

    override fun goToSignInInfo() {
        goToNextScreen()
    }

    override fun loadTripDialog() {
        presenter.loadTripDialogContent()
    }

    override fun showTripDialog(contents: List<ScreenContent>) {
        WalkingPlanTripDialog(
            contents = contents
        ).show(supportFragmentManager, WalkingPlanTripDialog::class.java.name)
    }

    private fun onBackButtonPressed() {
        if (binding.signInVP.currentItem == 0) {
            finish()
        } else {
            goToPreviousScreen()
        }
    }

    private fun setUpViews(contentMap: Map<String, ScreenContent>) {
        signInAdapter = SignInAdapter(supportFragmentManager, contentMap)

        binding.signInVP.swipeEnabled = false

        binding.signInVP.adapter = signInAdapter
    }

    private fun goToNextScreen() {
        val currentItem = binding.signInVP.currentItem
        binding.signInVP.setCurrentItem(currentItem + 1, true)
    }

    private fun goToPreviousScreen() {
        val currentItem = binding.signInVP.currentItem
        binding.signInVP.setCurrentItem(currentItem - 1, true)
    }

}

