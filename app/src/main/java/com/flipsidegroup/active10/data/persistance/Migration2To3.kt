package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration


class Migration2To3 : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        schema.create("MyWalksMessages")
            .addField("todayNoWalking", String()::class.java, FieldAttribute.REQUIRED)
            .addField("daysTargetHit", String()::class.java, FieldAttribute.REQUIRED)
            .addField("daysTargetNoHit", String()::class.java, FieldAttribute.REQUIRED)
            .addField("daysNoBrisk", String()::class.java, FieldAttribute.REQUIRED)
            .addField("weekCurrent", String()::class.java, FieldAttribute.REQUIRED)
            .addField(
                "lastWeekTargetHitIncreaseTarget",
                String()::class.java,
                FieldAttribute.REQUIRED
            )
            .addField("lastWeek4_6IncreaseTarget", String()::class.java, FieldAttribute.REQUIRED)
            .addField("lastWeekTargetOneDaysOne", String()::class.java, FieldAttribute.REQUIRED)
            .addField("lastWeekTargetXDaysOne", String()::class.java, FieldAttribute.REQUIRED)
            .addField("lastWeekTargetOneDaysX", String()::class.java, FieldAttribute.REQUIRED)
            .addField("lastWeekTargetXDaysX", String()::class.java, FieldAttribute.REQUIRED)
            .addField("weekBrisk150", String()::class.java, FieldAttribute.REQUIRED)
            .addField("weeksDays7", String()::class.java, FieldAttribute.REQUIRED)
            .addField("weeksDays4_6", String()::class.java, FieldAttribute.REQUIRED)
            .addField("weekDays0", String()::class.java, FieldAttribute.REQUIRED)
            .addField("weekDays1", String()::class.java, FieldAttribute.REQUIRED)
            .addField("weekDays2_3", String()::class.java, FieldAttribute.REQUIRED)

        schema.create("MyWalksTargetMessages")
            .addField("noActive0Mins", String()::class.java, FieldAttribute.REQUIRED)
            .addField("noActiveXMins", String()::class.java, FieldAttribute.REQUIRED)
            .addField("oneTarget0Mins", String()::class.java, FieldAttribute.REQUIRED)
            .addField("oneTargetXMins", String()::class.java, FieldAttribute.REQUIRED)
            .addField("twoTargets0Mins", String()::class.java, FieldAttribute.REQUIRED)
            .addField("twoTargetsXMins", String()::class.java, FieldAttribute.REQUIRED)
            .addField("threeTargets0Mins", String()::class.java, FieldAttribute.REQUIRED)
            .addField("threeTargetsXMins", String()::class.java, FieldAttribute.REQUIRED)
            .addField("moreThreeTargets", String()::class.java, FieldAttribute.REQUIRED)

        schema.create("TodayWalkMessages")
            .addRealmObjectField("myWalksTarget1", schema.get("MyWalksTargetMessages"))
            .addRealmObjectField("myWalksTarget2", schema.get("MyWalksTargetMessages"))
            .addRealmObjectField("myWalksTarget3", schema.get("MyWalksTargetMessages"))

        schema.create("WalkingMessageResponse")
            .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
            .addRealmObjectField("myWalksTexts", schema.get("MyWalksMessages"))
            .addRealmObjectField("todayWalkTexts", schema.get("TodayWalkMessages"))
    }
}