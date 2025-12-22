package com.flipsidegroup.active10.data.models.dataholders

import android.os.Parcelable
import com.flipside.briskcounter.data.BriskPauseResume
import com.flipsidegroup.active10.data.models.api.CurrentWalkingPlanDTO
import com.flipsidegroup.active10.data.models.api.CurrentWalkingPlanDayDTO
import com.flipsidegroup.active10.data.models.api.PauseResumeDTO
import com.flipsidegroup.active10.data.models.api.WalkingPlan
import com.flipsidegroup.active10.utils.WalkingPlanState.IDLE
import com.flipsidegroup.active10.utils.WalkingPlanState.entries
import com.flipsidegroup.active10.utils.fromLocalZoneToUtcSecondsAsLong
import com.flipsidegroup.active10.utils.toUtcMillis
import com.flipsidegroup.active10.utils.toUtcSwiftMillisAsDouble
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.RealmClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Parcelize
@RealmClass(embedded=true)
open class CurrentWalkingPlan(
    var planId: Long = -1L,
    var startDate: String = "",
    var state: String = IDLE.name.lowercase(),
    var days: @RawValue RealmList<CurrentWalkingPlanDay> = RealmList(),
    var pauseResume: @RawValue RealmList<PauseResume> = RealmList(),
) : Parcelable, RealmObject() {

    fun getEnumState() = entries.singleOrNull {
        it.name.lowercase() == state
    } ?: IDLE

    fun toDTO(): CurrentWalkingPlanDTO {
        return CurrentWalkingPlanDTO(
            planId = planId.toInt(),
            startDate = LocalDateTime.parse(startDate).toUtcSwiftMillisAsDouble(),
            status = state,
            days = days.map { it.toDTO() },
            pauseResume = pauseResume.map { it.toDTO() },
        )
    }

    fun addDays(
        cmsPlan: WalkingPlan,
        newDays: List<CurrentWalkingPlanDay>,
    ) {
        if (this.planId != cmsPlan.id) throw Exception("Plan ID mismatch")

        val requiredDaysSize = cmsPlan.planItineraryItems.size.times(7)
        val daysDiff = requiredDaysSize - this.days.size
        val sortedPastDays = newDays.sortedBy { LocalDate.parse(it.timestamp) }
        this.days.addAll(sortedPastDays.take(daysDiff))
    }

    fun deleteCurrentWeekDays(cmsPlan: WalkingPlan) {
        val daysCount = this.days.size
        if (daysCount == 0) return
        if (this.planId != cmsPlan.id) throw Exception("Plan ID mismatch")

        val isAllDaysSaved = this.days.size == cmsPlan.planItineraryItems.size.times(7)
        val finalDaysCount =
            (if (isAllDaysSaved) (daysCount - 1) else daysCount / 7).times(7)

        val sortedDays = this.days.sortedBy { LocalDate.parse(it.timestamp) }
        val sortedDaysCopy = sortedDays.toList()
        val firstDayOfOldCurrentWeek = sortedDaysCopy.drop(finalDaysCount).firstOrNull() ?: return

        val pausedAtStartOfDay =
            LocalDate.parse(firstDayOfOldCurrentWeek.timestamp).atStartOfDay()
        val resumedAtStartOfDay = LocalDateTime.now().with(LocalTime.MIN)

        // remove outdated PauseResume object, where 'paused' is after pausedAtDay
        this.pauseResume.removeIf { LocalDateTime.parse(it.paused) >= pausedAtStartOfDay }

        // handle PauseResume object where 'resumed' is empty or is after pausedAtDay.
        // If that object doesn't exist, add PauseResume object to prevent deleted days
        // from being saved in the future
        this.pauseResume
            .firstOrNull { it.resumed.isEmpty() || LocalDateTime.parse(it.resumed) > pausedAtStartOfDay }
            ?.apply { this.resumed = resumedAtStartOfDay.toString() }
            ?: this.pauseResume.add(
                PauseResume(
                    paused = pausedAtStartOfDay.toString(),
                    resumed = resumedAtStartOfDay.toString(),
                )
            )

        this.days = RealmList(*sortedDays.take(finalDaysCount).toTypedArray())
    }

    fun resetPlan() {
        this.startDate = LocalDateTime.now().toString()
        this.days = RealmList()
        this.pauseResume = RealmList()
    }
}

@Parcelize
@RealmClass(embedded=true)
open class CurrentWalkingPlanDay(
    var timestamp: String = "",
    var totalBriskMin: Int = 0,
    var totalNonBriskMin: Int = 0,
    var totalSteps: Int = 0,
) : Parcelable, RealmObject() {

    fun toDTO(): CurrentWalkingPlanDayDTO {
        return CurrentWalkingPlanDayDTO(
            timestamp = LocalDate.parse(timestamp).fromLocalZoneToUtcSecondsAsLong(),
            totalBriskMin = totalBriskMin,
            totalNonBriskMin = totalNonBriskMin,
            totalSteps = totalSteps,
        )
    }
}

@Parcelize
@RealmClass(embedded=true)
open class PauseResume(
    var paused: String = "",
    var resumed: String = "",
) : Parcelable, RealmObject() {

    fun toDTO(): PauseResumeDTO {
        return PauseResumeDTO(
            paused = LocalDateTime.parse(paused).toUtcSwiftMillisAsDouble(),
            resumed = if (resumed != "") LocalDateTime.parse(resumed).toUtcSwiftMillisAsDouble() else 0.0,
        )
    }

    fun toBriskPauseResume(): BriskPauseResume {
        val resumedTemp = if (resumed.isEmpty()) {
            LocalDateTime.now().plusYears(10)
        } else {
            LocalDateTime.parse(resumed)
        }
        return BriskPauseResume(
            paused = LocalDateTime.parse(paused).toUtcMillis(),
            resumed = resumedTemp.toUtcMillis(),
        )
    }
}