package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration15to16 : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        val walksMessagesSchema = schema.get("MyWalksMessages")
        walksMessagesSchema?.apply {
            addField("noRewardsToday", String::class.java, FieldAttribute.REQUIRED)
        }

        val myWalksTargetMessages = schema.get("MyWalksTargetMessages")
        myWalksTargetMessages?.apply {
            addField("fourTargets0Mins", String::class.java, FieldAttribute.REQUIRED)
            addField("fourTargetsXMins", String::class.java, FieldAttribute.REQUIRED)
            addField("fiveTargets0Mins", String::class.java, FieldAttribute.REQUIRED)
            addField("fiveTargetsXMins", String::class.java, FieldAttribute.REQUIRED)
            addField("moreFiveTargets", String::class.java, FieldAttribute.REQUIRED)
        }

        val todayWalkMessages = schema.get("TodayWalkMessages")
        todayWalkMessages?.apply {
            addRealmObjectField("myWalksTarget4", myWalksTargetMessages)
            addRealmObjectField("myWalksTarget5", myWalksTargetMessages)
        }

    }
}