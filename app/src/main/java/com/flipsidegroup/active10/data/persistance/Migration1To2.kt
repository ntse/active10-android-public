package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration


class Migration1To2 : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema
        val onboardingSchema = schema.get("Onboarding")
        onboardingSchema?.let {
            if (!it.hasField("notifications")) {
                it.addField(
                    "notifications",
                    String::class.java,
                    FieldAttribute.REQUIRED
                )
            }
        }
    }
}