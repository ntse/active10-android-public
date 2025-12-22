package com.flipside.briskcounter.internal

import com.flipside.briskcounter.data.BriskActivity

interface ActivityListener {
    fun onSuccess(briskActivity: BriskActivity)
    fun onFailure(activityError: String)
}