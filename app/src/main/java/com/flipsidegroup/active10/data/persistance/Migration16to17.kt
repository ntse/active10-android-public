package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration16to17 : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        schema.create("PlanItineraryItem")
            .addField("id", Long::class.java, FieldAttribute.PRIMARY_KEY)
            .addField("weekLabel", String()::class.java, FieldAttribute.REQUIRED)
            .addField("descriptionOfWeeklyTask", String()::class.java, FieldAttribute.REQUIRED)
            .addField("totalBriskMinutes", Int::class.java, FieldAttribute.REQUIRED)
            .addRealmListField("dailyBriskMinutes", Int::class.java)
            .addField("totalNonBriskMinutes", Int::class.java, FieldAttribute.REQUIRED)
            .addRealmListField("dailyNonBriskMinutes", Int::class.java)

        schema.create("CategoryDetails")
            .addRealmListField("sex", String()::class.java)
            .addRealmListField("age", String()::class.java)
            .addRealmListField("activityLevel", String()::class.java)
            .addRealmListField("motivation", String()::class.java)

        schema.create("WalkingPlan")
            .addField("id", Long::class.java, FieldAttribute.PRIMARY_KEY)
            .addField("image", String()::class.java)
            .addField("planName", String()::class.java, FieldAttribute.REQUIRED)
            .addField("planCode", String()::class.java, FieldAttribute.REQUIRED)
            .addField("planTagline", String()::class.java, FieldAttribute.REQUIRED)
            .addField("planDescription", String()::class.java, FieldAttribute.REQUIRED)
            .addField("planDuration", String()::class.java, FieldAttribute.REQUIRED)
            .addField("planDurationWeeks", Int::class.java, FieldAttribute.REQUIRED)
            .addField("planBriskMinutesPerDay", String::class.java, FieldAttribute.REQUIRED)
            .addField("planGoal", String::class.java, FieldAttribute.REQUIRED)
            .addField("planDifficulty", String::class.java, FieldAttribute.REQUIRED)
            .addRealmListField("planItineraryItems", schema.get("PlanItineraryItem")!!)
            .addRealmObjectField("categoryDetails", schema.get("CategoryDetails")!!)
            .addRealmListField("inferiorPlanIds", Long::class.java)
            .addRealmListField("superiorPlanIds", Long::class.java)

        schema.create("LocalNotification")
            .addField("slug", String()::class.java, FieldAttribute.REQUIRED)
            .addField("title", String()::class.java, FieldAttribute.REQUIRED)
            .addField("description", String()::class.java, FieldAttribute.REQUIRED)
            .addField("destination", String()::class.java, FieldAttribute.REQUIRED)
            .addField("isLapsed", Boolean::class.java, FieldAttribute.REQUIRED)

        schema.get("StepOverview")
            ?.addField("totalSteps", Int::class.java)
            ?.setNullable("totalSteps", true)

        schema.create("CurrentWalkingPlanDay")
            .addField("timestamp", String::class.java, FieldAttribute.REQUIRED)
            .addField("totalBriskMin", Int::class.java, FieldAttribute.REQUIRED)
            .addField("totalNonBriskMin", Int::class.java, FieldAttribute.REQUIRED)
            .addField("totalSteps", Int::class.java, FieldAttribute.REQUIRED)
            .isEmbedded = true

        schema.create("PauseResume")
            .addField("paused", String::class.java, FieldAttribute.REQUIRED)
            .addField("resumed", String::class.java, FieldAttribute.REQUIRED)
            .isEmbedded = true

        schema.create("CurrentWalkingPlan")
            .addField("planId", Long::class.java, FieldAttribute.REQUIRED)
            .addField("startDate", String::class.java, FieldAttribute.REQUIRED)
            .addField("state", String::class.java, FieldAttribute.REQUIRED)
            .addRealmListField("days", schema.get("CurrentWalkingPlanDay")!!)
            .addRealmListField("pauseResume", schema.get("PauseResume")!!)
            .isEmbedded = true

        schema.create("HistoricalWalkingPlan")
            .addField("planId", Long::class.java, FieldAttribute.REQUIRED)
            .addField("startDate", String::class.java, FieldAttribute.REQUIRED)
            .addField("endDate", String::class.java, FieldAttribute.REQUIRED)
            .addField("status", String::class.java, FieldAttribute.REQUIRED)
            .isEmbedded = true

        schema.create("WalkingPlanEntity")
            .addField("id", Long::class.java, FieldAttribute.PRIMARY_KEY)
            .addRealmObjectField("currentWalkingPlan", schema.get("CurrentWalkingPlan")!!)
            .addRealmListField("walkingPlanHistory", schema.get("HistoricalWalkingPlan")!!)

        schema.get("RewardBadge")
            ?.addField("repetitions", Int::class.java, FieldAttribute.REQUIRED)

        schema.get("Notifications")
            ?.addRealmListField("localNotifications", schema.get("LocalNotification")!!)

        schema.get("Goal")
            ?.addField("order", Int::class.java, FieldAttribute.REQUIRED)
    }
}