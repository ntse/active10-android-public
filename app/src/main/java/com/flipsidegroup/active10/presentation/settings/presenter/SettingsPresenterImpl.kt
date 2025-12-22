package com.flipsidegroup.active10.presentation.settings.presenter

import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.ActivityLevelEnum
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.persistance.newapi.WalkingPlanRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.onboarding.fragments.ACCESSIBILITY
import com.flipsidegroup.active10.presentation.onboarding.fragments.PRIVACY_POLICY
import com.flipsidegroup.active10.presentation.onboarding.fragments.TERMS_AND_CONDITIONS
import com.flipsidegroup.active10.presentation.settings.fragments.SettingsView
import com.flipsidegroup.active10.presentation.targets.activities.DEFAULT_TARGET
import com.flipsidegroup.active10.presentation.usecases.ChangePlanStateUseCase
import com.flipsidegroup.active10.presentation.usecases.RemoveNhsUserDataUseCase
import com.flipsidegroup.active10.utils.DateHelper
import com.flipsidegroup.active10.utils.EarnBadgeHelper
import com.flipsidegroup.active10.utils.UIUtils
import com.flipsidegroup.active10.utils.WalkingPlanState
import com.phe.betterhealth.components.settings.SettingsItem
import com.phe.betterhealth.components.settings.SettingsMenuItem
import com.phe.betterhealth.components.settings.SettingsMenuSwitchItem
import com.phe.betterhealth.components.settings.SettingsSection
import com.phe.betterhealth.components.settings.SettingsVersion
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import java.util.Calendar
import java.util.Locale

private const val DEFAULT_REMINDER_HOUR = 8

