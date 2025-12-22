package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration9to10: RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema
        val onboardingSchema = schema.get("MyWalksMessages")
        onboardingSchema?.let {
            if (!it.hasField("breakdownAccessibilityText")) {
                it.addField(
                    "breakdownAccessibilityText",
                    String::class.java,
                    FieldAttribute.REQUIRED
                )
            }
        }
    }
}