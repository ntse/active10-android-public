package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration8to9: RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema
        val onboardingSchema = schema.get("Goal")
        onboardingSchema?.let {
            if (!it.hasField("isCustomGoal")) {
                it.addField(
                    "isCustomGoal",
                    Boolean::class.java,
                    FieldAttribute.REQUIRED
                )
            }
            if (!it.hasField("isSelected")) {
                it.addField(
                    "isSelected",
                    Boolean::class.java,
                    FieldAttribute.REQUIRED
                )
            }
        }

    }
}