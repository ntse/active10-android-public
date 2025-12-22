package com.flipsidegroup.active10.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.databinding.DialogWalkingPlanCommonBinding
import com.flipsidegroup.active10.presentation.common.dialogfragments.BaseDialogFragment
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import javax.inject.Inject

class WalkingPlanCommonDialog(
    private val slug: String,
    private val onFirstButton: () -> Unit,
    private val onSecondButton: () -> Unit = {},
    private val planContent: WalkingPlan? = null,
) : BaseDialogFragment<BaseView>() {

    override fun getPresenter() = null

    @Inject
    lateinit var screenRepository: ScreenRepository

    @Inject
    lateinit var firebaseAnalyticsHelper: FirebaseAnalyticsHelper

    private var binding: DialogWalkingPlanCommonBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_walking_plan_common, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogWalkingPlanCommonBinding.bind(view)

        isCancelable = false
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (slug.isBlank()) {
            dismiss()
            Timber.e("Incorrect slug")
            return
        }

        lifecycleScope.launch {
            runCatching {
                screenRepository.getScreenContentBySlug(slug).await()
            }
                .onSuccess {
                    showContent(it)
                }
                .onFailure {
                    showAlert(it)
                }

        }
    }

    private fun showContent(screenContent: ScreenContent) {
        with(binding) {
            screenContent.apply {
                title = title
                        .replace("{plan}", planContent?.planName ?: "plan")
                //TODO: fix title/description popups in CMS, consult with iOS dev
                if(slug != WALKING_PLAN_RESTART_AFTER_DELAY_DIALOG) {
                    description = title
                    binding.title.visibility = View.GONE
                }
            }

            content = screenContent
            executePendingBindings()

            when (slug) {
                WALKING_PLAN_CANCEL_DIALOG,
                WALKING_PLAN_START_DIALOG,
                WALKING_PLAN_RESTART_DIALOG,
                WALKING_PLAN_RESTART_AFTER_DELAY_DIALOG -> {
                    icon.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_yellow_active_plan
                        )
                    )
                    icon.visibility = View.VISIBLE
                }
                WALKING_PLAN_PAUSE_DIALOG -> {
                    icon.setImageDrawable(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            R.drawable.ic_pink_paused_plan
                        )
                    )
                    icon.visibility = View.VISIBLE
                }
                else -> {
                    icon.visibility = View.GONE
                }
            }
            firstButton.setOnClickListener {
                val event = when (slug) {
                    WALKING_PLAN_START_DIALOG -> "MyWalkingPlanConfirmStart"
                    WALKING_PLAN_PAUSE_DIALOG -> "MyWalkingPlanConfirmPause"
                    WALKING_PLAN_CANCEL_DIALOG -> "MyWalkingPlanConfirmCancel"
                    WALKING_PLAN_RESTART_DIALOG -> "MyWalkingPlanConfirmRestart"
                    WALKING_PLAN_RESTART_AFTER_DELAY_DIALOG -> "MyWalkingPlanConfirmRestartAfterDelay"
                    else -> null
                }
                event?.let { firebaseAnalyticsHelper.sendButtonClickedEvent(it) }
                onFirstButton()
                dismiss()
            }
            secondButton.setOnClickListener {
                val event = when (slug) {
                    WALKING_PLAN_START_DIALOG -> "MyWalkingPlanDenyStart"
                    WALKING_PLAN_PAUSE_DIALOG -> "MyWalkingPlanDenyPause"
                    WALKING_PLAN_CANCEL_DIALOG -> "MyWalkingPlanDenyCancel"
                    WALKING_PLAN_RESTART_DIALOG -> "MyWalkingPlanDenyRestart"
                    WALKING_PLAN_RESTART_AFTER_DELAY_DIALOG -> "MyWalkingPlanDenyRestartAfterDelay"
                    else -> null
                }
                event?.let { firebaseAnalyticsHelper.sendButtonClickedEvent(it) }
                onSecondButton()
                dismiss()
            }
        }
    }

    companion object {
        const val WALKING_PLAN_START_DIALOG = "start_walking_plan"
        const val WALKING_PLAN_PAUSE_DIALOG = "pause_walking_plan"
        const val WALKING_PLAN_CANCEL_DIALOG = "cancel_walking_plan"
        const val WALKING_PLAN_RESTART_DIALOG = "restart_walking_plan"
        const val WALKING_PLAN_RESTART_AFTER_DELAY_DIALOG = "restart_walking_plan_after_delay"
    }
}