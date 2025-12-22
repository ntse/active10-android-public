package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration12to13 : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        val legalRulesSchema = schema.get("LegalRules")
        legalRulesSchema?.removePrimaryKey()
        legalRulesSchema?.addField(
            "id",
            Int::class.java,
            FieldAttribute.REQUIRED
        )
    }
}