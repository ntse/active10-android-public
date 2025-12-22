package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration11to12 : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        val legalContent = schema.get("LegalContent")
        if (legalContent == null) {
            schema.create("LegalContent")
                .addField("type", String()::class.java, FieldAttribute.REQUIRED)
                .addField("body", String()::class.java, FieldAttribute.REQUIRED)
        }

        val communityAppSchema = schema.get("LegalRules")
        if (communityAppSchema == null) {
            schema.create("LegalRules")
                .addField("pageType", String()::class.java, FieldAttribute.REQUIRED)
                .addField("version", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("title", String()::class.java, FieldAttribute.REQUIRED)
                .addRealmListField("content", schema.get("LegalContent")!!)
        }
    }
}