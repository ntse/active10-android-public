package com.flipsidegroup.active10.utils

import android.net.Uri
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object PkceUtils {

    private const val CODE_VERIFIER_NUM_BYTES = 64
    private val secureRandom = SecureRandom()

    fun generateCodeVerifier(): String {
        val randomBytes = ByteArray(CODE_VERIFIER_NUM_BYTES)
        secureRandom.nextBytes(randomBytes)
        return randomBytes.toBase64Url()
    }

    fun createCodeChallenge(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(codeVerifier.toByteArray(Charsets.US_ASCII)).toBase64Url()
    }

    fun generateState(): String {
        val randomBytes = ByteArray(32)
        secureRandom.nextBytes(randomBytes)
        return randomBytes.toBase64Url()
    }

    fun buildLoginUrl(
        baseUrl: String,
        path: String,
        codeChallenge: String,
        redirectUri: String,
        clientId: String,
        state: String
    ): String {
        return Uri.parse(baseUrl).buildUpon()
            .appendEncodedPath(path)
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("code_challenge", codeChallenge)
            .appendQueryParameter("code_challenge_method", "S256")
            .appendQueryParameter("state", state)
            .build()
            .toString()
    }

    private fun ByteArray.toBase64Url(): String {
        return Base64.encodeToString(this, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}
