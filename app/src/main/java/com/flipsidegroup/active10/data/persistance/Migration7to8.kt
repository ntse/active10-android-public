package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration7to8 : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema
        val onboardingSchema = schema.get("Onboarding")
        onboardingSchema?.let {
            if (!it.hasField("aboutYou")) {
                it.addField(
                    "aboutYou",
                    String::class.java,
                    FieldAttribute.REQUIRED
                )
            }
        }

    }
}