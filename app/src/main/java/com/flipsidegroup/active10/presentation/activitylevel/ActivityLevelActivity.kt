package com.flipsidegroup.active10.presentation.activitylevel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat
import androidx.lifecycle.MutableLiveData
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.ActivityLevelEnum
import com.flipsidegroup.active10.data.models.ClassicUser
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.databinding.ActivityActivityLevelBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

fun Context.getActivityLevelIntent(): Intent {
    return Intent(this, ActivityLevelActivity::class.java)
}

class ActivityLevelActivity : BaseSecureActivity<ActivityLevelView>(), ActivityLevelView {

    @Inject
    internal lateinit var presenter: ActivityLevelPresenter

    override fun getPresenter(): LifecycleAwarePresenter<ActivityLevelView> = presenter

    private var binding: ActivityActivityLevelBinding by lifecycleAwareVariable()

    private var userActivityLevel: ActivityLevelEnum? = null

    private val continueButtonValidation = MutableLiveData<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityActivityLevelBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        binding.activityLevelToolbar.backTV.setOnClickListener { onBackPressed() }
        binding.activityLevelToolbar.titleTV.text = getString(R.string.settings_my_activity_level)

        handleSaveButton()
        handleSkipButton()
        handleContinueValidation()
        presenter.loadContent()
    }

    override fun showContent(content: ScreenContent) {
        setupActivityCheckBoxes(content)
        presenter.loadData()
    }

    override fun showData(classicUser: ClassicUser?) {
        classicUser?.activityLevel?.let {
            with(binding) {
                when (ActivityLevelEnum.fromValue(it)) {
                    ActivityLevelEnum.INACTIVE -> activityLevelNoActiveRB.isChecked = true
                    ActivityLevelEnum.MODERATELY_ACTIVE -> activityLevelModerateActiveRB.isChecked = true
                    ActivityLevelEnum.ACTIVE -> activityLevelVeryActiveRB.isChecked = true
                    else -> null
                }
            }
        }
    }

    override fun onBack() {
        finish()
    }

    private fun handleContinueValidation() {
        continueButtonValidation.value = userActivityLevel != null
    }

    private fun handleSaveButton() {
        continueButtonValidation.observe(this) {
            binding.saveButton.isEnabled = it
        }

        binding.saveButton.setOnClickListener {
            presenter.saveData(userActivityLevel)
        }
    }

    private fun handleSkipButton() {
        binding.skipButton.setOnClickListener {
            finish()
        }
    }

    private fun setupActivityCheckBoxes(screenContent: ScreenContent?) {
        val inactiveText = screenContent?.getPropertyValue("inactive_text")
        val moderateText = screenContent?.getPropertyValue("moderately_active_text")
        val activeText = screenContent?.getPropertyValue("active_text")

        binding.inactiveCheckboxTitle.text = getString(R.string.inactive_title)
        binding.inactiveCheckboxText.text = inactiveText ?: getString(R.string.inactive_description)
        setAccessibilityForRadioButton(
            binding.inactiveCheckboxLayout,
            binding.inactiveCheckboxTitle,
            binding.inactiveCheckboxText,
            binding.activityLevelNoActiveRB
        )

        binding.moderatelyActiveCheckboxTitle.text = getString(R.string.moderatelyActive_title)
        binding.moderatelyActiveCheckboxText.text =
            moderateText ?: getString(R.string.moderatelyActive_description)
        setAccessibilityForRadioButton(
            binding.moderatelyActiveCheckboxLayout,
            binding.moderatelyActiveCheckboxTitle,
            binding.moderatelyActiveCheckboxText,
            binding.activityLevelModerateActiveRB
        )

        binding.veryActiveCheckboxTitle.text = getString(R.string.active_title)
        binding.veryActiveCheckboxText.text =
            activeText ?: getString(R.string.active_description)
        setAccessibilityForRadioButton(
            binding.veryActiveCheckboxLayout,
            binding.veryActiveCheckboxTitle,
            binding.veryActiveCheckboxText,
            binding.activityLevelVeryActiveRB
        )

        binding.inactiveCheckboxLayout.setOnClickListener { _ ->
            binding.activityLevelNoActiveRB.isChecked = true

        }

        binding.activityLevelNoActiveRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userActivityLevel = ActivityLevelEnum.INACTIVE
                binding.activityLevelNoActiveRB.isChecked = true
                binding.activityLevelModerateActiveRB.isChecked = false
                binding.activityLevelVeryActiveRB.isChecked = false
                handleContinueValidation()
                binding.inactiveCheckboxLayout.announceForAccessibility("Selected")
            }
        }

        binding.moderatelyActiveCheckboxLayout.setOnClickListener { _ ->
            binding.activityLevelModerateActiveRB.isChecked = true
        }

        binding.activityLevelModerateActiveRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userActivityLevel = ActivityLevelEnum.MODERATELY_ACTIVE
                binding.activityLevelModerateActiveRB.isChecked = true
                binding.activityLevelNoActiveRB.isChecked = false
                binding.activityLevelVeryActiveRB.isChecked = false
                handleContinueValidation()
                binding.moderatelyActiveCheckboxLayout.announceForAccessibility("Selected")
            }
        }

        binding.veryActiveCheckboxLayout.setOnClickListener { _ ->
            binding.activityLevelVeryActiveRB.isChecked = true
        }

        binding.activityLevelVeryActiveRB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                userActivityLevel = ActivityLevelEnum.ACTIVE
                binding.activityLevelVeryActiveRB.isChecked = true
                binding.activityLevelNoActiveRB.isChecked = false
                binding.activityLevelModerateActiveRB.isChecked = false
                handleContinueValidation()
                binding.veryActiveCheckboxLayout.announceForAccessibility("Selected")
            }
        }
    }

    private fun setAccessibilityForRadioButton(
        btnView: View,
        btnTitle: TextView,
        btnText: TextView,
        btnRadio: AppCompatRadioButton
    ) {
        ViewCompat.setAccessibilityDelegate(btnView, object : AccessibilityDelegateCompat() {
            override fun onInitializeAccessibilityNodeInfo(
                v: View, info: AccessibilityNodeInfoCompat
            ) {
                super.onInitializeAccessibilityNodeInfo(v, info)
                info.actionList.clear()
                info.contentDescription =
                    "${if (btnRadio.isChecked) "Selected" else "Not selected"}. " + "${btnTitle.text}. ${btnText.text}. "
                info.roleDescription = "Radio Button"
                info.addAction(
                    AccessibilityActionCompat(
                        AccessibilityNodeInfoCompat.ACTION_CLICK, "Toggle"
                    )
                )
            }
        })
    }
}