package com.flipsidegroup.active10.presentation.onboarding.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.databinding.ActivityCustomGoalBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

const val IS_FROM_SETTINGS = "IS_FROM_SETTINGS"

fun Context.CustomGoalsIntent(isFromSettings: Boolean = false): Intent {
    return Intent(this, CustomGoalActivity::class.java).apply {
        putExtra(IS_FROM_SETTINGS, isFromSettings)
    }
}

class CustomGoalActivity : BaseSecureActivity<BaseView>() {

    private var binding: ActivityCustomGoalBinding by lifecycleAwareVariable()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityCustomGoalBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
            root.setBottomPaddingToBottomInset()
        }

        setUpClickListeners()
        setUpViews()
        setUpToolbar()
    }

    private fun setUpToolbar() {
        binding.customGoalToolbar.root.visibility =
            if (isFromSettings()) VISIBLE else INVISIBLE
        binding.customGoalWhiteToolbar.root.visibility =
            if (!isFromSettings()) VISIBLE else INVISIBLE

        binding.customGoalToolbar.titleTV.text = getString(R.string.add_custom_motivation_title)

        binding.customGoalToolbar.backTV.setOnClickListener { onBackPressed() }
        binding.customGoalWhiteToolbar.backTV.setOnClickListener { onBackPressed() }
    }

    private fun setUpViews() {
        binding.enterCustomMotivation.inputType =
            EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
        binding.enterCustomMotivation.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.enterCustomMotivation.setRawInputType(EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES)

        if (isFromSettings()) {
            binding.addCustomGoalButton.setBackgroundResource(R.drawable.teal_button_selector)
            binding.addCustomGoalButton.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.white,
                    null
                )
            )
        }
    }

    private fun setUpClickListeners() {
        binding.addCustomGoalButton.setOnClickListener {
            val newGoal = binding.enterCustomMotivation.text
            if (newGoal.isBlank()) {
                return@setOnClickListener
            }
            setResult(Activity.RESULT_OK, Intent().putExtra(GOALS_KEY, newGoal.toString()))
            finish()
        }

        binding.enterCustomMotivation.doOnTextChanged { text, _, _, _ ->
            binding.addCustomGoalButton.isEnabled = !text.isNullOrBlank()
        }
    }

    private fun isFromSettings() = intent.getBooleanExtra(IS_FROM_SETTINGS, false)

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null
}
