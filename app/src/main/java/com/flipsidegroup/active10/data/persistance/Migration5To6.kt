package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration5To6 : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema
        schema.create("RewardBadge").addField(
            "id",
            Int::class.java,
            FieldAttribute.PRIMARY_KEY,
            FieldAttribute.REQUIRED
        ).addField("title", String()::class.java, FieldAttribute.REQUIRED)
            .addField("slug", String()::class.java, FieldAttribute.REQUIRED)
            .addField("text", String()::class.java, FieldAttribute.REQUIRED)
            .addField("category", String()::class.java, FieldAttribute.REQUIRED)
            .addField("onImage", String()::class.java, FieldAttribute.REQUIRED)
            .addField("offImage", String()::class.java, FieldAttribute.REQUIRED)
            .addField("animation", String()::class.java, FieldAttribute.REQUIRED)
            .addField("howTo", String()::class.java, FieldAttribute.REQUIRED)
            .addField("share", String()::class.java, FieldAttribute.REQUIRED)
            .addField("position", Int::class.java, FieldAttribute.REQUIRED)


        val walksMessagesSchema = schema.get("MyWalksMessages")
        walksMessagesSchema?.let {
            if (!it.hasField("noRewardsThisDay")) {
                it.addField(
                    "noRewardsThisDay",
                    String::class.java,
                    FieldAttribute.REQUIRED
                )
            }
            if (!it.hasField("noRewardsThisWeek")) {
                it.addField(
                    "noRewardsThisWeek",
                    String::class.java,
                    FieldAttribute.REQUIRED
                )
            }
            if (!it.hasField("noRewardsThisMonth")) {
                it.addField(
                    "noRewardsThisMonth",
                    String::class.java,
                    FieldAttribute.REQUIRED
                )
            }
        }

    }
}