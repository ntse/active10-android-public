package com.flipsidegroup.active10.presentation.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.CurrentWalkingPlanDay
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.persistance.newapi.WalkingPlanRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.databinding.DialogWalkingPlanDebugBinding
import com.flipsidegroup.active10.presentation.common.dialogfragments.BaseDialogFragment
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.usecases.ChangePlanStateUseCase
import com.flipsidegroup.active10.services.WalkingPlanService
import com.flipsidegroup.active10.utils.WalkingPlanState
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.rx2.await
import java.time.LocalDate
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

class WalkingPlanDebugDialog(
    private val cmsPlan: WalkingPlan,
    private val onFirstButton: () -> Unit,
    private val onSecondButton: () -> Unit = {}
) : BaseDialogFragment<BaseView>() {

    override fun getPresenter() = null

    @Inject
    lateinit var screenRepository: ScreenRepository

    @Inject
    lateinit var walkingPlanRepository: WalkingPlanRepository

    @Inject
    lateinit var preferenceRepository: PreferenceRepository

    @Inject
    lateinit var changePlanStateUseCase: ChangePlanStateUseCase

    @Inject
    lateinit var settingsUtils: SettingsUtils

    private var binding: DialogWalkingPlanDebugBinding by lifecycleAwareVariable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_walking_plan_debug, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogWalkingPlanDebugBinding.bind(view)
        isCancelable = true
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        with(binding) {
            isGoogleFitChecked = true
            firstButton.setOnClickListener {
                onFirstBtnClicked()
            }
            secondButton.setOnClickListener {
                onSecondButton()
                dismiss()
            }
            sourceRG.setOnCheckedChangeListener { _, checkedId ->
                isGoogleFitChecked = checkedId == binding.GoogleFitRB.id
            }
        }
    }

    private fun onFirstBtnClicked() {
        val daysCount = (binding.daysEditText.text.toString().toIntOrNull() ?: 0).let {
            val targetDays = cmsPlan.planItineraryItems.size * 7
            if (it >= targetDays) targetDays else it
        }
        val briskPercentage = binding.briskEditText.text.toString().toIntOrNull() ?: 0
        val nonBriskPercentage = binding.nonBriskEditText.text.toString().toIntOrNull() ?: 0

        lifecycleScope.launch {
            runCatching {
                walkingPlanRepository.subscribeUserWalkingPlans().awaitFirst()
            }.onSuccess { userPlansData ->
                val userPlanData = userPlansData.first {
                    it.currentWalkingPlan?.planId == cmsPlan.id
                }
                val currentPlan = userPlanData.currentWalkingPlan
                if (currentPlan?.getEnumState() != WalkingPlanState.ACTIVE)
                    throw IllegalStateException("Plan for Debug is not active")
                currentPlan.apply {
                    val currentDaysCount = days.toList().size
                    val missingDaysCount = if (daysCount > currentDaysCount) daysCount - currentDaysCount else 0
                    days.forEach {
                        it.timestamp = LocalDate.parse(it.timestamp).minusDays(missingDaysCount.toLong()).toString()
                    }
                    startDate = LocalDateTime.parse(startDate).minusDays(missingDaysCount.toLong()).toString()
                    if (binding.isGoogleFitChecked) {
                        val planServiceIntent = requireContext().WalkingPlanService()
                        WalkingPlanService.enqueueWork(requireContext(), planServiceIntent)
                    } else {
                        for (i in 0 until missingDaysCount) {
                            val dayToSaveIndex = currentDaysCount + i
                            val timestamp = LocalDate.now().minusDays((missingDaysCount - i).toLong()).toString()
                            val weeks = cmsPlan.planItineraryItems.sortedBy { it.id }
                            val planDay = CurrentWalkingPlanDay(
                                timestamp = timestamp,
                                totalBriskMin = ((weeks[dayToSaveIndex / 7]?.dailyBriskMinutes
                                    ?.get(dayToSaveIndex % 7) ?: 0) * (briskPercentage.toDouble() / 100)).toInt(),
                                totalNonBriskMin = ((weeks[dayToSaveIndex / 7]?.dailyNonBriskMinutes
                                    ?.get(dayToSaveIndex % 7) ?: 0) * (nonBriskPercentage.toDouble() / 100)).toInt(),
                                totalSteps = 0
                            )
                            days.add(planDay)
                        }
                    }
                }

                walkingPlanRepository.saveWalkingPlanEntity(userPlanData).await()

                dismiss()
                onFirstButton()
            }.onFailure {
                Timber.d(it)
                showAlert(it)
                dismiss()
            }

        }
    }

}