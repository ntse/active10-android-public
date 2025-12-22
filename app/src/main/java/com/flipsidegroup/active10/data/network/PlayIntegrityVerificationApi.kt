package com.flipsidegroup.active10.data.network

import com.flipsidegroup.active10.data.models.IntegrityVerdictWrapper
import com.flipsidegroup.active10.data.models.ValidateTokenRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface PlayIntegrityVerificationApi {

    @POST("new-check-integrity-token")
    suspend fun validateToken(@Body validateTokenRequest: ValidateTokenRequest) : IntegrityVerdictWrapper

}