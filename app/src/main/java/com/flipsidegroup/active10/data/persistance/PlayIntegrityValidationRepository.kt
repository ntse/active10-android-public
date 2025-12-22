package com.flipsidegroup.active10.data.persistance

import android.util.Base64
import com.flipsidegroup.active10.data.models.IntegrityVerdict
import com.flipsidegroup.active10.data.models.ValidateTokenRequest
import com.flipsidegroup.active10.data.network.PlayIntegrityVerificationApi
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.utils.GlobalUIEvents
import com.google.android.play.core.integrity.StandardIntegrityException
import com.google.android.play.core.integrity.StandardIntegrityManager
import com.google.android.play.core.integrity.StandardIntegrityManager.PrepareIntegrityTokenRequest
import com.google.android.play.core.integrity.StandardIntegrityManager.StandardIntegrityTokenRequest
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayIntegrityValidationRepository @Inject constructor(
    private val api: PlayIntegrityVerificationApi,
    private val integrityManager: StandardIntegrityManager,
    private val preferenceRepository: PreferenceRepository
) {

    private val cacheDurationMillis = 1 * 60 * 60 * 1000L // 1h


    fun launchIntegrityCheck() {
        CoroutineScope(Dispatchers.IO).launch {
            val now = System.currentTimeMillis()
            val lastCheck = preferenceRepository.integrityCheckTimestamp
            val lastResult = preferenceRepository.integrityCheckResult
            if (now - lastCheck < cacheDurationMillis) {
                if (!lastResult) {
                    GlobalUIEvents.showRootDetectedDialog.tryEmit(Unit)
                }
                Timber.d("Root detection - used cache!")
                return@launch
            }

            try {
                val integrityTokenProvider = integrityManager.prepareIntegrityToken(
                    PrepareIntegrityTokenRequest.builder()
                        .setCloudProjectNumber(594558244892)
                        .build()
                ).await()

                val requestHash = generateHash()

                val request = StandardIntegrityTokenRequest.builder()
                    .setRequestHash(requestHash)
                    .build()

                val response = integrityTokenProvider.request(request).await()

                val token = response.token()

                val verdict = api.validateToken(ValidateTokenRequest(token)).tokenPayloadExternal

                preferenceRepository.integrityCheckTimestamp = now

                val result = verifyVerdict(verdict, requestHash)
                preferenceRepository.integrityCheckResult = result

                if (!result) {
                    delay(2000)
                    GlobalUIEvents.showRootDetectedDialog.tryEmit(Unit)
                    FirebaseCrashlytics.getInstance().log("RootDetection - online verification - not verified")
                }

            } catch (e: Exception) {
                if (e.isQuotaExceededError()) {
                    Timber.w("Google Play Integrity quota exceeded")
                }

                if (e.isStandardIntegrityException()) {
                    Timber.d(e)
                }

            }

        }
    }

    private fun Exception.isQuotaExceededError(): Boolean {
        return this is HttpException && this.code() == 429
    }


    private fun Exception.isStandardIntegrityException(): Boolean {
        return this is StandardIntegrityException
    }

    private fun verifyVerdict(verdict: IntegrityVerdict, requestHash: String): Boolean {
        return deviceMeetsIntegrity(verdict)
                && requestHashIsValid(verdict, requestHash)
    }

    private fun requestHashIsValid(verdict: IntegrityVerdict, requestHash: String): Boolean {
        return verdict.requestDetails.requestHash == requestHash
    }

    private fun deviceMeetsIntegrity(verdict: IntegrityVerdict): Boolean {
        return verdict.deviceIntegrity.deviceRecognitionVerdict.toSet()
            .intersect(setOf("MEETS_BASIC_INTEGRITY", "MEETS_DEVICE_INTEGRITY")).isNotEmpty()
    }

    // Generate a nonce for the integrity token request - encoded as a base64 web-safe no-wrap string
    private fun generateHash(): String {
        val nonce = ByteArray(32)
        val random = java.security.SecureRandom()
        random.nextBytes(nonce)
        return Base64.encodeToString(
            nonce,
            Base64.URL_SAFE or Base64.NO_WRAP
        )
    }
}