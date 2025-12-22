package com.flipsidegroup.active10.presentation.todaywalk.interfaces

import com.flipside.briskcounter.data.BriskActivity


interface StepDataListener {

    fun onStepDataReceived(briskActivity: BriskActivity)

    fun onStepDataError(errorMessage: String)
}