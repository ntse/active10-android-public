package com.flipsidegroup.active10.di.modules

import android.content.Context
import com.flipsidegroup.active10.data.network.AppApi
import com.flipsidegroup.active10.data.network.AppV3Api
import com.flipsidegroup.active10.data.network.LoginApi
import com.flipsidegroup.active10.data.network.NewAppApi
import com.flipsidegroup.active10.data.network.ParagonApi
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.AppRepository
import com.flipsidegroup.active10.data.persistance.AppRepositoryImpl
import com.flipsidegroup.active10.data.persistance.badwords.BadWordsAssetRepository
import com.flipsidegroup.active10.data.persistance.badwords.BadWordsRepository
import com.flipsidegroup.active10.data.persistance.jsonstorage.JsonRepository
import com.flipsidegroup.active10.data.persistance.jsonstorage.JsonRepositoryImpl
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.flipsidegroup.active10.data.persistance.local.LocalRepositoryImpl
import com.flipsidegroup.active10.data.persistance.login.LoginRepository
import com.flipsidegroup.active10.data.persistance.login.LoginRepositoryImpl
import com.flipsidegroup.active10.data.persistance.migration.MigrationRepository
import com.flipsidegroup.active10.data.persistance.migration.MigrationRepositoryImpl
import com.flipsidegroup.active10.data.persistance.newapi.LocalNotificationRepository
import com.flipsidegroup.active10.data.persistance.newapi.NhsSyncRepository
import com.flipsidegroup.active10.data.persistance.newapi.PreferenceRepository
import com.flipsidegroup.active10.data.persistance.newapi.WalkingPlanRepository
import com.flipsidegroup.active10.data.persistance.paragon.ParagonRepository
import com.flipsidegroup.active10.data.persistance.paragon.ParagonRepositoryImpl
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.usecases.RemoveNhsUserDataUseCase
import com.flipsidegroup.active10.utils.MyWalkRewardMessageHelper
import com.flipsidegroup.active10.utils.TodayWalkHeaderHelper
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class RepositoryModule {

    @Provides
    @Singleton
    internal fun provideAppRepository(
        appApi: AppApi,
        appV3Api: AppV3Api,
        newAppApi: NewAppApi,
        localRepository: LocalRepository,
        myWalkRewardMessageHelper: MyWalkRewardMessageHelper,
        todayWalkHeaderHelper: TodayWalkHeaderHelper,
    ): AppRepository {
        return AppRepositoryImpl(
            appApi,
            appV3Api,
            newAppApi,
            localRepository,
            myWalkRewardMessageHelper,
            todayWalkHeaderHelper,
        )
    }

    @Provides
    @Singleton
    internal fun provideJsonRepository(
        context: Context,
        gson: Gson,
        localRepository: LocalRepository,
    ): JsonRepository {
        return JsonRepositoryImpl(
            context,
            gson,
            localRepository,
        )
    }

    @Provides
    @Singleton
    internal fun provideParagonRepository(paragonApi: ParagonApi): ParagonRepository {
        return ParagonRepositoryImpl(paragonApi)
    }

    @Provides
    @Singleton
    internal fun provideBadWordsRepository(context: Context, gson: Gson): BadWordsRepository {
        return BadWordsAssetRepository(context, gson)
    }

    @Provides
    @Singleton
    internal fun provideLocalRepository(appDatabase: AppDatabase): LocalRepository {
        return LocalRepositoryImpl(appDatabase)
    }

    @Provides
    @Singleton
    internal fun provideMigrationRepository(context: Context, gson: Gson): MigrationRepository {
        return MigrationRepositoryImpl(context, gson)
    }

    @Provides
    @Singleton
    internal fun provideLoginRepository(
        localNotificationRepository: LocalNotificationRepository,
        settingsUtils: SettingsUtils,
        loginApi: LoginApi,
        removeNhsUserDataUseCase: RemoveNhsUserDataUseCase,
    ): LoginRepository {
        return LoginRepositoryImpl(
            localNotificationRepository,
            settingsUtils,
            loginApi,
            removeNhsUserDataUseCase
        )
    }

    @Provides
    @Singleton
    internal fun provideWalkingPlanRepository(
        appRepository: AppRepository,
        localRepository: LocalRepository,
        settingsUtils: SettingsUtils,
        loginApi: LoginApi,
        removeNhsUserDataUseCase: RemoveNhsUserDataUseCase,
    ): WalkingPlanRepository {
        return WalkingPlanRepository(
            appRepository,
            localRepository,
            settingsUtils,
            loginApi,
            removeNhsUserDataUseCase,
        )
    }

    @Provides
    @Singleton
    internal fun provideLocalNotificationRepository(
        context: Context,
        localRepository: LocalRepository,
        preferenceRepository: PreferenceRepository,
        settingsUtils: SettingsUtils,
    ): LocalNotificationRepository {
        return LocalNotificationRepository(
            context,
            localRepository,
            preferenceRepository,
            settingsUtils
        )
    }

    @Provides
    @Singleton
    internal fun provideNhsSyncRepository(
        localRepository: LocalRepository,
        loginRepository: LoginRepository,
        settingsUtils: SettingsUtils,
        preferenceRepository: PreferenceRepository,
        walkingPlanRepository: WalkingPlanRepository
    ): NhsSyncRepository {
        return NhsSyncRepository(
            settingsUtils,
            loginRepository,
            localRepository,
            preferenceRepository,
            walkingPlanRepository
        )
    }

}