package com.flipsidegroup.active10.utils

object VersionHelper {

    fun isCurrentVersionUpdated(
        currentVersionName: String,
        latestVersionName: String
    ): Boolean {
        if (latestVersionName.isEmpty() || currentVersionName == latestVersionName) {
            return true
        }

        val currentVersions =
            currentVersionName.substringBefore("-").split(".").map { it.toIntOrNull() ?: 0 }
        val latestVersions =
            latestVersionName.substringBefore("-").split(".").map { it.toIntOrNull() ?: 0 }
        val latestVersionsSize = latestVersions.size
        for (i in currentVersions.indices) {
            if (i >= latestVersionsSize) {
                return true
            }

            if (currentVersions[i] > latestVersions[i]) {
                return true
            } else if (currentVersions[i] < latestVersions[i]) {
                return false
            }
        }

        if (latestVersionsSize > currentVersions.size) {
            return false
        }

        val currentSuffix = currentVersionName.substringAfter("-")
        val latestSuffix = latestVersionName.substringAfter("-")
        return currentSuffix >= latestSuffix
    }
}