class SettingsPresenterImpl(
    private val settingsUtils: SettingsUtils,
    private val preferenceRepository: PreferenceRepository,
    private val screenRepository: ScreenRepository,
    private val walkingPlanRepository: WalkingPlanRepository,
    private val loginRepository: LoginRepository,
    private val changePlanStateUseCase: ChangePlanStateUseCase,
    private val removeNhsUserDataUseCase: RemoveNhsUserDataUseCase,
) : BasePresenter<SettingsView>(), SettingsPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun generateSettingsItemList() {
        view?.updateListItems(buildList {
            addAll(buildMyAccountSection())
            addAll(buildMyPreferencesSection())
            addAll(buildNotificationsSection())
            addAll(buildHelpSection())
            addAll(buildCommunitySection())
            addAll(buildAppInfoSection())
            addAll(buildAppVersionInfoSection())
        })
    }

    private fun buildMyAccountSection(): List<SettingsItem> {
        return when {
            preferenceRepository.isUserLoggedIn -> buildLoggedInUserMyAccountSection()

            !settingsUtils.getSettingsHolder().nhsLastSyncUserId.isNullOrBlank() ->
                buildNotLoggedUserMyAccountSection(returningUser = true)

            else -> buildNotLoggedUserMyAccountSection(returningUser = false)
        }
    }

    private fun buildNotLoggedUserMyAccountSection(returningUser: Boolean): List<SettingsItem> {
        return listOf(
            SettingsSection(
                10, UIUtils.getString(R.string.settings_my_account_title)
            ),
            SettingsMenuItem(
                id = 11,
                name = UIUtils.getString(if (returningUser) R.string.settings_log_in else R.string.settings_create_account),
                callback = { view?.goToNhsLogin() }
            ),
            SettingsMenuItem(
                id = 12,
                name = UIUtils.getString(R.string.settings_my_details),
                callback = { view?.goToDetails(isUserLoggedIn = false) }
            ),
        )
    }

    private fun buildLoggedInUserMyAccountSection(): List<SettingsItem> {
        return listOfNotNull(
            SettingsSection(
                10, UIUtils.getString(R.string.settings_my_account_title)
            ),
            SettingsMenuItem(
                id = 11,
                name = UIUtils.getString(R.string.settings_my_details),
                callback = { view?.goToDetails(isUserLoggedIn = true) }
            ),
            if (preferenceRepository.isUserLoggedIn) {
                SettingsMenuItem(
                    id = 12,
                    name = UIUtils.getString(R.string.settings_communication_preference),
                    value = UIUtils.getString(R.string.update),
                    callback = { view?.goToCommunicationPreference() }
                )
            } else null,
            SettingsMenuItem(
                id = 13,
                name = UIUtils.getString(R.string.settings_disconnect),
                callback = { view?.showDisconnect() }
            ),
            SettingsMenuItem(
                id = 14,
                name = UIUtils.getString(R.string.settings_quick_unlock),
                callback = { view?.goToQuickUnlock() }
            ),
            SettingsMenuItem(
                id = 15,
                name = UIUtils.getString(R.string.settings_log_out),
                callback = { view?.showLogout() }
            ),
        )
    }

    private fun buildMyPreferencesSection(): List<SettingsItem> {
        return listOfNotNull(
            SettingsSection(
                20, UIUtils.getString(R.string.settings_my_preferences_title)
            ),
            SettingsMenuItem(
                id = 21,
                name = UIUtils.getString(R.string.settings_my_target),
                value = UIUtils.getQuantityString(
                    R.plurals.settings_target_plural,
                    settingsUtils.getSettingsHolder().targetList?.lastOrNull()?.target
                        ?: DEFAULT_TARGET
                ),
                callback = { view?.goToSetTarget() }
            ),
            SettingsMenuItem(
                id = 22,
                name = UIUtils.getString(R.string.settings_my_rewards),
                value = UIUtils.getString(R.string.view_rewards_label),
                callback = { view?.goToRewards() }
            ),
            SettingsMenuItem(
                id = 23,
                name = UIUtils.getString(R.string.settings_my_activity_level),
                value = settingsUtils.getSettingsHolder().classicUser?.activityLevel?.let {
                    when (ActivityLevelEnum.fromValue(it)) {
                        ActivityLevelEnum.INACTIVE -> UIUtils.getString(R.string.inactive_title)
                        ActivityLevelEnum.MODERATELY_ACTIVE -> UIUtils.getString(R.string.moderatelyActive_title)
                        ActivityLevelEnum.ACTIVE -> UIUtils.getString(R.string.active_title)
                        else -> null
                    }
                } ?: "",
                callback = { view?.goToActivityLevel() }
            ),
            SettingsMenuItem(
                id = 24,
                name = UIUtils.getString(R.string.settings_my_goals),
                callback = { view?.goToGoals() }
            ),
        )
    }

    private fun buildNotificationsSection(): List<SettingsItem> {
        return listOf(
            SettingsSection(
                40, UIUtils.getString(R.string.settings_notifications_title)
            ),
            SettingsMenuSwitchItem(
                id = 41,
                name = UIUtils.getString(R.string.settings_walk_reminder),
                isTextSwitchStateVisible = true,
                value = settingsUtils.getSettingsHolder().isBriskReminderSet ?: false,
                callback = { toggleBriskReminder(it) }
            ),
            SettingsMenuItem(
                id = 42,
                name = if (settingsUtils.getSettingsHolder().isBriskReminderSet == true) {
                    settingsUtils.getSettingsHolder().briskReminderTimestamp?.let {
                        DateHelper.getReminderTime(it)
                    } ?: UIUtils.getString(R.string.settings_reminder_disabled)
                } else {
                    UIUtils.getString(R.string.settings_reminder_disabled)
                },
                value = UIUtils.getString(R.string.settings_reminder_change_action),
                callback = { view?.goToBriskWalkReminder() }
            ),
        )
    }

    private fun buildHelpSection(): List<SettingsItem> {
        return listOf(
            SettingsSection(
                50, UIUtils.getString(R.string.settings_help_title)
            ),
            SettingsMenuItem(
                id = 51,
                name = UIUtils.getString(R.string.settings_how_it_works),
                callback = { view?.goToHowItWorks() }
            ),
            SettingsMenuItem(
                id = 52,
                name = UIUtils.getString(R.string.settings_tips),
                callback = { view?.goToTips() }
            )
        )
    }

    private fun buildCommunitySection() = listOf(
        SettingsSection(60, UIUtils.getString(R.string.settings_community_title)),
        SettingsMenuItem(
            id = 61,
            name = UIUtils.getString(R.string.settings_share_app),
            callback = {
                view?.shareApp()
                EarnBadgeHelper.saveEarnedBadge(
                    settingsUtils = settingsUtils,
                    badge = RewardBadgeEnum.BUDDY_UP,
                    preferenceRepository = preferenceRepository
                )
            }
        )
    )

    private fun buildAppInfoSection() = listOfNotNull(
        SettingsSection(70, UIUtils.getString(R.string.settings_app_info)),
        SettingsMenuItem(
            id = 71,
            name = UIUtils.getString(R.string.settings_terms_and_conditions),
            callback = { view?.goToLegalScreen(TERMS_AND_CONDITIONS) }
        ),
        SettingsMenuItem(
            id = 72,
            name = UIUtils.getString(R.string.settings_privacy_policy),
            callback = { view?.goToLegalScreen(PRIVACY_POLICY) }
        ),
        SettingsMenuItem(
            id = 73,
            name = UIUtils.getString(R.string.accessibility_statement),
            callback = { view?.goToLegalScreen(ACCESSIBILITY) }
        ),
        SettingsMenuItem(
            id = 74,
            name = UIUtils.getString(R.string.settings_acknowledgments),
            callback = { view?.goToAcknowledgments() }
        ),
        SettingsMenuItem(
            id = 75,
            name = "Disconnect Google Fit",
            callback = { view?.showDisconnectFitDialog() }
        ).takeIf { settingsUtils.getSettingsHolder().isFitnessMotionEnabled == true }
    )

    private fun buildAppVersionInfoSection() = listOfNotNull(
        SettingsVersion(id = 80, name = BuildConfig.VERSION_NAME, code = BuildConfig.VERSION_CODE)
    )

    private fun toggleBriskReminder(isChecked: Boolean) {
        val isBriskReminderSet = settingsUtils.getSettingsHolder().isBriskReminderSet
        if (isBriskReminderSet != isChecked) {
            settingsUtils.updateSettings(SettingsDataHolder(isBriskReminderSet = isChecked))

            initializeBriskReminder()


            if (isChecked) {
                view?.turnOnAlarm(settingsUtils.getSettingsHolder().briskReminderTimestamp)
            } else {
                view?.turnOffAlarm()
            }
        }
    }

    private fun initializeBriskReminder() {
        if (settingsUtils.getSettingsHolder().briskReminderTimestamp == null) {
            val defaultHour = Calendar.getInstance(Locale.getDefault())
            defaultHour.set(Calendar.HOUR_OF_DAY, DEFAULT_REMINDER_HOUR)
            defaultHour.set(Calendar.MINUTE, 0)

            settingsUtils.updateSettings(SettingsDataHolder(briskReminderTimestamp = defaultHour.timeInMillis))
        }

        generateSettingsItemList()
    }

    private fun toggleAnimationEnabled(checked: Boolean) {
        preferenceRepository.isAnimationEnabled = checked
        generateSettingsItemList()
    }

    override fun onNotificationsPermissionResult(isGranted: Boolean) {
        if (!isGranted) {
            settingsUtils.updateSettings(SettingsDataHolder(isBriskReminderSet = false))
        }

        preferenceRepository.isNotificationsEnabled = isGranted
        generateSettingsItemList()
    }

    override fun initializeReminder() {
        initializeBriskReminder()
    }

    override fun getContent(slug: String) {
        view?.showLoading()

        presenterScope.launch {
            runCatching {
                screenRepository.getScreenContentBySlug(slug)
                    .await()
            }
                .onFailure { view?.showAlert(it) }
                .onSuccess {
                    view?.onContentReceived(slug, it)
                }

            view?.hideLoading()
        }
    }

    override fun onUserDisconnect() {
        walkingPlanRepository.saveToBackendAndDeleteFromDatabaseWalkingPlanEntity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    disconnect()
                },
                {
                    Timber.e(
                        it,
                        "Error during save to backend and delete from database walking plan data"
                    )
                }
            ).addToDisposables()
    }

    private fun disconnect() {
        loginRepository.disconnect()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("User disconnected")
                deleteLastSyncDate()
                removeNhsUserDataUseCase(showLogoutPopup = false)
                walkingPlanRepository.deleteWalkingPlanEntity()
                generateSettingsItemList()
                view?.showUserDisconnected()
            }, { error ->
                Timber.e("Error disconnecting: $error")
                view?.showAlert("Error while disconnecting user. Authentication expired. Logging out instead.")
                removeNhsUserDataUseCase(showLogoutPopup = false)
                generateSettingsItemList()
                view?.showUserDisconnected()
            }).addToDisposables()
    }

    override fun onUserLoggedOut() {
        changePlanStateUseCase.invoke(WalkingPlanState.PAUSED).onErrorComplete()
            .andThen(loginRepository.logout())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("User logged out")
                removeNhsUserDataUseCase(showLogoutPopup = false)
                generateSettingsItemList()
                view?.showUserLoggedOut()
            }, { error ->
                Timber.e("Error logging out, but we still log out user from the app. $error")
                removeNhsUserDataUseCase(showLogoutPopup = false)
                generateSettingsItemList()
                view?.showUserLoggedOut()
            }).addToDisposables()
    }

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }

    private fun deleteLastSyncDate() {
        Timber.d("Deleting nhsLastSyncTime and nhsLastSyncUserId.")
        val settingsHolder = settingsUtils.getSettingsHolder()
            .copy(nhsLastSyncTime = null, nhsLastSyncUserId = null)
        settingsUtils.saveSettingsHolder(settingsHolder)
    }

}
