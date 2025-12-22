package com.flipsidegroup.active10.data.models

import com.google.gson.annotations.SerializedName


data class MigrationDataResponse(
    @SerializedName("has_received_migration_data") val hasReceivedMigrationData: Boolean,
    @SerializedName("has_migrated") val hasMigrated: Boolean
)