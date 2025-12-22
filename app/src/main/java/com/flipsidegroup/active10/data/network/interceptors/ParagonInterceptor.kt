package com.flipsidegroup.active10.data.network.interceptors

import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.utils.Constants
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class ParagonInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request()!!.newBuilder()

        builder.header(Constants.Paragon.HEADER_KEY, BuildConfig.PARAGON_HEADER)

        return chain.proceed(builder.build())
    }
}