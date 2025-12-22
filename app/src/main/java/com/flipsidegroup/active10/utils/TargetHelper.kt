package com.flipsidegroup.active10.utils

import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.models.dataholders.TargetHolder
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.targets.activities.DEFAULT_TARGET
import java.util.*


object TargetHelper {

    fun initTargetIfEmpty(settingsUtils: SettingsUtils) {
        val isTargetEmpty = settingsUtils.getSettingsHolder().targetList.isNullOrEmpty()
        if (isTargetEmpty) {
            initTarget(settingsUtils)
        }
    }

    private fun initTarget(settingsUtils: SettingsUtils) {
        val newTargets = ArrayList<TargetHolder>()
        newTargets.add(TargetHolder(DEFAULT_TARGET))
        settingsUtils.updateSettings(SettingsDataHolder(targetList = newTargets))
    }
}