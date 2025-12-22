package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration10to11: RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema
        val discoverSchema = schema.get("DiscoverTip")
        discoverSchema?.let {
            if (!it.hasField("discoverOrder")) {
                it.addField(
                    "discoverOrder",
                    Int::class.java,
                    FieldAttribute.REQUIRED
                )
            }
        }
    }
}