package com.flipsidegroup.active10.di.modules

import android.content.Context
import com.flipsidegroup.active10.data.persistance.AppDatabase
import com.flipsidegroup.active10.data.persistance.DataBaseMigration
import com.flipsidegroup.active10.data.persistance.REALM_SCHEMA_VERSION
import dagger.Module
import dagger.Provides
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Singleton


@Module
class DatabaseModule {

    @Provides
    @Singleton
    internal fun provideDatabase(context: Context): AppDatabase {
        Realm.init(context)

        val config = RealmConfiguration.Builder()
            .schemaVersion(REALM_SCHEMA_VERSION)
            .migration(DataBaseMigration())
            .build()

        return AppDatabase(config)
    }
}