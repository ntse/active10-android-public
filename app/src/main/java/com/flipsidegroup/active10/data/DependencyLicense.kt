package com.flipsidegroup.active10.data


data class DependencyLicense(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val name: String?,
    val spdxLicenses: List<License>?,
    val unknownLicenses: List<License>?,
    val scm: Scm?
)

data class License(
    val identifier: String?,
    val name: String?,
    val url: String?
)

data class Scm(
    val url: String?
)

