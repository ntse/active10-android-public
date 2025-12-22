package com.flipsidegroup.active10.presentation.onboarding.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.work.WorkManager
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.OnboardingNotificationEnum
import com.flipsidegroup.active10.data.models.api.NhsUserDetails
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.databinding.ActivityUserBinding
import com.flipsidegroup.active10.presentation.common.activities.BasePublicActivity
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.utils.Constants
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.WalkDataGenerator
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.setHeading
import com.flipsidegroup.active10.utils.worker.ApiWorker
import com.flipsidegroup.active10.utils.worker.WorkSchedulerHelper
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset

fun Context.UserActivity(onBoardingIntent: Intent? = null): Intent {
    val intent = Intent(this, UserActivity::class.java)
    if (onBoardingIntent != null) {
        intent.putExtra(Intent.EXTRA_INTENT, onBoardingIntent)
    }
    return intent
}

class UserActivity : BasePublicActivity<BaseView>() {

    private var binding: ActivityUserBinding by lifecycleAwareVariable()

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null

    private fun isFirstTimeInAppNotInit(): Boolean =
        settingsUtils.getSettingsHolder().firstTimeInApp == null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityUserBinding.inflate(layoutInflater).apply { binding = this }.root)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }

        setUpInitState()
        setUpViews()
    }

    private fun setUpViews() {
        checkUserMigrating()
        setUpDeviceId()
        setUpFirstInstallationView()
        binding.continueBTN.setOnClickListener {
            setUpNotificationsWorkers(binding.notificationsCB.isChecked)
            settingsUtils.updateSettings(
                SettingsDataHolder(
                    showMissingAlertSooner = binding.missingDataCB.isChecked,
                    shouldFailRetrieveDataTest = binding.failRetrieveDataCB.isChecked,
                    shouldShowUpdateAppDialog = binding.showUpdateAppDialogCB.isChecked,
                    shouldShowRetrieveDataDialog = binding.showRetrieveDataDialogCB.isChecked,
                    shouldShowUpdateTermsDialog = binding.showUpdateTermsDialogCB.isChecked,
                    lastTimeInApp = DateHelper.getCurrentTimestamp(),
                    generate1Active10WalkData = binding.generate2Weeks1Active10CB.isChecked,
                    generate5Active10WalkData = binding.generate2Weeks5Active10CB.isChecked,
                )
            )

            if (binding.paceCheckerResultStepsEdit.text.toString().toFloatOrNull() != null) {
                preferenceRepository.fakePaceCheckerSteps =
                    binding.paceCheckerResultStepsEdit.text.toString().toFloat()
            } else {
                preferenceRepository.fakePaceCheckerSteps = 0f
            }

            if (binding.paceCheckerExistingUser.isChecked) {
                preferenceRepository.isPaceCheckerNewUser = false
            }

            if (binding.resetWalkDataCB.isChecked) {
                resetWalkData()
            }

            setGenerateRandomWalkData(binding.generateDataCB.isChecked)
            generateHighAchieversWalkData(binding.generate2Weeks1Active10CB.isChecked || binding.generate2Weeks5Active10CB.isChecked)


            if (binding.showOnboardingCB.isChecked || isFirstTimeInAppNotInit()) {
                settingsUtils.updateSettings(SettingsDataHolder(firstTimeInApp = true))
                startIntroActivity(!binding.migratingUserCB.isChecked)
            } else {
                settingsUtils.updateSettings(SettingsDataHolder(firstTimeInApp = false))
                startHomeActivity()
            }
        }

        binding.mockedLoggedUserCB.setOnCheckedChangeListener { _, isChecked ->
            setMockedLoggedUser(isChecked)
        }

        binding.loginNotificationsTestCB.isChecked = preferenceRepository.isLocalNotificationsTest
        binding.loginNotificationsTestCB.setOnCheckedChangeListener { _, isChecked ->
            setLocalNotificationsTest(isChecked)
        }

        binding.useProdCmsCB.setOnCheckedChangeListener { _, isChecked ->
            setUseProdCMS(isChecked)
            WorkManager.getInstance(this)
                .enqueue(ApiWorker.getRequest())
        }

        binding.generate2Weeks1Active10CB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.generate2Weeks5Active10CB.isChecked = false
            }
        }
        binding.generate2Weeks5Active10CB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.generate2Weeks1Active10CB.isChecked = false
            }
        }

        binding.set30secInstead1minCB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.set30secInstead5minCB.isChecked = false
                binding.set30secInstead30minCB.isChecked = false
                binding.set30secInstead12hCB.isChecked = false
                preferenceRepository.set30SecForAuthTest = "1min"
            } else {
                preferenceRepository.set30SecForAuthTest = null
            }
        }

        binding.set30secInstead5minCB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.set30secInstead1minCB.isChecked = false
                binding.set30secInstead30minCB.isChecked = false
                binding.set30secInstead12hCB.isChecked = false
                preferenceRepository.set30SecForAuthTest = "5min"
            } else {
                preferenceRepository.set30SecForAuthTest = null
            }
        }

        binding.set30secInstead30minCB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.set30secInstead1minCB.isChecked = false
                binding.set30secInstead5minCB.isChecked = false
                binding.set30secInstead12hCB.isChecked = false
                preferenceRepository.set30SecForAuthTest = "30min"
            } else {
                preferenceRepository.set30SecForAuthTest = null
            }
        }

        binding.set30secInstead12hCB.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.set30secInstead1minCB.isChecked = false
                binding.set30secInstead5minCB.isChecked = false
                binding.set30secInstead30minCB.isChecked = false
                preferenceRepository.set30SecForAuthTest = "12h"
            } else {
                preferenceRepository.set30SecForAuthTest = null
            }
        }

        binding.optionMessageTv.setHeading()

        setupAgeSelector()
        setupGenderSelector()
    }

    private fun setUpInitState() {
        preferenceRepository.set30SecForAuthTest = null
    }

    private fun setUpFirstInstallationView() {
        if (isFirstTimeInAppNotInit()) {
            binding.resetWalkDataCB.isVisible = true
        } else {
            binding.resetWalkDataCB.isVisible = false
            val resetTimestamp: Long? = settingsUtils.getSettingsHolder().resetTimestamp
            if (resetTimestamp != null) {
                binding.resetWalkDataTv.isVisible = true
                binding.resetWalkDataTv.text =
                    UIUtils.getString(
                        R.string.ignoring_walks_to_time,
                        DateHelper.getReminderTime(resetTimestamp)
                    )
            }
        }
    }

    private fun checkUserMigrating() {
        val isMigrating = UIUtils.checkIfFileExists(Constants.MIGRATION_FILE_NAME)
        binding.migratingUserCB.isChecked = isMigrating
    }

    private fun setGenerateRandomWalkData(generateData: Boolean) {
        val lastGenerateSettings = settingsUtils.getSettingsHolder().generateRandomWalkData
        if (generateData || lastGenerateSettings == true && !generateData) {
            settingsUtils.updateSettings(SettingsDataHolder(earnedBadges = arrayListOf()))
            settingsUtils.updateSettings(SettingsDataHolder(readArticles = arrayListOf()))
        }
        settingsUtils.updateSettings(SettingsDataHolder(generateRandomWalkData = generateData))
    }

    private fun generateHighAchieversWalkData(generateData: Boolean) {
        if (generateData) {
            WalkDataGenerator.setMinimumActive10Walks(
                when {
                    binding.generate2Weeks5Active10CB.isChecked -> 5
                    binding.generate2Weeks1Active10CB.isChecked -> 1
                    else -> 0
                }
            )
        }
    }

    private fun setMockedLoggedUser(useMockedLoggedUser: Boolean) {
        val mockedUser = if (useMockedLoggedUser) NhsUserDetails(
            id = "3",
            firstName = "Mocked Hubert",
            email = "testuserlive+1@demo.signin.nhs.uk",
            postcode = "PL1 1DE",
            isEmailUpdatesAllowed = false,
            gender = "female",
            age = 28,
            ageRange = "25 to 34"
        ) else null
        settingsUtils.updateSettings(SettingsDataHolder(nhsUser = mockedUser))
        preferenceRepository.isUserLoggedIn = useMockedLoggedUser
    }

    private fun setLocalNotificationsTest(isCustomTime: Boolean) {
        preferenceRepository.isLocalNotificationsTest = isCustomTime
    }

    private fun setUseProdCMS(useProdCMS: Boolean) {
        settingsUtils.updateSettings(SettingsDataHolder(useProdCMS = useProdCMS))
    }

    private fun setUpNotificationsWorkers(isNotificationMin: Boolean) {
        settingsUtils.updateSettings(SettingsDataHolder(notificationMin = isNotificationMin))

        if (isNotificationMin || isFirstTimeInAppNotInit()) {
            WorkSchedulerHelper.scheduleOnboardingNotification(
                OnboardingNotificationEnum.DAY_3.timeSinceInstallation,
                isNotificationMin
            )
            settingsUtils.updateSettings(SettingsDataHolder(lapsedCount = 0))
            settingsUtils.updateSettings(SettingsDataHolder(nextOnboardingNotification = 0))
        }
    }

    private fun resetWalkData() {
        settingsUtils.updateSettings(SettingsDataHolder(resetTimestamp = DateHelper.getCurrentTimestamp()))
    }

    private fun setupAgeSelector() {
        val isNhsUser = settingsUtils.getSettingsHolder().nhsUser != null && preferenceRepository.isUserLoggedIn
        binding.ageRadioGroup.isEnabled = isNhsUser
        binding.ageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedAge = when (checkedId) {
                R.id.age_20 -> "18 to 24"
                R.id.age_30 -> "25 to 34"
                R.id.age_40 -> "35 to 44"
                R.id.age_50 -> "45 to 54"
                R.id.age_60 -> "55 to 64"
                R.id.age_70 -> "65 or over"
                else -> return@setOnCheckedChangeListener
            }
            val nhsUser = settingsUtils.getSettingsHolder().nhsUser
            settingsUtils.updateSettings(SettingsDataHolder(nhsUser = nhsUser?.copy(ageRange = selectedAge)))
            nhsUser?.let { Toast.makeText(this, "Age changed", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun setupGenderSelector() {
        val isNhsUser = settingsUtils.getSettingsHolder().nhsUser != null && preferenceRepository.isUserLoggedIn
        binding.genderRadioGroup.isEnabled = isNhsUser
        binding.genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedGender = when (checkedId) {
                R.id.gender_male -> "Male"
                R.id.gender_female -> "Female"
                else -> return@setOnCheckedChangeListener
            }
            val nhsUser = settingsUtils.getSettingsHolder().nhsUser
            settingsUtils.updateSettings(SettingsDataHolder(nhsUser = nhsUser?.copy(gender = selectedGender)))
            nhsUser?.let { Toast.makeText(this, "Gender changed", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun startHomeActivity() {
        if (intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT) != null) {
            startActivity(intent.getParcelableExtra(Intent.EXTRA_INTENT))
        } else {
            startActivity(HomeActivity.getHomeIntent(this))
            finish()
        }
    }

    private fun startIntroActivity(isNewUser: Boolean) {
        startActivity(IntroIntent(isNewUser))
        finish()
    }

    private fun setUpDeviceId() {
        val savedDeviceId = settingsUtils.getSettingsHolder().deviceId
        savedDeviceId?.let {
            binding.deviceIdTV.text = it
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        binding.optionMessageTv.post {
            binding.optionMessageTv.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED)
        }
    }
}
