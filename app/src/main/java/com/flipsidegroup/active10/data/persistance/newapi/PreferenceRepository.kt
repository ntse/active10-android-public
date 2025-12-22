package com.flipsidegroup.active10.data.persistance.newapi

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.flipsidegroup.active10.utils.Crypto
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceRepository @Inject constructor(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences("active_10_preferences", Context.MODE_PRIVATE)

    var countOfReadArticles
        get() = sharedPreferences.getInt("count_of_read_articles", 0)
        set(value) = sharedPreferences.edit { putInt("count_of_read_articles", value) }

    var isAnimationEnabled
        get() = sharedPreferences.getBoolean("is_animation_enabled", true)
        set(value) = sharedPreferences.edit(commit = true) {
            putBoolean("is_animation_enabled", value)
        }

    var isNotificationsEnabled
        get() = sharedPreferences.getBoolean("is_notifications_enabled", false)
        set(value) = sharedPreferences.edit { putBoolean("is_notifications_enabled", value) }

    var isPaceCheckerTooltipShown
        get() = sharedPreferences.getBoolean("is_pace_checker_tooltip_shown", false)
        set(value) = sharedPreferences.edit { putBoolean("is_pace_checker_tooltip_shown", value) }

    var isPaceCheckerOpened
        get() = sharedPreferences.getBoolean("is_pace_checker_opened", false)
        set(value) = sharedPreferences.edit { putBoolean("is_pace_checker_opened", value) }

    var dateOfOnboardFinished: LocalDate?
        get() = sharedPreferences.getString("date_of_onboard_finished", null)
            ?.let { LocalDate.parse(it) }
        set(value) = sharedPreferences.edit {
            putString(
                "date_of_onboard_finished",
                value?.toString()
            )
        }

    var isPaceCheckerBannerDismissed: Boolean
        get() = sharedPreferences.getBoolean("is_pace_checker_banner_dismissed", false)
        set(value) = sharedPreferences.edit {
            putBoolean(
                "is_pace_checker_banner_dismissed",
                value
            )
        }

    var isPaceCheckerNewUser: Boolean
        get() = sharedPreferences.getBoolean("is_pace_checker_new_user", false)
        set(value) = sharedPreferences.edit { putBoolean("is_pace_checker_new_user", value) }

    var daysHitTargetInLastTwoWeeks: Int
        get() = sharedPreferences.getInt("days_hit_target_in_last_two_weeks", 0)
        set(value) = sharedPreferences.edit { putInt("days_hit_target_in_last_two_weeks", value) }

    var fakePaceCheckerSteps: Float
        get() = sharedPreferences.getFloat("fake_pace_checker_steps", 0f)
        set(value) = sharedPreferences.edit { putFloat("fake_pace_checker_steps", value) }

    var isPaceCheckerIntroDismissed: Boolean
        get() = sharedPreferences.getBoolean("is_pace_checker_intro_dismissed", false)
        set(value) = sharedPreferences.edit { putBoolean("is_pace_checker_intro_dismissed", value) }

    var isUserLoggedIn: Boolean
        get() = sharedPreferences.getBoolean("is_user_logged_in", false)
        set(value) = sharedPreferences.edit { putBoolean("is_user_logged_in", value) }

    var showLogoutPopupAttemptsCount: Int
        get() = sharedPreferences.getInt("show_logout_popup_attempts_count", 0)
        set(value) = sharedPreferences.edit { putInt("show_logout_popup_attempts_count", value) }

    var coachAppIntroducedDate: LocalDate?
        get() = sharedPreferences.getString("coach_app_introduced_date", null)
            ?.let { LocalDate.parse(it) }
        set(value) = sharedPreferences.edit {
            putString(
                "coach_app_introduced_date",
                value?.toString()
            )
        }

    var coachAppSmashingItAnswer: String?
        get() = sharedPreferences.getString("coach_app_smashing_it_answer", null)
        set(value) = sharedPreferences.edit {
            putString(
                "coach_app_smashing_it_answer",
                value
            )
        }

    var coachAppDownloadAppAnswer: String?
        get() = sharedPreferences.getString("coach_app_download_app_answer", null)
        set(value) = sharedPreferences.edit {
            putString(
                "coach_app_download_app_answer",
                value
            )
        }


    var integrityCheckResult: Boolean
        get() = sharedPreferences.getBoolean("integrity_check_result", true)
        set(value) = sharedPreferences.edit { putBoolean("integrity_check_result", value) }

    var integrityCheckTimestamp: Long
        get() = sharedPreferences.getLong("integrity_check_timestamp", 0)
        set(value) = sharedPreferences.edit { putLong("integrity_check_timestamp", value) }

    var showMyWalksTooltips: Boolean
        get() = sharedPreferences.getBoolean("show_my_walks_tooltips", true)
        set(value) = sharedPreferences.edit { putBoolean("show_my_walks_tooltips", value) }

    var showMyWalksNewFeaturesBanner: Boolean
        get() = sharedPreferences.getBoolean("show_my_walks_new_features_banner", true)
        set(value) = sharedPreferences.edit {
            putBoolean(
                "show_my_walks_new_features_banner",
                value
            )
        }

    var lastWeekWithShownHitPopup: Int
        get() = sharedPreferences.getInt("last_week_with_shown_hit_popup", -1)
        set(value) = sharedPreferences.edit { putInt("last_week_with_shown_hit_popup", value) }

    var lastWeekWithShownExceededPopup: Int
        get() = sharedPreferences.getInt("last_week_with_shown_exceeded_popup", -1)
        set(value) = sharedPreferences.edit { putInt("last_week_with_shown_exceeded_popup", value) }

    var lastWeekWithShownMissedPopup: Int
        get() = sharedPreferences.getInt("last_week_with_shown_missed_popup", -1)
        set(value) = sharedPreferences.edit { putInt("last_week_with_shown_missed_popup", value) }

    var isUserFirstTimeInAppNotified: Boolean
        get() = sharedPreferences.getBoolean("is_user_first_time_in_app_notified", false)
        set(value) = sharedPreferences.edit { putBoolean("is_user_first_time_in_app_notified", value) }

    var isUserNotSelectedPlanNotified: Boolean
        get() = sharedPreferences.getBoolean("is_user_not_selected_plan_notified", false)
        set(value) = sharedPreferences.edit { putBoolean("is_user_not_selected_plan_notified", value) }

    var showMWPCompletedNotification: Boolean
        get() = sharedPreferences.getBoolean("show_mwp_completed_notification", false)
        set(value) = sharedPreferences.edit { putBoolean("show_mwp_completed_notification", value) }

    var isLocalNotificationsTest: Boolean
        get() = sharedPreferences.getBoolean("is_local_notifications_test", false)
        set(value) = sharedPreferences.edit { putBoolean("is_local_notifications_test", value) }

    var authPinCode: String?
        get() = sharedPreferences.getString("auth_pin_code", null)?.let {
                Crypto.decryptedString(it)
        }
        set(value) = sharedPreferences.edit { putString("auth_pin_code", value?.let {
                Crypto.encryptedString(it)
            })
        }

    var isBiometricAllowed: Boolean
        get() = sharedPreferences.getBoolean("is_biometric_allowed", false)
        set(value) = sharedPreferences.edit { putBoolean("is_biometric_allowed", value) }

    var set30SecForAuthTest: String?
        get() = sharedPreferences.getString("set_30_sec_for_auth_test", null)
        set(value) = sharedPreferences.edit { putString("set_30_sec_for_auth_test", value) }

    var isOnboardingCompleted: Boolean
        get() = sharedPreferences.getBoolean("is_onboarding_completed", false)
        set(value) = sharedPreferences.edit { putBoolean("is_onboarding_completed", value) }

    var showLogoutPopup: Boolean
        get() = sharedPreferences.getBoolean("show_logout_popup", false)
        set(value) = sharedPreferences.edit { putBoolean("show_logout_popup", value) }

    var continuousUsageNhsTime: Long
        get() = sharedPreferences.getLong("continuous_usage_nhs_time", 0L)
        set(value) = sharedPreferences.edit { putLong("continuous_usage_nhs_time", value) }

    var logoutNhsUserAuthFlag: Boolean
        get() = sharedPreferences.getBoolean("logout_nhs_user_auth_flag", false)
        set(value) = sharedPreferences.edit { putBoolean("logout_nhs_user_auth_flag", value) }

}
