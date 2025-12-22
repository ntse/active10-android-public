package com.flipsidegroup.active10.presentation.targets.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.dataholders.TargetHolder
import com.flipsidegroup.active10.databinding.ActivityTargetBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.targets.presenter.SetTargetPresenter
import com.flipsidegroup.active10.presentation.targets.view.SetTargetView
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.view.TargetView
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import java.time.LocalDate
import javax.inject.Inject

const val DEFAULT_TARGET = 1
private const val MINUTES_PER_TARGET = 10
private const val PARAM_MODE = "PARAM_MODE"
private const val NUMBER_OF_A10 = 3

fun Context.TargetIntent(
    mode: SetTargetMode
): Intent {
    return Intent(this, SetTargetActivity::class.java).apply {
        putExtra(PARAM_MODE, mode)
    }
}

enum class SetTargetMode {
    ONBOARDING, SETTINGS, HIGH_ACHIEVERS
}

class SetTargetActivity : BaseSecureActivity<SetTargetView>(), SetTargetView {

    @Inject
    internal lateinit var presenter: SetTargetPresenter

    private var binding: ActivityTargetBinding by lifecycleAwareVariable()

    lateinit var targetViews: List<TargetView>

    override fun getPresenter(): LifecycleAwarePresenter<SetTargetView>? = presenter

    private val mode: SetTargetMode by lazy { intent.getSerializableExtra(PARAM_MODE) as SetTargetMode }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalyticsHelper.sendViewScreenEvent(if (preferenceRepository.isUserLoggedIn) "NHSLoginSetYourTargets" else "SetYourTargets")

        setContentView(ActivityTargetBinding.inflate(layoutInflater).apply { binding = this }.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        setUpViews()
    }

    override fun onSendTargetCompleted() {
        if (mode == SetTargetMode.ONBOARDING) {
            preferenceRepository.dateOfOnboardFinished = LocalDate.now()
            startActivity(HomeActivity.getHomeIntent(this))
        } else {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.menu_item_close) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun setUpViews() {
        targetViews = listOf(
            binding.target1,
            binding.target2,
            binding.target3,
            binding.target4,
            binding.target5
        )

        binding.setTargetContinueBTN.apply {
            if (mode == SetTargetMode.HIGH_ACHIEVERS) setText(R.string.button_save)
            if (mode != SetTargetMode.ONBOARDING) {
                setBackgroundResource(R.drawable.teal_button_selector)
                setTextColor(ResourcesCompat.getColor(resources, R.color.white, null))
            }
            setOnClickListener {
                presenter.onContinue()
            }
        }

        setUpToolbar()

        if (mode == SetTargetMode.HIGH_ACHIEVERS) {
            binding.titleTV.text = getString(R.string.increase_target_title)
            binding.subtitleTV.text = getString(R.string.increase_target_subtitle)
        }
        binding.setTargetImage.isVisible = mode == SetTargetMode.ONBOARDING

        targetViews.forEachIndexed { idx, targetView ->
            targetView.targetId = idx + 1
            targetView.selectedTargetId = settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target ?: DEFAULT_TARGET
            targetView.setOnClickListener {
                updateTargetViews(targetView.targetId)
                presenter.onSelectTarget(targetView.targetId)
                it.announceForAccessibility(it.contentDescription)
            }
        }

        binding.targetContainer2.isVisible =
            (settingsUtils.getSettingsHolder().earnedHighAchieversBadgeTarget ?: 0) >= 3

        val lastSavedTarget = (settingsUtils.getSettingsHolder().targetList?.lastOrNull() ?: TargetHolder(
            DEFAULT_TARGET
        )).target

        setTargetSelectedTexts(lastSavedTarget ?: 0)

        ViewCompat.setAccessibilityHeading(binding.titleTV, true)

    }

    private fun setUpToolbar() {
        val isFromSettings = mode != SetTargetMode.ONBOARDING

        binding.setTargetToolbar.root.isVisible = isFromSettings
        binding.setTargetWhiteToolbar.root.isVisible = !isFromSettings

        binding.setTargetToolbar.titleTV.text = getString(R.string.settings_my_goals)

        binding.setTargetToolbar.backTV.setOnClickListener { onBackPressed() }
        binding.setTargetWhiteToolbar.backTV.setOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (mode == SetTargetMode.HIGH_ACHIEVERS) {
            menuInflater.inflate(R.menu.close_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateTargetViews(target: Int) {
        setTargetSelectedTexts(target)
        targetViews.forEach { targetView ->
            targetView.selectedTargetId = target
        }
    }

    private fun setTargetSelectedTexts(target: Int) {
        val targetMinutes = MINUTES_PER_TARGET * target
        if (target == NUMBER_OF_A10)
            binding.targetBenefitTV.text = UIUtils.getString(R.string.target3_active_10, target)
        else
            binding.targetBenefitTV.text =
                UIUtils.getString(R.string.target_set_benefit, targetMinutes)
        binding.targetActivesTv.text = UIUtils.getQuantityString(R.plurals.target_plural, target)
    }

}
