package com.flipsidegroup.active10.presentation.onboarding.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.afollestad.materialdialogs.MaterialDialog
import com.flipside.briskcounter.BriskCounter
import com.flipside.briskcounter.internal.Error
import com.flipside.briskcounter.internal.InitListener
import com.flipside.briskcounter.internal.State
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.Onboarding
import com.flipsidegroup.active10.data.PermissionEnum
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.databinding.ActivityPermissionBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.goals.fragments.GoalsFragment
import com.flipsidegroup.active10.presentation.onboarding.adapters.PermissionAdapter
import com.flipsidegroup.active10.presentation.onboarding.interfaces.GoalListener
import com.flipsidegroup.active10.presentation.onboarding.presenter.PermissionPresenter
import com.flipsidegroup.active10.presentation.onboarding.view.PermissionView
import com.flipsidegroup.active10.services.MigrationService
import com.flipsidegroup.active10.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

fun Context.PermissionActivity() = Intent(this, PermissionActivity::class.java)

private const val ACTIVITY_RECOGNITION_PERMISSION_REQUEST = 2
private const val NOTIFICATION_PERMISSION_REQUEST = 3
const val GOAL_FRAGMENT_POSITION = 2
const val GOALS_KEY = "goal"

class PermissionActivity : BaseSecureActivity<PermissionView>(), GoalListener, InitListener,
    PermissionView {

    private var permissionAdapter: PermissionAdapter? = null

    private var goals = ArrayList<Goal>()

    lateinit var start: ActivityResultLauncher<Intent>

    @Inject
    internal lateinit var presenter: PermissionPresenter

    private var binding: ActivityPermissionBinding by lifecycleAwareVariable()

    val permissionsParentLayout: ConstraintLayout
        get() = binding.permissionsParentLayout

    override fun getPresenter(): LifecycleAwarePresenter<PermissionView> = presenter

    override fun onDestroy() {
        super.onDestroy()
        BriskCounter.removeInitListener()
    }

    override fun onResume() {
        super.onResume()
        BriskCounter.addInitListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityPermissionBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        presenter.getOnboarding()

        binding.permissionToolbar.backTV.setOnClickListener { onBackPressed() }
        binding.permissionToolbar.root.setIsVisible(false)

        setUpViews()
    }

    override fun onStart() {
        super.onStart()
        start =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val goalsIdItem = getGoalsFragmentId() ?: return@registerForActivityResult
                    val currentFragment =
                        (supportFragmentManager.fragments[goalsIdItem] as GoalsFragment)
                    currentFragment.updateCurrentGoals(
                        result.data?.extras?.getString(GOALS_KEY)
                            ?: return@registerForActivityResult
                    )
                }
            }
    }

    private fun getGoalsFragmentId(): Int? = permissionAdapter?.getItemTypes()
        ?.indexOfFirst { it == PermissionEnum.GOAL_LIST }

    override fun onSendGoalsCompleted() {
        binding.continueBTN.isEnabled = true
        settingsUtils.updateSettings(SettingsDataHolder(isOnboardingFinished = true))
        hideLoading()
    }

    override fun onOnboardingReceived(onboarding: Onboarding?) {
        permissionAdapter = PermissionAdapter(
            supportFragmentManager,
            onboarding
        )
        binding.permissionVP.offscreenPageLimit = PermissionEnum.values().count()
        binding.permissionVP.adapter = permissionAdapter
    }

    override fun onSendGoalsError() {
        binding.continueBTN.isEnabled = true
    }

    override fun onFailure(error: Error) {
        AlertHelper.showErrorToast(getString(R.string.brisk_counter_failure_user_message))

        FirebaseCrashlytics.getInstance().log(error.name)
        FirebaseCrashlytics.getInstance()
            .recordException(RuntimeException(Constants.FirebaseAnalytics.INITIALIZE_BRISK_COUNTER_FAILURE))
    }

    override fun onSuccess(state: State) {
        settingsUtils.updateSettings(SettingsDataHolder(isFitnessMotionEnabled = true))
        startMigrationService()
        goToNextScreen()
    }

    override fun onGoalChangeListener(goalsList: List<Goal>) {
        goals.clear()
        goals.addAll(goalsList)

        setButtonState()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ACTIVITY_RECOGNITION_PERMISSION_REQUEST -> {
                val isPermissionGranted =
                    grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (isPermissionGranted) {
                    BriskCounter.initialize(this@PermissionActivity as InitListener)
                }
            }

            NOTIFICATION_PERMISSION_REQUEST -> {
                val isPermissionGranted =
                    grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (isPermissionGranted) {
                    preferenceRepository.isNotificationsEnabled = true
                    goToNextScreen()
                }
            }
        }
    }

    override fun onBackPressed() {
        val currentItem = binding.permissionVP.currentItem
        closeKeyboard()
        if (currentItem == 0) {
            super.onBackPressed()
        } else {
            goToPreviousScreen()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                goToPreviousScreen()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_RECOGNITION_PERMISSION_REQUEST) {
            if (hasPermissions(Manifest.permission.ACTIVITY_RECOGNITION)) {
                BriskCounter.initialize(this@PermissionActivity as InitListener)
            }
        } else {
            BriskCounter.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun startMigrationService() {
        val isDeviceRegistered = settingsUtils.getSettingsHolder().isDeviceRegistered
        if (isDeviceRegistered != true) {
            return
        }

        val isMigrationDataSent = settingsUtils.getSettingsHolder().isMigrationDataSent
        if (isMigrationDataSent == true) {
            return
        }

        MigrationService.enqueueWork(this, MigrationService())
    }

    private fun showGoalsButtons() {
        binding.continueBTN.background =
            ContextCompat.getDrawable(this, R.drawable.secondary_button_selector)
        binding.noThanksBTN.background = ContextCompat.getDrawable(this, R.drawable.teal_button_selector)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.continueBTN.setTextAppearance(R.style.SecondaryButtonLarge)
            binding.noThanksBTN.setTextAppearance(R.style.TealButtonLarge)
        } else {
            binding.continueBTN.setTextAppearance(this, R.style.SecondaryButtonLarge)
            binding.noThanksBTN.setTextAppearance(this, R.style.TealButtonLarge)
        }
        binding.noThanksBTN.setText(R.string.save_and_continue)
    }

    private fun showOnboardingButtons() {
        binding.noThanksBTN.background =
            ContextCompat.getDrawable(this, R.drawable.secondary_button_selector)
        binding.continueBTN.background = ContextCompat.getDrawable(this, R.drawable.teal_button_selector)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.noThanksBTN.setTextAppearance(R.style.SecondaryButtonLarge)
            binding.continueBTN.setTextAppearance(R.style.TealButtonLarge)
        } else {
            binding.noThanksBTN.setTextAppearance(this, R.style.SecondaryButtonLarge)
            binding.continueBTN.setTextAppearance(this, R.style.TealButtonLarge)
        }
        binding.noThanksBTN.setText(R.string.button_no_thanks)
        binding.noThanksBTN.isEnabled = true
    }

    private fun setUpViews() {
        settingsUtils.getSettingsHolder().goalsList?.let {
            goals.addAll(it)
        }
        binding.permissionVP.swipeEnabled = false

        binding.permissionVP.addOnPageChangeListener(OnPageChangeListener {
            val permissionEnum =
                permissionAdapter?.getPermissionTypeForPosition(it) ?: return@OnPageChangeListener

            if (permissionEnum == PermissionEnum.FITNESS_PERMISSION) {
                binding.continueBTNFitness.isVisible = true
                binding.continueBTN.isVisible = false
            } else {
                binding.continueBTN.isVisible = true
                binding.continueBTNFitness.isVisible = false
            }

            if (permissionEnum.buttonCTA != 0) {
                binding.continueBTN.text = getString(permissionEnum.buttonCTA)
                binding.buttonContainerCL.visibility = View.VISIBLE
            } else {
                binding.buttonContainerCL.visibility = View.GONE
            }

            if (permissionEnum == PermissionEnum.GOAL_LIST) {
                setButtonState()
            } else {
                binding.continueBTN.isEnabled = true
            }
            binding.noThanksBTN.visibility = permissionEnum.declineBtnVisibility
            binding.permissionToolbar.root.setIsVisible(permissionEnum.toolbarVisibility)
            if (permissionEnum.reverseButtons) {
                showGoalsButtons()
            } else {
                showOnboardingButtons()
            }
        })

        binding.continueBTNFitness.setOnClickListener {
            if (hasInternetConnection()) {
                showFitnessDialog()
            } else {
                showNoInternetDialog()
            }
        }

        binding.continueBTN.setOnClickListener {
            permissionAdapter?.getPermissionTypeForPosition(binding.permissionVP.currentItem)?.let {
                when (it) {
                    PermissionEnum.FITNESS_PERMISSION -> if (hasInternetConnection()) {
                        showFitnessDialog()
                    } else {
                        showNoInternetDialog()
                    }

                    PermissionEnum.NOTIFICATIONS_PERMISSION -> {
                        handleNotificationPermission()
                    }

                    PermissionEnum.GOAL_LIST -> {
                        start.launch(CustomGoalsIntent(false))
                    }

                    PermissionEnum.USER_DETAILS -> goToNextScreen()
                }
            }
        }

        binding.noThanksBTN.setOnClickListener {
            val settingsDataHolder =
                permissionAdapter?.getPermissionTypeForPosition(binding.permissionVP.currentItem)?.let {
                    when (it) {
                        PermissionEnum.FITNESS_PERMISSION -> SettingsDataHolder(
                            isFitnessMotionEnabled = false
                        )

                        PermissionEnum.NOTIFICATIONS_PERMISSION -> {
                            preferenceRepository.isNotificationsEnabled = false
                            goToNextScreen()
                            return@setOnClickListener
                        }

                        PermissionEnum.GOAL_LIST -> {
                            goals.removeAll { !it.isSelected && it.isCustomGoal }
                            settingsUtils.updateSettings(SettingsDataHolder(goalsList = goals))
                            EarnBadgeHelper.saveEarnedBadge(
                                settingsUtils = settingsUtils,
                                badge = RewardBadgeEnum.GOAL_SETTER,
                                preferenceRepository = preferenceRepository
                            )
                            presenter.sendGoals()
                            goToNextScreen()
                            return@setOnClickListener
                        }

                        PermissionEnum.USER_DETAILS -> return@setOnClickListener
                    }
                }
            settingsDataHolder?.let {
                settingsUtils.updateSettings(settingsDataHolder)
                goToNextScreen()
            }
        }
    }

    private fun setButtonState() {
        val goalsIdItem = getGoalsFragmentId() ?: return
        if (binding.permissionVP.currentItem == goalsIdItem) {
            binding.noThanksBTN.isEnabled = goals.any { it.isSelected }
        } else {
            binding.continueBTN.isEnabled = goals.any { it.isSelected }
        }
    }

    @SuppressLint("InlinedApi")
    private fun handleNotificationPermission() {
        if (!hasPermissions(Manifest.permission.POST_NOTIFICATIONS)) {
            ActivityCompat.requestPermissions(
                this@PermissionActivity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST
            )
        } else {
            preferenceRepository.isNotificationsEnabled = true
            goToNextScreen()
        }
    }

    private fun goToNextScreen() {
        val currentItem = binding.permissionVP.currentItem
        binding.permissionVP.setCurrentItem(currentItem + 1, true)
    }

    private fun goToPreviousScreen() {
        val currentItem = binding.permissionVP.currentItem
        binding.permissionVP.setCurrentItem(currentItem - 1, true)
    }

    private fun showFitnessDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_motion_fitness_permission_title)
            .setMessage(R.string.dialog_motion_fitness_permission_subtitle)
            .setPositiveButton(R.string.button_confirm) { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasPermissions(
                        Manifest.permission.ACTIVITY_RECOGNITION
                    )
                ) {
                    if (settingsUtils.getSettingsHolder().wasAskedMotionPermission == null ||
                        shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)
                    ) {
                        settingsUtils.updateSettings(SettingsDataHolder(wasAskedMotionPermission = true))
                        ActivityCompat.requestPermissions(
                            this@PermissionActivity,
                            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                            ACTIVITY_RECOGNITION_PERMISSION_REQUEST
                        )
                    } else {
                        goToSettingsPermission()
                    }
                } else {
                    BriskCounter.initialize(this@PermissionActivity as InitListener)
                }
            }
            .setNegativeButton(R.string.button_decline) { _, _ -> }
            .show()
    }

    private fun goToSettingsPermission() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, ACTIVITY_RECOGNITION_PERMISSION_REQUEST)
    }

    private fun showNoInternetDialog() {
        MaterialDialog(this).show {
            title(R.string.dialog_no_internet_title)
            message(R.string.dialog_no_internet_subtitle)
            positiveButton(R.string.button_ok)
        }
    }
}
