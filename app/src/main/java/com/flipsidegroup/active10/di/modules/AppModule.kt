package com.flipsidegroup.active10.di.modules

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Context
import android.location.Geocoder
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.utils.DateDeserializer
import com.flipsidegroup.active10.utils.DialogUtils
import com.flipsidegroup.active10.utils.DialogUtilsImpl
import com.flipsidegroup.active10.utils.MyWalkRewardMessageHelper
import com.flipsidegroup.active10.utils.TodayWalkHeaderHelper
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import java.util.Date
import javax.inject.Singleton


@Module
class AppModule {

    @Provides
    @Singleton
    internal fun provideContext(application: Application): Context = application

    @Provides
    @Singleton
    internal fun provideSettingsUtils(context: Context, gson: Gson) = SettingsUtils(context, gson)

    @Provides
    @Singleton
    internal fun provideFirebaseAnalyticsHelper(
        settingsUtils: SettingsUtils,
        preferenceRepository: PreferenceRepository
    ) = FirebaseAnalyticsHelper(settingsUtils, preferenceRepository)

    @Provides
    @Singleton
    internal fun provideAppGson() = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateDeserializer())
        .create()

    @Provides
    @Singleton
    internal fun provideTodayWalkHeaderHelper(settingsUtils: SettingsUtils) =
        TodayWalkHeaderHelper(settingsUtils)

    @Provides
    @Singleton
    internal fun provideMyWalkRewardMessageHelper(
        settingsUtils: SettingsUtils,
        todayWalkHeaderHelper: TodayWalkHeaderHelper
    ) =
        MyWalkRewardMessageHelper(settingsUtils, todayWalkHeaderHelper)

    @Provides
    @Singleton
    internal fun provideDialogUtils(
        settingsUtils: SettingsUtils,
        firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
        preferenceRepository: PreferenceRepository
    ): DialogUtils = DialogUtilsImpl(settingsUtils, firebaseAnalyticsHelper, preferenceRepository)


    @Provides
    @Singleton
    internal fun provideAppWidgetManager(context: Context): AppWidgetManager {
        return AppWidgetManager.getInstance(context)
    }

    @Provides
    @Singleton
    internal fun provideGeocoder(context: Context): Geocoder? {
        return if (Geocoder.isPresent()) Geocoder(context) else null
    }
}
