package com.flipsidegroup.active10.data.network.interceptors

import com.flipsidegroup.active10.data.preferences.SettingsUtils
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


private const val PROD_HOST = "active10.prod.phedigital.co.uk"
private const val DEV_HOST = "active10.stg.phedigital.co.uk"

class HostSelectionInterceptor(val settingsUtils: SettingsUtils) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()

        val host = getAppApiEndpoint()

        if (request.url.host == PROD_HOST
            || request.url.host == DEV_HOST
        ) {
            val newUrl: HttpUrl = request.url.newBuilder()
                .host(host)
                .build()
            request = request.newBuilder()
                .url(newUrl)
                .build()
        }
        return chain.proceed(request)
    }

    private fun getAppApiEndpoint(): String {
        if (settingsUtils.getSettingsHolder().useProdCMS == true) {
            return PROD_HOST
        }

        return DEV_HOST
    }
}