package com.flipsidegroup.active10.data.models.dataholders

import android.os.Parcelable
import com.flipsidegroup.active10.data.models.api.WalkingPlanDTO
import com.flipsidegroup.active10.data.models.api.WalkingPlanDataDTO
import com.flipsidegroup.active10.utils.WalkingPlanState
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.time.LocalDateTime
import java.util.UUID

@Parcelize
open class WalkingPlanEntity(
    /**
     * Info: Don't change value for 'id' field - only one WalkingPlanEntity should exist in database
     */
    @PrimaryKey
    var id: Long = 0L,
    var currentWalkingPlan: CurrentWalkingPlan? = null,
    var walkingPlanHistory: @RawValue RealmList<HistoricalWalkingPlan> = RealmList(),
) : Parcelable, RealmObject() {

    fun toDTO(): WalkingPlanDTO {
        return WalkingPlanDTO(
            id = UUID.randomUUID().toString(),
            walkingPlanData = WalkingPlanDataDTO(
                currentWalkingPlan = currentWalkingPlan?.toDTO(),
                walkingPlanHistory = walkingPlanHistory.map { it.toDTO() }
            )
        )
    }

    fun moveCurrentPlanToHistoricalPlan(state: WalkingPlanState) {
        currentWalkingPlan?.let { currentPlan ->
            val historicalWalkingPlan = HistoricalWalkingPlan(
                planId = currentPlan.planId,
                startDate = currentPlan.startDate,
                endDate = LocalDateTime.now().toString(),
                status = state.name.lowercase(),
            )
            walkingPlanHistory.add(historicalWalkingPlan)
            currentWalkingPlan = null
        }
    }
}