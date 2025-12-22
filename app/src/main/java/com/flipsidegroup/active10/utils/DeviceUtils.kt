package com.flipsidegroup.active10.utils

import com.flipsidegroup.active10.data.models.dataholders.SettingsDataHolder
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import java.util.*

object DeviceUtils {

    fun setDeviceIdIfNotExisting(settingsUtils: SettingsUtils) {
        val savedDeviceId = settingsUtils.getSettingsHolder().deviceId
        if (savedDeviceId == null) {
            val randomUUID = UUID.randomUUID().toString()
            settingsUtils.updateSettings(SettingsDataHolder(deviceId = randomUUID))
        }
    }

    fun getDeviceId(settingsUtils: SettingsUtils): String {
        val savedDeviceId = settingsUtils.getSettingsHolder().deviceId
        return if (savedDeviceId == null) {
            val randomUUID = UUID.randomUUID().toString()
            settingsUtils.updateSettings(SettingsDataHolder(deviceId = randomUUID))
            randomUUID
        } else {
            savedDeviceId
        }
    }
}