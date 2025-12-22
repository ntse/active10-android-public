package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName

data class MigrationData(@SerializedName("activity") val migrationActivity: List<MigrationActivity> = ArrayList())