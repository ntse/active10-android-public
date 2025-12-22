package com.flipsidegroup.active10.presentation.settings.fragments

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.flipside.briskcounter.GoogleFitHelper
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository.Companion.LOGIN_DISCONNECT
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository.Companion.LOGIN_DISCONNECT_SURE
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository.Companion.LOGIN_LOGOUT
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository.Companion.LOGIN_LOGOUT_SURE
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.databinding.FragmentSettingsBinding
import com.flipsidegroup.active10.presentation.accountLoggedOut.AccountLogOutType
import com.flipsidegroup.active10.presentation.accountLoggedOut.AccountLoggedOutActivity
import com.flipsidegroup.active10.presentation.accountLoggedOut.AccountLoggedOutActivityIntent
import com.flipsidegroup.active10.presentation.activitylevel.getActivityLevelIntent
import com.flipsidegroup.active10.presentation.authentication.settings.getQuickUnlockSettingsIntent
import com.flipsidegroup.active10.presentation.classicuserdetails.getClassicUserDetailsIntent
import com.flipsidegroup.active10.presentation.common.fragments.BaseFragment
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.dialogs.DialogCMSButtonModel
import com.flipsidegroup.active10.presentation.dialogs.LoginBottomSheetDialog
import com.flipsidegroup.active10.presentation.faq.FaqIntent
import com.flipsidegroup.active10.presentation.goals.activities.GoalsIntent
import com.flipsidegroup.active10.presentation.home.activities.DEEPLINK_EVENT_PARAM
import com.flipsidegroup.active10.presentation.home.activities.DEEPLINK_EVENT_SHARE
import com.flipsidegroup.active10.presentation.home.activities.HomeActivity
import com.flipsidegroup.active10.presentation.howitworks.activity.HowItWorksIntent
import com.flipsidegroup.active10.presentation.legals.activity.LegalsActivity
import com.flipsidegroup.active10.presentation.licenses.LicensesActivity
import com.flipsidegroup.active10.presentation.reward.getRewardIntent
import com.flipsidegroup.active10.presentation.nhsuserdetails.getNhsUserDetailsIntent
import com.flipsidegroup.active10.presentation.settings.presenter.SettingsPresenter
import com.flipsidegroup.active10.presentation.signIn.signInIntent
import com.flipsidegroup.active10.presentation.stayUpdated.getStayUpdatedIntent
import com.flipsidegroup.active10.presentation.targets.activities.SetTargetMode
import com.flipsidegroup.active10.presentation.targets.activities.TargetIntent
import com.flipsidegroup.active10.presentation.tips.activities.TipsIntent
import com.flipsidegroup.active10.presentation.walkreminder.WalkReminderIntent
import com.flipsidegroup.active10.presentation.walkreminder.alarmreceiver.AlarmReceiver
import com.flipsidegroup.active10.utils.FlowType
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.LoginBottomSheetDialogType
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.permissionx.guolindev.PermissionX
import com.phe.betterhealth.components.settings.SettingsAdapter
import com.phe.betterhealth.components.settings.SettingsItem
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import javax.inject.Inject

private const val SHARING_INTENT_TYPE = "text/plain"
private const val SHARING_URL = "https://www.nhs.uk/better-health/get-active/"

class SettingsFragment : BaseFragment<SettingsView>(), SettingsView {

    @Inject
    lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var presenter: SettingsPresenter

    @Inject
    internal lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    private var binding: FragmentSettingsBinding by lifecycleAwareVariable()

