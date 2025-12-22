package com.flipsidegroup.active10.data.models

data class IntegrityVerdictWrapper(
    val tokenPayloadExternal: IntegrityVerdict
)

data class IntegrityVerdict(
    val requestDetails: RequestDetails,
    val appIntegrity: AppIntegrity? = null,
    val deviceIntegrity: DeviceIntegrity,
    val accountDetails: AccountDetails? = null,
    val environmentDetails: EnvironmentDetails? = null
)

data class RequestDetails(
    val requestPackageName: String,
    val requestHash: String? = null,
    val nonce: String,
    val timestampMillis: Long
)

data class AppIntegrity(
    val appRecognitionVerdict: String,
    val packageName: String? = null,
    val certificateSha256Digest: List<String>? = null,
    val versionCode: String? = null
)

data class DeviceIntegrity(
    val deviceRecognitionVerdict: List<String> = emptyList()
)

data class AccountDetails(
    val appLicensingVerdict: String
)

data class EnvironmentDetails(
    val playProtectVerdict: String
)