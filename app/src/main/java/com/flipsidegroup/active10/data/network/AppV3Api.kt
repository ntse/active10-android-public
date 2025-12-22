package com.flipsidegroup.active10.data.network

import com.flipsidegroup.active10.data.RewardBadge
import io.reactivex.Single
import retrofit2.http.GET


interface AppV3Api {

    @GET("rewards")
    fun getRewardList(): Single<List<RewardBadge>>
}