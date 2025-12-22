package com.flipsidegroup.active10.presentation.walksneardetails.dialog

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import com.flipsidegroup.active10.data.enums.RewardBadgeEnum
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.databinding.DialogGoJauntlyBinding
import com.flipsidegroup.active10.presentation.common.dialogfragments.BaseDialogFragment
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.utils.EarnBadgeHelper
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.flipsidegroup.active10.utils.openURL
import javax.inject.Inject

class GoJauntlyDialog(
    private val id : String?,
    private val url : String?
): BaseDialogFragment<BaseView>() {

    private var binding: DialogGoJauntlyBinding by lifecycleAwareVariable()

    @Inject
    internal lateinit var settingsUtils: SettingsUtils

    @Inject
    internal lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    override fun getPresenter(): LifecycleAwarePresenter<BaseView>? = null

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogGoJauntlyBinding.inflate(inflater, container, false)

        binding.buttonCancel.setOnClickListener {
            firebaseAnalyticsHelper.goToJauntlyPopup(isCancelled = true)
            dismiss()
        }

        binding.buttonContinue.setOnClickListener {
            EarnBadgeHelper.saveEarnedBadge(
                settingsUtils = settingsUtils,
                badge = RewardBadgeEnum.GO_JAUNTLY,
                timestamp = System.currentTimeMillis(),
                preferenceRepository = preferenceRepository
            )
            firebaseAnalyticsHelper.goToJauntlyPopup(isCancelled = false)
            openGoJauntlyInPlayStore()
        }

        return binding.root
    }

    private fun openGoJauntlyInPlayStore() {
        val goJauntlyIntent = requireActivity().packageManager.getLaunchIntentForPackage(GO_JAUNTLY_PACKAGE_NAME)
        if (goJauntlyIntent != null) {
            id?.let {
               context?.openURL(GO_JAUNTLY_DEEPLINK_WALK_PREFIX + id)
                return@openGoJauntlyInPlayStore
            } ?: run {
                startActivity(goJauntlyIntent)
                return@openGoJauntlyInPlayStore
            }
        }
        try {
            // Try to open the app page in Google Play app
            context?.openURL("market://details?id=$GO_JAUNTLY_PACKAGE_NAME")
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$GO_JAUNTLY_PACKAGE_NAME")))
        } catch (e: ActivityNotFoundException) {
            // If Google Play app is not installed, open in web browser
            context?.openURL(url ?: "\"https://play.google.com/store/apps/details?id=$GO_JAUNTLY_PACKAGE_NAME\"")
        }
    }

    companion object {
        const val GO_JAUNTLY_PACKAGE_NAME = "com.gojauntly.app"
        const val GO_JAUNTLY_DEEPLINK_WALK_PREFIX = "gojauntly://walks.gojauntly.com/walks/"

    }

}