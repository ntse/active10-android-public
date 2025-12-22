package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.RealmMigration



const val REALM_SCHEMA_VERSION = 17L

class DataBaseMigration : RealmMigration {
    private val migrationFactory = MigrationFactory()

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        for (i in oldVersion until REALM_SCHEMA_VERSION) {
            migrationFactory.getMigrationWithIndex(i.toInt() - 1)
                .migrate(realm, oldVersion, newVersion)
        }
    }
}