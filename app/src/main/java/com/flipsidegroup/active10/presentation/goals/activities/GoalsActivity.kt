package com.flipsidegroup.active10.presentation.goals.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.databinding.ActivityGoalsBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.goals.fragments.GoalsFragment
import com.flipsidegroup.active10.presentation.goals.presenter.GoalsActivityPresenter
import com.flipsidegroup.active10.presentation.goals.view.GoalsActivityView
import com.flipsidegroup.active10.presentation.onboarding.activities.CustomGoalsIntent
import com.flipsidegroup.active10.presentation.onboarding.activities.GOALS_KEY
import com.flipsidegroup.active10.presentation.onboarding.interfaces.GoalListener
import com.flipsidegroup.active10.utils.EarnBadgeHelper
import com.flipsidegroup.active10.utils.addGlobalLayoutListenerToHideViewWhenKeyboardShown
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.removeGlobalLayoutListener
import com.flipsidegroup.active10.utils.setBottomPaddingToBottomInset
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import timber.log.Timber
import javax.inject.Inject

const val IN_IS_FROM_SETTINGS = "IN_IS_FROM_SETTINGS"

fun Context.GoalsIntent(isFromSettings: Boolean = false): Intent {
    return Intent(this, GoalsActivity::class.java).apply {
        putExtra(IN_IS_FROM_SETTINGS, isFromSettings)
    }
}

private const val GOALS_SCREEN_ENUM_POS = 3

class GoalsActivity : BaseSecureActivity<GoalsActivityView>(), GoalsActivityView, GoalListener {

    @Inject
    internal lateinit var presenter: GoalsActivityPresenter

    private var binding: ActivityGoalsBinding by lifecycleAwareVariable()

    private lateinit var keyboardLayoutListener: ViewTreeObserver.OnGlobalLayoutListener

    private var goals = ArrayList<Goal>()

    override fun getPresenter(): LifecycleAwarePresenter<GoalsActivityView> = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(ActivityGoalsBinding.inflate(layoutInflater).apply { binding = this }.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
            root.setBottomPaddingToBottomInset()
        }

        setUpToolbar()
        setUpViews()
    }

    override fun onStart() {
        super.onStart()

        keyboardLayoutListener =
            binding.goalsParentLayout.addGlobalLayoutListenerToHideViewWhenKeyboardShown(
                buttonView = binding.goalsContinueButton,
                containerView = binding.goalsParentLayout,
            )
    }

    override fun onStop() {
        super.onStop()
        binding.goalsParentLayout.removeGlobalLayoutListener(keyboardLayoutListener)
    }

    private fun setUpToolbar() {
        binding.goalsToolbar.root.isVisible = isFromSettings()
        binding.goalsWhiteToolbar.root.isVisible = !isFromSettings()

        binding.goalsToolbar.titleTV.text = getString(R.string.settings_my_goals)

        binding.goalsToolbar.backTV.setOnClickListener { onBackPressed() }
        binding.goalsWhiteToolbar.backTV.setOnClickListener { onBackPressed() }
    }

    override fun onSendGoalsCompleted() {
        binding.goalsContinueButton.isEnabled = true
        finishActivity()
    }

    override fun onSendGoalsError() {
        binding.goalsContinueButton.isEnabled = true
        finishActivity()
    }

    override fun onGoalsReceived(goals: List<Goal>) {
        this.goals.clear()
        this.goals.addAll(goals)
    }

    override fun onGoalChangeListener(goalsList: List<Goal>) {
        goals.clear()
        goals.addAll(goalsList)

        setButtonState()
    }

    private fun setUpViews() {
        val storedGoalsList = settingsUtils.getSettingsHolder().goalsList
        storedGoalsList?.forEach {
            goals.add(it)
        }

        if (isFromSettings()) {
            binding.goalsContinueButton.setBackgroundResource(R.drawable.teal_button_selector)
            binding.goalsContinueButton.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.white,
                    null
                )
            )
        }

        setButtonState()

        val goalsFragment = GoalsFragment.newInstance(GOALS_SCREEN_ENUM_POS)
        supportFragmentManager.beginTransaction()
            .replace(binding.goalsContainer.id, goalsFragment)
            .commitAllowingStateLoss()

        binding.goalsContinueButton.setOnClickListener {
            val hasNewGoals = hasNewGoals()
            Timber.d("Should send goals: $hasNewGoals")
            if (!hasNewGoals) {
                finishActivity()
            } else {
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.GOAL_SETTER,
                    preferenceRepository = preferenceRepository
                )
                binding.goalsContinueButton.isEnabled = false
                updateSelectedGoals()
            }
        }

        val customGoalResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.getStringExtra(GOALS_KEY)?.let {
                        goalsFragment.updateCurrentGoals(it)
                    }
                }
            }

        binding.goalsAddCustomGoalButton.setOnClickListener {
            customGoalResultLauncher.launch(CustomGoalsIntent(isFromSettings()))
        }
    }

    private fun finishActivity() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun hasNewGoals(): Boolean {
        val savedGoalsList = settingsUtils.getSettingsHolder().goalsList
        if (savedGoalsList.isNullOrEmpty()) {
            return true
        }

        val goalsChanged = savedGoalsList != goals

        return goalsChanged
    }

    private fun setButtonState() {
        binding.goalsContinueButton.isEnabled = hasNewGoals() && goals.any { it.isSelected }
    }

    private fun updateSelectedGoals() {
        goals.removeAll { it.isCustomGoal && !it.isSelected }
        presenter.saveGoals(goals)
    }

    private fun isFromSettings() = intent.getBooleanExtra(IN_IS_FROM_SETTINGS, false)

}
