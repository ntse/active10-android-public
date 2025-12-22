package com.flipsidegroup.active10.data.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey



open class StepOverview(
    @PrimaryKey var timestamp: Long? = null,
    var totalBriskMin: Int? = null,
    var totalWalkMin: Int? = null,
    var totalSteps: Int? = null
) : RealmObject()