    private val settingsAdapter: SettingsAdapter = SettingsAdapter()
    private var isNotificationPermissionGranted: Boolean = false
    private var setAlarmTimestamp: Long? = null
    private val alarmPermissionResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            turnOnAlarmHandler()
        }

    private var shouldShareAtStart: Boolean = false

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        shouldShareAtStart = activity?.intent?.getStringExtra(DEEPLINK_EVENT_PARAM) == DEEPLINK_EVENT_SHARE
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingsBinding.bind(view)
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
            presenter.onNotificationsPermissionResult(false)
        }else{
            presenter.onNotificationsPermissionResult(true)
            presenter.initializeReminder()
        }

        ViewCompat.setAccessibilityHeading(binding.settingsToolbar, true)
        initializeRecyclerView()

        if (shouldShareAtStart) {
            shareApp()
            shouldShareAtStart = false
        }
    }

    override fun onResume() {
        super.onResume()
        val notificationsEnabled = NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
        val alarmsAllowed = isScheduleExactAlarmPermissionAllowed()
        val isEverythingAllowed = notificationsEnabled && alarmsAllowed
        presenter.onNotificationsPermissionResult(isEverythingAllowed)
        presenter.generateSettingsItemList()
    }

    private fun initializeRecyclerView() {
        with(binding.settingsRV) {
            adapter = settingsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun getPresenter(): LifecycleAwarePresenter<SettingsView> = presenter

    override fun updateListItems(settings: List<SettingsItem>) {
        settingsAdapter.submitList(settings)
    }

    override fun goToSetTarget() {
        startActivity(requireContext().TargetIntent(SetTargetMode.SETTINGS))
    }

    override fun goToRewards() {
        startActivity(requireContext().getRewardIntent())
    }

    override fun goToActivityLevel() {
        startActivity(requireContext().getActivityLevelIntent())
    }

    override fun goToLegalScreen(legalScreenString: String) {
        startActivity(requireContext().LegalsActivity(legalScreenString))
    }

    override fun goToBriskWalkReminder() {
        startActivity(requireContext().WalkReminderIntent(true))
    }

    override fun goToFaq() {
        startActivity(requireContext().FaqIntent(true))
    }

    override fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = SHARING_INTENT_TYPE
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))

        val shareMessage = getString(R.string.share_message) + SHARING_URL
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(
            Intent.createChooser(
                shareIntent,
                getString(R.string.share_intent_chooser_title),
            )
        )
    }

    override fun goToTips() {
        startActivity(requireContext().TipsIntent(true))
    }

    override fun goToGoals() {
        startActivity(requireContext().GoalsIntent(true))
    }

    override fun goToHowItWorks() {
        startActivity(requireContext().HowItWorksIntent(true))
    }

    override fun goToNhsLogin() {
        startActivity(requireContext().signInIntent(flowType = FlowType.SETTINGS))
    }

    override fun goToQuickUnlock() {
        startActivity(requireContext().getQuickUnlockSettingsIntent())
    }

    override fun turnOnAlarm(timestamp: Long?) {
        setAlarmTimestamp = timestamp
        val neededPermissions = buildList {
            add(checkPostNotificationPermission())
        }.filterNotNull()

        if (neededPermissions.isNotEmpty()) {
            PermissionX.init(this)
                .permissions(neededPermissions)
                .onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        "Please allow notification permission to use this feature.",
                        "Allow",
                        "Cancel"
                    )
                }.request { isGranted, _, _ ->
                    isNotificationPermissionGranted = isGranted

                    if (!isScheduleExactAlarmPermissionAllowed()) {
                        checkScheduleExactAlarmPermission()
                    } else {
                        turnOnAlarmHandler()
                    }
                }
        } else {
            val notificationsEnabled = NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
            if(notificationsEnabled){
                presenter.onNotificationsPermissionResult(true)
                AlarmReceiver.createAlarm(requireContext(), timestamp)
            }else{
                presenter.onNotificationsPermissionResult(false)
                Toast.makeText(
                    requireContext(),
                    R.string.dialog_notifications_disabled_title,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun turnOnAlarmHandler() {
        val allGranted = isNotificationPermissionGranted && isScheduleExactAlarmPermissionAllowed()
        presenter.onNotificationsPermissionResult(allGranted)
        if (allGranted) {
            AlarmReceiver.createAlarm(requireContext(), setAlarmTimestamp)
        }
    }

    private fun isScheduleExactAlarmPermissionAllowed(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()
        } else {
            true
        }
    }

    private fun checkScheduleExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.dialog_alarm_permission_title)
                .setMessage(R.string.dialog_alarm_permission_subtitle)
                .setPositiveButton(R.string.button_confirm) { _, _ ->
                    Intent().also { intent ->
                        intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        alarmPermissionResult.launch(intent)
                    }
                }.setNegativeButton(R.string.button_decline) { _, _ ->
                    turnOnAlarmHandler()
                }.setOnCancelListener {
                    turnOnAlarmHandler()
                }.show()
        }
    }

    private fun checkPostNotificationPermission(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        }
    }

    override fun turnOffAlarm() {
        AlarmReceiver.deleteAlarm(requireContext())
    }

    private fun showNotificationsDisabledDialog() {
        MaterialDialog(requireActivity()).show {
            title(R.string.dialog_notifications_disabled_title)
            message(R.string.dialog_notifications_disabled_subtitle)
            positiveButton(R.string.button_ok)
        }
    }

    override fun showDisconnectFitDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Disconnect Google Fit")
            .setMessage("Are you sure you want to disconnect Google Fit from Active 10?")
            .setNegativeButton("No") { _, _ -> }
            .setPositiveButton("Yes") { _, _ ->
                GoogleFitHelper.disconnectGoogleFit(requireContext()) {
                    (requireActivity() as? HomeActivity)?.removeIsFitnessMotionEnabledPermission()
                }
            }
            .show()
    }

    override fun goToDetails(isUserLoggedIn: Boolean) {
        if (isUserLoggedIn)
            startActivity(requireContext().getNhsUserDetailsIntent())
        else
            startActivity(requireContext().getClassicUserDetailsIntent())
    }

    override fun goToCommunicationPreference() {
        startActivity(requireContext().getStayUpdatedIntent(flowType = FlowType.STAY_UPDATED_ONLY))
    }

    override fun showLogout() {
        presenter.getContent(LOGIN_LOGOUT)
    }

    private fun showLogoutSure() {
        presenter.getContent(LOGIN_LOGOUT_SURE)
    }

    override fun showDisconnect() {
        presenter.getContent(LOGIN_DISCONNECT)
    }

    private fun showDisconnectSure() {
        presenter.getContent(LOGIN_DISCONNECT_SURE)
    }

    override fun onContentReceived(slug: String, content: ScreenContent) {
        when (slug) {
            LOGIN_LOGOUT -> showLogoutBottomDialog(content)
            LOGIN_LOGOUT_SURE -> showLogoutSureBottomDialog(content)
            LOGIN_DISCONNECT -> showDisconnectBottomDialog(content)
            LOGIN_DISCONNECT_SURE -> showDisconnectSureBottomDialog(content)
        }
    }

    override fun goToAcknowledgments() {
        startActivity(requireContext().LicensesActivity())
    }

    override fun showUserLoggedOut() {
        startActivity(requireContext().AccountLoggedOutActivityIntent(AccountLogOutType.LOGOUT))
    }

    override fun showUserDisconnected() {
        startActivity(requireContext().AccountLoggedOutActivityIntent(AccountLogOutType.DISCONNECT))
    }

    private fun showDisconnectBottomDialog(content: ScreenContent) {
        showBottomDialog(content, showIcon = true, onPrimary = {
            showDisconnectSure()
        }, onSecondary = {
            firebaseAnalyticsHelper.nhsAccountDisconnectAnswer("No thanks", null)
        })
    }

    private fun showDisconnectSureBottomDialog(content: ScreenContent) {
        showBottomDialog(content, onPrimary = {
            firebaseAnalyticsHelper.nhsAccountDisconnectAnswer("Yes, disconnect", "Yes, disconnect")
            presenter.onUserDisconnect()
        }, onSecondary = {
            firebaseAnalyticsHelper.nhsAccountDisconnectAnswer("Yes, disconnect", "No thanks")
        })
    }

    private fun showLogoutBottomDialog(content: ScreenContent) {
        showBottomDialog(content, showIcon = true, onPrimary = {
            showLogoutSure()
        }, onSecondary = {
            firebaseAnalyticsHelper.nhsAccountLogoutAnswer("No thanks", null)
        })
    }

    private fun showLogoutSureBottomDialog(content: ScreenContent) {
        showBottomDialog(content, onPrimary = {
            firebaseAnalyticsHelper.nhsAccountLogoutAnswer("Yes, log out", "Yes, log out")
            presenter.onUserLoggedOut()
        }, onSecondary = {
            firebaseAnalyticsHelper.nhsAccountLogoutAnswer("Yes, log out", "No thanks")
        })
    }

    private  fun showBottomDialog(content: ScreenContent, showIcon: Boolean = false, onPrimary: () -> Unit, onSecondary: () -> Unit) {
        LoginBottomSheetDialog(
            icon = if (showIcon) R.drawable.ic_nhs_a10_disconnect else null,
            dialogType = LoginBottomSheetDialogType.DISCONNECT,
            title = content.title,
            subtitle = content.description,
            primaryButton = DialogCMSButtonModel(text = content.firstButtonTitle, onClick = onPrimary),
            secondaryButton = DialogCMSButtonModel(content.secondButtonTitle, onClick = onSecondary),
        ).show(requireActivity().supportFragmentManager, null)
    }


}
