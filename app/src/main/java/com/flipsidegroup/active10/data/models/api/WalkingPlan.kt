package com.flipsidegroup.active10.data.models.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
open class WalkingPlan(

    @SerializedName("id")
    @PrimaryKey
    var id: Long = 0L,

    @SerializedName("image")
    var image: String? = null,

    @SerializedName("plan_name")
    var planName: String = "",

    @SerializedName("plan_code")
    var planCode: String = "",

    @SerializedName("plan_tagline")
    var planTagline: String = "",

    @SerializedName("plan_description")
    var planDescription: String = "",

    @SerializedName("plan_duration")
    var planDuration: String = "",

    @SerializedName("plan_duration_weeks")
    var planDurationWeeks: Int = 0,

    @SerializedName("plan_brisk_minutes_per_day")
    var planBriskMinutesPerDay: String = "",

    @SerializedName("plan_goal")
    var planGoal: String = "",

    @SerializedName("plan_difficulty")
    var planDifficulty: String = "",

    @SerializedName("plan_itinerary_items")
    var planItineraryItems: @RawValue RealmList<PlanItineraryItem> = RealmList(),

    @SerializedName("category_details")
    var categoryDetails: @RawValue CategoryDetails? = CategoryDetails(),

    @SerializedName("inferior_plan_ids")
    @Required
    var inferiorPlanIds: @RawValue RealmList<Long> = RealmList(),

    @SerializedName("superior_plan_ids")
    @Required
    var superiorPlanIds: @RawValue RealmList<Long> = RealmList()
) : Parcelable, RealmObject()

@Parcelize
open class PlanItineraryItem(

    @SerializedName("id")
    @PrimaryKey
    var id: Long = 0L,

    @SerializedName("week_label")
    var weekLabel: String = "",

    @SerializedName("description_of_weekly_task")
    var descriptionOfWeeklyTask: String = "",

    @SerializedName("total_brisk_minutes")
    var totalBriskMinutes: Int = 0,

    @SerializedName("daily_brisk_minutes")
    @Required
    var dailyBriskMinutes: @RawValue RealmList<Int> = RealmList(),

    @SerializedName("total_non_brisk_minutes")
    var totalNonBriskMinutes: Int = 0,

    @SerializedName("daily_non_brisk_minutes")
    @Required
    var dailyNonBriskMinutes: @RawValue RealmList<Int> = RealmList()
) : Parcelable, RealmObject()

@Parcelize
open class CategoryDetails(
    @SerializedName("sex")
    var sex: @RawValue RealmList<String> = RealmList(),

    @SerializedName("age")
    var age: @RawValue RealmList<String> = RealmList(),

    @SerializedName("activity_level")
    var activityLevel: @RawValue RealmList<String> = RealmList(),

    @SerializedName("motivation")
    var motivation: @RawValue RealmList<String> = RealmList()
) : Parcelable, RealmObject()