package com.flipsidegroup.active10.data

import android.content.Context
import android.content.SharedPreferences
import com.flipsidegroup.active10.Active10App


private const val PERMISSION_PREFERENCES = "permission_sh_preferences"
private const val FITNESS_PERMISSION_KEY = "FITNESS_PERMISSION_KEY"
private const val LOCATION_PERMISSION_KEY = "LOCATION_PERMISSION_KEY"

object PermissionPreferences {

    fun saveFitnessPermissionChoice(isAllowed: Boolean) {
        getPermissionPreferences().edit()
            .putBoolean(FITNESS_PERMISSION_KEY, isAllowed)
            .apply()
    }

    fun saveLocationPermissionChoice(isAllowed: Boolean) {
        getPermissionPreferences().edit()
            .putBoolean(LOCATION_PERMISSION_KEY, isAllowed)
            .apply()
    }

    fun isFitnessPermissionAllowed(): Boolean =
        getPermissionPreferences().getBoolean(FITNESS_PERMISSION_KEY, false)

    fun isLocationPermissionAllowed(): Boolean =
        getPermissionPreferences().getBoolean(LOCATION_PERMISSION_KEY, false)

    fun clearSession() {
        getPermissionPreferences().edit()
            .clear()
            .apply()
    }

    private fun getPermissionPreferences(): SharedPreferences {
        return Active10App.instance
            .applicationContext
            .getSharedPreferences(PERMISSION_PREFERENCES, Context.MODE_PRIVATE)
    }
}
