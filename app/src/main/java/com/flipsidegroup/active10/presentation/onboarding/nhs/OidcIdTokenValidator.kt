package com.flipsidegroup.active10.presentation.onboarding.nhs

import android.util.Base64
import org.json.JSONObject
import timber.log.Timber

object OidcIdTokenValidator {

    fun hasExpectedNonce(idToken: String?, expectedNonce: String): Boolean {
        if (idToken.isNullOrBlank()) {
            Timber.w("OIDC login failed: ID token missing")
            return false
        }

        val payload = idToken.split(".").getOrNull(1)
        if (payload.isNullOrBlank()) {
            Timber.w("OIDC login failed: ID token payload missing")
            return false
        }

        return try {
            val decodedPayload = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            val nonce = JSONObject(String(decodedPayload, Charsets.UTF_8)).optString("nonce", null)
            nonce == expectedNonce
        } catch (error: Exception) {
            Timber.w(error, "OIDC login failed: ID token nonce could not be read")
            false
        }
    }
}
