package com.flipsidegroup.active10.presentation.progressBar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.ActivityProgressBarBinding
import com.flipsidegroup.active10.presentation.accountCreated.AccountCreatedIntent
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.announceHeader
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.serializable
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import timber.log.Timber
import javax.inject.Inject

fun Context.getProgressBarIntent(flowType: FlowType): Intent {
    return Intent(this, ProgressBarActivity::class.java).apply {
        putExtra(Constants.FLOW_TYPE, flowType)
    }
}

class ProgressBarActivity : BasePublicActivity<ProgressBarView>(), ProgressBarView {

    @Inject
    internal lateinit var presenter: ProgressBarPresenter

    private lateinit var flowType: FlowType

    override fun getPresenter(): LifecycleAwarePresenter<ProgressBarView> = presenter

    private var binding: ActivityProgressBarBinding by lifecycleAwareVariable()
    private var isLoggingBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        firebaseAnalyticsHelper.sendViewScreenEvent("NHSLoginConnectingAccount")

        val lastSyncTime = settingsUtils.getSettingsHolder().nhsLastSyncTime
        val lastSyncUserId = settingsUtils.getSettingsHolder().nhsLastSyncUserId
        isLoggingBack = lastSyncTime != null && lastSyncTime != 0L && lastSyncUserId != null && lastSyncUserId == settingsUtils.getSettingsHolder().nhsUser?.id

        setContentView(
            ActivityProgressBarBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
            root.setBottomPaddingToBottomInset()
        }
        onBackPressedDispatcher.addCallback(this) {}

        flowType = intent.serializable<FlowType>(Constants.FLOW_TYPE)

        presenter.loadContent()
        presenter.startSynchronization()
        binding.progressBarTitle.announceHeader()
    }

    override fun showProgress(progressPercent: Int) {
        Timber.d("Progress: $progressPercent")
        binding.progressBar.progress = progressPercent
        val fixedPercent = if (progressPercent < 100) progressPercent else 100
        binding.progressBarPercentage.text =
            getString(R.string.nhs_progress_bar_loaded, fixedPercent.toString())
    }

    override fun navigateToNextScreen() {
        startActivity(AccountCreatedIntent(flowType))
    }

//    override fun navigateToNextScreen() {
//        if (isLoggingBack) {
//            if (isSignInSettingsFlow == false) {
//                startActivity(PermissionActivity())
//            } else {
//                val intent = Intent(this, HomeActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                startActivity(intent)
//            }
//        } else {
//            startActivity(AccountCreatedIntent(flowType ?: false))
//        }
//    }

    override fun showContent(screenContent: ScreenContent?) {
        screenContent?.let {
            val bulletPoints = it.getPropertyValue("bullet_points")?.split(",")
            binding.progressBarTitle.text = it.title
            binding.progressBarDescriptionText1.isVisible = bulletPoints?.getOrNull(0) != null
            binding.progressBarDescriptionImage1.isVisible = bulletPoints?.getOrNull(0) != null
            binding.progressBarDescriptionText2.isVisible = bulletPoints?.getOrNull(1) != null
            binding.progressBarDescriptionImage2.isVisible = bulletPoints?.getOrNull(1) != null
            binding.progressBarDescriptionText3.isVisible = bulletPoints?.getOrNull(2) != null
            binding.progressBarDescriptionImage3.isVisible = bulletPoints?.getOrNull(2) != null
            binding.progressBarDescriptionText4.isVisible = bulletPoints?.getOrNull(3) != null
            binding.progressBarDescriptionImage4.isVisible = bulletPoints?.getOrNull(3) != null
            binding.progressBarDescriptionText5.isVisible = bulletPoints?.getOrNull(4) != null
            binding.progressBarDescriptionImage5.isVisible = bulletPoints?.getOrNull(4) != null
            binding.progressBarDescriptionText1.text = bulletPoints?.getOrNull(0)
            binding.progressBarDescriptionText2.text = bulletPoints?.getOrNull(1)
            binding.progressBarDescriptionText3.text = bulletPoints?.getOrNull(2)
            binding.progressBarDescriptionText4.text = bulletPoints?.getOrNull(3)
            binding.progressBarDescriptionText5.text = bulletPoints?.getOrNull(4)
        }
    }

    override fun showErrorMessage(message: String) {
        Timber.e(message) // TODO: implement error handling
    }
}