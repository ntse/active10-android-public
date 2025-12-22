package com.flipsidegroup.active10.data.network

import com.flipsidegroup.active10.data.models.requests.AddUserRequest
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST


interface ParagonApi {

    @POST("AddUser")
    fun addUser(@Body request: AddUserRequest): Observable<String>
}