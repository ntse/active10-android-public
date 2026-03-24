package com.flipsidegroup.active10.di.modules

import android.app.Application
import android.content.Context
import com.flipsidegroup.active10.BuildConfig
import com.flipsidegroup.active10.data.network.AppApi
import com.flipsidegroup.active10.data.network.AppV3Api
import com.flipsidegroup.active10.data.network.GoJauntlyApi
import com.flipsidegroup.active10.data.network.LoginApi
import com.flipsidegroup.active10.data.network.NewAppApi
import com.flipsidegroup.active10.data.network.ParagonApi
import com.flipsidegroup.active10.data.network.PlayIntegrityVerificationApi
import com.flipsidegroup.active10.data.network.interceptors.HostSelectionInterceptor
import com.flipsidegroup.active10.data.network.interceptors.ParagonInterceptor
import com.flipsidegroup.active10.data.persistance.PlayIntegrityValidationRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.StandardIntegrityManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton


@Module
class NetworkModule {

    private fun loadCertificates(): List<String> {
        return listOf(
            BuildConfig.CERTIFICATE_1,
            BuildConfig.CERTIFICATE_2,
            BuildConfig.CERTIFICATE_3,
            BuildConfig.CERTIFICATE_4,
            BuildConfig.CERTIFICATE_5
        ).map { it.trim() }
            .filter { it.isNotEmpty() && !it.equals("dummy", ignoreCase = true) }
    }

    private fun hostFromUrl(url: String): String =
        url.removePrefix("https://")
            .removePrefix("http://")
            .substringBefore("/")

    private fun buildCertificatePinner(): CertificatePinner {
        val pins = loadCertificates().map { "sha256/$it" }
        if (pins.isEmpty()) {
            Timber.w("Certificate pinning disabled: no valid pins configured.")
            return CertificatePinner.DEFAULT
        }

        val builder = CertificatePinner.Builder()
        val appHost = hostFromUrl(BuildConfig.APP_ENDPOINT)
        val nhsHost = hostFromUrl(BuildConfig.NHS_LOGIN_URL)
        if (appHost.isNotBlank()) builder.add(appHost, *pins.toTypedArray())
        if (nhsHost.isNotBlank()) builder.add(nhsHost, *pins.toTypedArray())
        return builder.build()
    }

    private val certificatePinner = buildCertificatePinner()

    @Provides
    @Singleton
    fun provideAppApi(
        retrofitBuilder: Retrofit.Builder,
        client: OkHttpClient
    ): AppApi {
        return retrofitBuilder
            .client(client)
            .baseUrl(BuildConfig.APP_ENDPOINT)
            .build()
            .create(AppApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppV3Api(
        retrofitBuilder: Retrofit.Builder,
        client: OkHttpClient
    ): AppV3Api {
        return retrofitBuilder
            .client(client)
            .baseUrl(BuildConfig.APP_ENDPOINT_V3)
            .build()
            .create(AppV3Api::class.java)
    }

    @Provides
    @Singleton
    fun provideNewAppApi(
        retrofitBuilder: Retrofit.Builder,
        client: OkHttpClient
    ): NewAppApi {
        return retrofitBuilder
            .client(client)
            .baseUrl(BuildConfig.APP_ENDPOINT)
            .build()
            .create(NewAppApi::class.java)
    }

    @Provides
    @Singleton
    fun providePlayIntegrityApi(
        retrofitBuilder: Retrofit.Builder,
        client: OkHttpClient
    ): PlayIntegrityVerificationApi {
        return retrofitBuilder
            .client(client)
            .baseUrl(BuildConfig.APP_ENDPOINT.removeSuffix("active10/"))
            .build()
            .create(PlayIntegrityVerificationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideParagonApi(retrofitBuilder: Retrofit.Builder, client: OkHttpClient): ParagonApi {
        return retrofitBuilder
            .client(client)
            .baseUrl(BuildConfig.PARAGON_ENDPOINT)
            .build()
            .create(ParagonApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLoginApi(retrofitBuilder: Retrofit.Builder, client: OkHttpClient): LoginApi {
        return retrofitBuilder
            .client(client)
            .baseUrl(BuildConfig.NHS_LOGIN_URL)
            .build()
            .create(LoginApi::class.java)
    }

    @Singleton
    @Provides
    fun provideGoJauntlyApi(retrofitBuilder: Retrofit.Builder, client: OkHttpClient): GoJauntlyApi =
        retrofitBuilder
            .client(client)
            .baseUrl(BuildConfig.NHS_LOGIN_URL)
            .build()
            .create(GoJauntlyApi::class.java)

    @Provides
    @Singleton
    fun provideDefaultOkHttpClientBuilder(
        application: Application,
        settingsUtils: SettingsUtils
    ): OkHttpClient.Builder {
        val okHttpBuilder = OkHttpClient.Builder()
            .certificatePinner(certificatePinner)

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.addInterceptor(HostSelectionInterceptor(settingsUtils))
                .addInterceptor(interceptor)
        }

        return okHttpBuilder
    }

    @Provides
    @Singleton
    internal fun provideApiHttpClient(okHttpClientBuilder: OkHttpClient.Builder): OkHttpClient {
        return okHttpClientBuilder.apply {
            addNetworkInterceptor(ParagonInterceptor())
        }.build()
    }

    @Provides
    @Singleton
    internal fun provideConversionFactory(): Converter.Factory {
        return GsonConverterFactory.create(Gson())
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(factory: Converter.Factory): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(factory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }

    @Provides
    @Singleton
    fun provideIntegrityManager(applicationContext: Context): StandardIntegrityManager = IntegrityManagerFactory
        .createStandard(applicationContext)


    @Provides
    @Singleton
    fun providePlayIntegrityValidationRepository(
        api: PlayIntegrityVerificationApi,
        integrityManager: StandardIntegrityManager,
        preferenceRepository: PreferenceRepository,
    ) = PlayIntegrityValidationRepository(api, integrityManager, preferenceRepository)

    @Provides
    fun provideLoggingInterceptor() =
        HttpLoggingInterceptor { Timber.i(it) }.apply {
            level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
        }

}
