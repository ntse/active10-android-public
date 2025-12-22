package com.flipsidegroup.active10.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.flipside.briskcounter.BriskCounter
import com.flipside.briskcounter.internal.InitListener
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.GlobalRules
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.dialogs.CommonBottomSheetDialog
import com.flipsidegroup.active10.presentation.dialogs.DialogButtonModel
import com.flipsidegroup.active10.presentation.dialogs.TermsConditionsDialog
import com.flipsidegroup.active10.presentation.dialogs.UpdateAppDialog
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper

interface DialogUtils {
    fun showTermAndConditionsDialog(activity: FragmentActivity, globalRules: GlobalRules?)

    fun showUpdateAppDialog(activity: FragmentActivity, globalRules: GlobalRules?)

    fun showNewRewardBadgesDialog(activity: FragmentActivity)

    fun showFitnessDialog(context: Activity)
}

class DialogUtilsImpl(
    val settingsUtils: SettingsUtils,
    val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
    private val preferenceRepository: PreferenceRepository
) : DialogUtils {

    companion object {
        private const val DEFAULT_TERMS_AND_CONDITIONS_VERSION_DEBUG = 0
        private const val DEFAULT_TERMS_AND_CONDITIONS_VERSION = 1

        private const val ACTIVITY_RECOGNITION_PERMISSION_REQUEST = 3
    }

    override fun showTermAndConditionsDialog(
        activity: FragmentActivity,
        globalRules: GlobalRules?
    ) {
        globalRules?.let { rules ->
            val defaultTermsAndConditionsVersion = if (BuildConfig.DEBUG) {
                DEFAULT_TERMS_AND_CONDITIONS_VERSION_DEBUG
            } else {
                DEFAULT_TERMS_AND_CONDITIONS_VERSION
            }
            val defaultTcVersion = if (BuildConfig.DEBUG) {
                DEFAULT_TERMS_AND_CONDITIONS_VERSION_DEBUG
            } else {
                DEFAULT_TERMS_AND_CONDITIONS_VERSION
            }
            val termsAndConditionsVersion =
                settingsUtils.getSettingsHolder().termsAndConditionsVersion
                    ?: defaultTermsAndConditionsVersion
            val termsAndConditionsLatestVersion =
                if (rules.termsAndConditions?.latestVersion != "null")
                    rules.termsAndConditions?.latestVersion?.toInt() ?: defaultTcVersion
                else
                    defaultTcVersion

            if (termsAndConditionsVersion >= termsAndConditionsLatestVersion
                && settingsUtils.getSettingsHolder().shouldShowUpdateTermsDialog != true
            ) return

            TermsConditionsDialog(rules) {
                settingsUtils.updateSettings(SettingsDataHolder(termsAndConditionsVersion = termsAndConditionsLatestVersion))
            }.show(activity.supportFragmentManager, null)
        }
    }

    override fun showUpdateAppDialog(activity: FragmentActivity, globalRules: GlobalRules?) {
        globalRules?.let {
            val appVersionVersion =
                BuildConfig.VERSION_NAME
            val appLatestVersion = it.app?.android?.latestVersion ?: ""
            val appUpdateLastTimestamp = settingsUtils.getSettingsHolder().appUpdateLastTimestamp

            val isUpdated =
                VersionHelper.isCurrentVersionUpdated(appVersionVersion, appLatestVersion)
                        || (appUpdateLastTimestamp != null && DateHelper.isSameDay(
                    appUpdateLastTimestamp
                ))

            if (isUpdated && settingsUtils.getSettingsHolder().shouldShowUpdateAppDialog != true) {
                return
            }

            settingsUtils.updateSettings(SettingsDataHolder(appUpdateLastTimestamp = System.currentTimeMillis()))

            firebaseAnalyticsHelper.saveEvent(Constants.FirebaseAnalytics.EVENT_APP_UPDATE)

            UpdateAppDialog { isUpdateConfirmed ->
                if (isUpdateConfirmed) {
                    firebaseAnalyticsHelper.saveEvent(Constants.FirebaseAnalytics.EVENT_APP_UPDATE_CONFIRMED)
                } else {
                    firebaseAnalyticsHelper.saveEvent(Constants.FirebaseAnalytics.EVENT_APP_UPDATE_CANCELLED)
                }
            }.show(activity.supportFragmentManager, null)

        }
    }

    override fun showNewRewardBadgesDialog(activity: FragmentActivity) {
        settingsUtils.updateSettings(SettingsDataHolder(shouldShowRewardOnboarding = false))
        val goal = settingsUtils.getSettingsHolder().goalsList?.firstOrNull()
        val earningsBadges = settingsUtils.getSettingsHolder().earnedBadges ?: arrayListOf()
        if (goal != null && earningsBadges.find { it.id == RewardBadgeEnum.GOAL_SETTER.id } == null) {
            EarnBadgeHelper.saveEarnedBadgeWithoutCheck(
                settingsUtils = settingsUtils,
                preferenceRepository = preferenceRepository,
                badge = RewardBadgeEnum.GOAL_SETTER,
                timestamp = DateHelper.getInstalledDate(activity).timeInMillis
            )
        }

        CommonBottomSheetDialog(
            icon = R.drawable.ic_goal_setter_badge,
            title = R.string.new_reward_badge_title,
            subtitle = R.string.new_reward_badge_message,
            primaryButton = DialogButtonModel(R.string.lets_go)
        ).show(activity.supportFragmentManager, null)
    }

    override fun showFitnessDialog(context: Activity) {
        MaterialDialog(context).show {
            title(R.string.dialog_motion_fitness_permission_title)
            message(R.string.dialog_motion_fitness_permission_subtitle)
            positiveButton(R.string.button_confirm) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q &&
                    !context.hasPermissions(Manifest.permission.ACTIVITY_RECOGNITION)
                ) {
                    if (settingsUtils.getSettingsHolder().wasAskedMotionPermission == null ||
                        context.shouldShowPermission(Manifest.permission.ACTIVITY_RECOGNITION)
                    ) {
                        settingsUtils.updateSettings(SettingsDataHolder(wasAskedMotionPermission = true))
                        ActivityCompat.requestPermissions(
                            context,
                            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                            ACTIVITY_RECOGNITION_PERMISSION_REQUEST
                        )
                    } else {
                        goToSettingsPermission(context)
                    }
                } else {
                    BriskCounter.initialize(context as InitListener)
                }

            }
            negativeButton(R.string.button_decline)
        }
    }

    private fun goToSettingsPermission(context: Activity) {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        context.startActivityForResult(intent, ACTIVITY_RECOGNITION_PERMISSION_REQUEST)
    }
}
