package com.flipsidegroup.active10.presentation.usecases

import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import timber.log.Timber
import javax.inject.Inject

class IsPlanMatchToUserUseCase @Inject constructor(
    private val settingsUtils: SettingsUtils
) {

    operator fun invoke(cmsPlan: WalkingPlan, checkForPlanHeroBadge: Boolean = false): Boolean {
        val nhsUser = settingsUtils.getSettingsHolder().nhsUser ?: run {
            Timber.e("Lack of NHS user data in settings holder")
            return false
        }
        val classicUser = settingsUtils.getSettingsHolder().classicUser ?: run {
            Timber.e("Lack of classic user data in settings holder")
            return false
        }
        val userGoals = settingsUtils.getSettingsHolder().goalsList?.filter { it.isSelected } ?: emptyList()

        val planAgeRange = cmsPlan.categoryDetails!!.age
        val planGender = cmsPlan.categoryDetails!!.sex
        val planGoals = cmsPlan.categoryDetails!!.motivation
        val planActivityLevel = cmsPlan.categoryDetails!!.activityLevel.map { it.toString().lowercase() }

        val userSex = nhsUser.gender!!
            .replaceFirstChar(Char::titlecase)
            .takeIf { str -> str in arrayOf("Male, Female") } ?: ""
        val userAgeRange = nhsUser.ageRange
        val userActivityLevel = classicUser.activityLevel.toString().lowercase()

        val shouldCheckMotivation =
            (userAgeRange == "35 to 44") ||
            (userSex == "Male" && (userAgeRange == "45 to 54" || userAgeRange == "55 to 64"))

        val highestPriorityCMSGoal = userGoals.filter { !it.isCustomGoal }.minByOrNull { it.order }
        val userHasOnlyCustomGoals = userGoals.all { it.isCustomGoal }

        val isMotivationsMatch = if (shouldCheckMotivation) {
            (highestPriorityCMSGoal != null && planGoals.contains(highestPriorityCMSGoal.goal)) ||
            (planGoals.isEmpty() && userHasOnlyCustomGoals)
        } else {
            planGoals.isEmpty()
        }

        val isActivityLevelMatch = if (checkForPlanHeroBadge) {
            planActivityLevel.contains("active")
        } else {
            planActivityLevel.contains(userActivityLevel)
        }

        return (userSex.isEmpty() || planGender.isEmpty() || userSex in planGender) &&
                (planAgeRange.isEmpty() || userAgeRange in planAgeRange) &&
                isMotivationsMatch &&
                isActivityLevelMatch
    }

}