package com.flipsidegroup.active10.data.models.api

import android.os.Parcelable
import com.flipsidegroup.active10.data.models.dataholders.CurrentWalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.CurrentWalkingPlanDay
import com.flipsidegroup.active10.data.models.dataholders.HistoricalWalkingPlan
import com.flipsidegroup.active10.data.models.dataholders.PauseResume
import com.flipsidegroup.active10.data.models.dataholders.WalkingPlanEntity
import com.flipsidegroup.active10.utils.utcSwiftToLocalDateTimeString
import com.flipsidegroup.active10.utils.utcToLocalDateFromLocalZoneString
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
open class WalkingPlanDTO(

    @SerializedName("id")
    var id: String = UUID.randomUUID().toString(),

    @SerializedName("walking_plan_data")
    var walkingPlanData: WalkingPlanDataDTO,
) : Parcelable

@Parcelize
open class WalkingPlanDataDTO(

    @SerializedName("current_walking_plan")
    var currentWalkingPlan: CurrentWalkingPlanDTO? = null,

    @SerializedName("walking_plan_history")
    var walkingPlanHistory: List<HistoricalWalkingPlanDTO> = emptyList(),
) : Parcelable {

    fun toEntity(): WalkingPlanEntity {
        return WalkingPlanEntity(
            currentWalkingPlan = currentWalkingPlan?.toEntity(),
            walkingPlanHistory = RealmList<HistoricalWalkingPlan>().apply {
                addAll(walkingPlanHistory.map { it.toEntity() })
            }
        )
    }
}


@Parcelize
open class CurrentWalkingPlanDTO(

    @SerializedName("planId")
    var planId: Int,

    @SerializedName("startDate")
    var startDate: Double,

    @SerializedName("status")
    var status: String,

    @SerializedName("walkingData")
    var days: List<CurrentWalkingPlanDayDTO> = emptyList(),

    @SerializedName("pauseResume")
    var pauseResume: List<PauseResumeDTO> = emptyList(),
) : Parcelable {

    fun toEntity(): CurrentWalkingPlan {
        return CurrentWalkingPlan(
            planId.toLong(),
            startDate.utcSwiftToLocalDateTimeString(),
            status,
            RealmList<CurrentWalkingPlanDay>().apply {
                addAll(days.map { it.toEntity() })
            },
            RealmList<PauseResume>().apply {
                addAll(pauseResume.map { it.toEntity() })
            }
        )
    }
}

@Parcelize
open class CurrentWalkingPlanDayDTO(

    @SerializedName("date")
    var timestamp: Long,

    @SerializedName("minsBrisk")
    var totalBriskMin: Int,

    @SerializedName("minsWalking")
    var totalNonBriskMin: Int,

    @SerializedName("steps")
    var totalSteps: Int,
) : Parcelable {

    fun toEntity(): CurrentWalkingPlanDay {
        return CurrentWalkingPlanDay(
            timestamp.utcToLocalDateFromLocalZoneString(),
            totalBriskMin,
            totalNonBriskMin,
            totalSteps
        )

    }
}

@Parcelize
open class PauseResumeDTO(

    @SerializedName("paused")
    var paused: Double,

    @SerializedName("resumed")
    var resumed: Double,
) : Parcelable {

    fun toEntity(): PauseResume {
        return PauseResume(
            paused.utcSwiftToLocalDateTimeString(),
            resumed.utcSwiftToLocalDateTimeString()
        )
    }
}

@Parcelize
open class HistoricalWalkingPlanDTO(

    @SerializedName("planId")
    var planId: Int,

    @SerializedName("startDate")
    var startDate: Double,

    @SerializedName("endDate")
    var endDate: Double,

    @SerializedName("status")
    var status: String,
) : Parcelable {

    fun toEntity(): HistoricalWalkingPlan {
        return HistoricalWalkingPlan(
            planId.toLong(),
            startDate.utcSwiftToLocalDateTimeString(),
            endDate.utcSwiftToLocalDateTimeString(),
            status
        )
    }
}