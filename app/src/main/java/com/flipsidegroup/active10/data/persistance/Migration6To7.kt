package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration


class Migration6To7 : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        schema.create("AccessibilityStatement")
            .addField("id", Int::class.java, FieldAttribute.REQUIRED)
            .addField("device", String()::class.java, FieldAttribute.REQUIRED)
            .addField("text", String()::class.java, FieldAttribute.REQUIRED)

        val globalRulesSchema = schema.get("GlobalRules")
        globalRulesSchema?.addRealmListField(
            "accessibilityStatements",
            schema.get("AccessibilityStatement")!!
        )

    }
}