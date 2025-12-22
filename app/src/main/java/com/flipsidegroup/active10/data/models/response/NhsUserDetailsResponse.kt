package com.flipsidegroup.active10.data.models.response

import android.os.Parcelable
import com.flipsidegroup.active10.data.models.Goal
import com.flipsidegroup.active10.data.models.api.NhsUserDetails
import com.flipsidegroup.active10.utils.Constants.NHSLogin.EMAIL_PREFERENCES_NAME
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NhsUserDetailsResponse(
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("first_name")
    val firstName: String? = "",
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("age")
    val age: Int? = null,
    @SerializedName("age_range")
    val ageRange: String? = "",
    @SerializedName("postcode")
    val postcode: String? = null,
    @SerializedName("gender")
    val gender: String? = "",
    @SerializedName("email_preferences")
    val emailPreferences: List<EmailPreferenceResponse> = emptyList(),
    @SerializedName("latest_activity_level")
    val latestActivityLevel: LatestActivityLevelResponse? = null,
    @SerializedName("latest_motivation")
    val latestMotivation: LatestMotivationResponse? = null,
) : Parcelable {

    fun toEntity(): NhsUserDetails {
        return NhsUserDetails(
            id = id,
            firstName = firstName,
            email = email,
            age = age,
            ageRange = ageRange,
            postcode = postcode,
            isEmailUpdatesAllowed = emailPreferences.firstOrNull {
                it.name == EMAIL_PREFERENCES_NAME
            }?.isActive ?: false,
            gender = gender,
        )
    }
}

@Parcelize
data class EmailPreferenceResponse(
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("is_active")
    val isActive: Boolean? = null,
) : Parcelable

@Parcelize
data class LatestActivityLevelResponse(
    @SerializedName("level")
    val level: String
) : Parcelable

@Parcelize
data class LatestMotivationResponse(
    @SerializedName("goals")
    val goals: List<NhsUserGoalResponse> = emptyList()
) : Parcelable {

    fun toAppGoals(cmsGoals: List<Goal>): List<Goal> {
        return goals.map { userGoal ->
            val isCustomGoal = userGoal.id == -1
            val cmsGoal = cmsGoals.find { it.goalId == userGoal.id }
            Goal(
                goalId = userGoal.id,
                order = if (isCustomGoal) -1 else cmsGoal?.order ?: -1,
                goal = if (isCustomGoal) userGoal.text else cmsGoal?.goal ?: userGoal.text,
                isCustomGoal = isCustomGoal,
                isSelected = true,
            )
        }
    }
}

@Parcelize
data class NhsUserGoalResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("text")
    val text: String
) : Parcelable