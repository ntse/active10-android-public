package com.flipsidegroup.active10.data.models.dataholders

import android.os.Parcelable
import com.flipsidegroup.active10.data.models.api.HistoricalWalkingPlanDTO
import com.flipsidegroup.active10.utils.toUtcSwiftMillisAsDouble
import io.realm.RealmObject
import io.realm.annotations.RealmClass
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
@RealmClass(embedded=true)
open class HistoricalWalkingPlan(
    var planId: Long = -1,
    var startDate: String = "",
    var endDate: String = "",
    var status: String = "",
) : Parcelable, RealmObject() {

    fun toDTO(): HistoricalWalkingPlanDTO {
        return HistoricalWalkingPlanDTO(
            planId = planId.toInt(),
            startDate = LocalDateTime.parse(startDate).toUtcSwiftMillisAsDouble(),
            endDate = LocalDateTime.parse(endDate).toUtcSwiftMillisAsDouble(),
            status = status,
        )
    }
}