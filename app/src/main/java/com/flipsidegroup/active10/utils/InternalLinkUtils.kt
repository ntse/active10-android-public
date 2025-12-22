package com.flipsidegroup.active10.utils

import android.net.Uri

object InternalLinkUtils {

    private const val APP_SCHEME = "active10"
    private const val NHS_LOGIN_HOST = "nhs_login"

    enum class Destination {
        SIGN_IN,
        UNKNOWN
    }

    fun resolveDestination(uri: Uri): Destination {
        if (uri.scheme == APP_SCHEME && uri.host == NHS_LOGIN_HOST) {
            val destination = uri.pathSegments.getOrNull(0)
            return when (destination) {
                "sign_in" -> Destination.SIGN_IN
                else -> Destination.UNKNOWN
            }
        }
        return Destination.UNKNOWN
    }

}