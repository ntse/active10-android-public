package com.flipsidegroup.active10.data.persistance.paragon

import com.flipsidegroup.active10.data.models.requests.AddUserRequest
import com.flipsidegroup.active10.data.network.ParagonApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers


class ParagonRepositoryImpl(private val paragonApi: ParagonApi) : ParagonRepository {

    override fun addUser(request: AddUserRequest): Observable<String> {
        return paragonApi.addUser(request)
            .subscribeOn(Schedulers.io())
    }
}