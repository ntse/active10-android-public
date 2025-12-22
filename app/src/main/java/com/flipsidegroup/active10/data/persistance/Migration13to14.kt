package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration13to14 : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        val rewardBadge = schema.get("RewardBadge")

        rewardBadge?.addField(
            "isEarned",
            Boolean::class.java,
            FieldAttribute.REQUIRED
        )
    }
}