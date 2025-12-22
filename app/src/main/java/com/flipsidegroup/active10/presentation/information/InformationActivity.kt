package com.flipsidegroup.active10.presentation.information

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ActivityInformationBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.loadFromUrl
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

private const val PARAM_SCREEN_ID = "SCREEN_ID"


fun Context.InformationIntent(screenId: String): Intent {
    return Intent(this, InformationActivity::class.java).apply {
        putExtra(PARAM_SCREEN_ID, screenId)
    }
}

class InformationActivity: BaseSecureActivity<InformationContract.View>(), InformationContract.View{

    @Inject
    lateinit var presenter: InformationPresenter

    override fun getPresenter(): LifecycleAwarePresenter<InformationContract.View> = presenter

    private var binding: ActivityInformationBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            ActivityInformationBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        val screenId = intent.getStringExtra(PARAM_SCREEN_ID)
            ?: throw IllegalStateException("Screen id is required")

        presenter.loadContent(screenId)

        initListeners()
    }

    private fun initListeners() {
        binding.informationPrimaryButton.setOnClickListener {
            presenter.onPrimaryButtonClicked()
        }
        binding.informationSecondaryButton.setOnClickListener {
            presenter.onSecondaryButtonClicked()
        }
        binding.informationPrimaryDarkButton.setOnClickListener {
            presenter.onPrimaryButtonClicked()
        }
        binding.informationSecondaryDarkButton.setOnClickListener {
            presenter.onSecondaryButtonClicked()
        }
    }

    override fun onBackPressed() {}

    override fun openNextInformationScreen(screenId: String) {
        startActivity(InformationIntent(screenId))
        finish()
    }

    override fun close() {
        finish()
    }

    override fun configureView(
        title: String,
        description: String,
        imageUrl: String?,
        primaryButtonText: String?,
        secondaryButtonText: String?,
        useDarkBackground: Boolean
    ) {
        binding.informationPrimaryButton.isVisible = !useDarkBackground && primaryButtonText != null
        binding.informationPrimaryDarkButton.isVisible = useDarkBackground && primaryButtonText != null
        binding.informationSecondaryButton.isVisible = !useDarkBackground && secondaryButtonText != null
        binding.informationSecondaryDarkButton.isVisible = useDarkBackground && secondaryButtonText != null

        binding.informationTitle.text = title
        binding.informationDescription.text = description
        binding.informationImage.loadFromUrl(imageUrl)

        binding.informationPrimaryButton.text = primaryButtonText
        binding.informationSecondaryButton.text = secondaryButtonText
        binding.informationPrimaryDarkButton.text = primaryButtonText
        binding.informationSecondaryDarkButton.text = secondaryButtonText

        binding.informationContainer.setBackgroundResource(
            if (useDarkBackground) {
                R.drawable.bg_cardview_top_corners_teal
            } else {
                R.drawable.bg_cardview_top_corners_white
            }
        )

        val textColor = if (useDarkBackground) {
                getColor(R.color.white)
            } else {
                getColor(R.color.black_33)
            }

        binding.informationTitle.setTextColor(textColor)
        binding.informationDescription.setTextColor(textColor)
    }

    override fun openPlayStore(packageName: String) {
        try {
            // Try to open the app page in Google Play app
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            // If Google Play app is not installed, open in web browser
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }
}