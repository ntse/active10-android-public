package com.flipsidegroup.active10.data.models

import com.flipsidegroup.active10.data.models.requests.NhsUserGoalRequest
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class Goal(

    @SerializedName("id")
    @PrimaryKey
    var goalId: Int = 0,

    @SerializedName("order")
    var order: Int = -1,

    @SerializedName("text")
    var goal: String = "",

    @SerializedName("isCustomGoal")
    var isCustomGoal: Boolean = false,

    @SerializedName("isSelected")
    var isSelected: Boolean = false
) : RealmObject() {

    fun toNhsGoalRequest(): NhsUserGoalRequest {
        return NhsUserGoalRequest(
            id = if (isCustomGoal) -1 else goalId,
            text = goal,
        )
    }
}