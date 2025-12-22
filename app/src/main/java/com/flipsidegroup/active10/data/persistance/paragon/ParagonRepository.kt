package com.flipsidegroup.active10.data.persistance.paragon

import com.flipsidegroup.active10.data.models.requests.AddUserRequest
import io.reactivex.Observable


interface ParagonRepository {

    fun addUser(request: AddUserRequest): Observable<String>
}