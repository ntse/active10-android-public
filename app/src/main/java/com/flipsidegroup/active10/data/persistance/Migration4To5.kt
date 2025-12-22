package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration


class Migration4To5 : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        // Notifications
        schema.create("NotificationUserInfo")
            .addField("action", String()::class.java, FieldAttribute.REQUIRED)
            .addField("notSameDay", String()::class.java)

        schema.create("OnboardingNotifications")
            .addField("day", Int::class.java, FieldAttribute.REQUIRED)
            .addField("copy", String()::class.java, FieldAttribute.REQUIRED)
            .addRealmObjectField("userInfo", schema.get("NotificationUserInfo")!!)

        schema.create("LapsedNotifications")
            .addField("ident", String()::class.java, FieldAttribute.REQUIRED)
            .addField("copy", String()::class.java, FieldAttribute.REQUIRED)
            .addRealmObjectField("userInfo", schema.get("NotificationUserInfo")!!)

        schema.create("Notifications")
            .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
            .addRealmListField("onboardingNotifications", schema.get("OnboardingNotifications")!!)
            .addRealmListField("lapsedNotifications", schema.get("LapsedNotifications")!!)
            .addField("reminder", String()::class.java, FieldAttribute.REQUIRED)

        // Global rules
        schema.create("TermsAndConditionsLinks")
            .addField("url", String()::class.java, FieldAttribute.REQUIRED)
            .addField("link", String()::class.java, FieldAttribute.REQUIRED)

        schema.create("TermsConditions")
            .addField("latestVersion", String()::class.java, FieldAttribute.REQUIRED)
            .addField("title", String()::class.java, FieldAttribute.REQUIRED)
            .addField("text", String()::class.java, FieldAttribute.REQUIRED)
            .addField("button", String()::class.java, FieldAttribute.REQUIRED)
            .addField("agree", String()::class.java, FieldAttribute.REQUIRED)
            .addRealmListField("links", schema.get("TermsAndConditionsLinks")!!)

        schema.create("GlobalRulesMissingData")
            .addField("title", String()::class.java, FieldAttribute.REQUIRED)
            .addField("text", String()::class.java, FieldAttribute.REQUIRED)

        schema.create("GlobalRulesAppAndroid")
            .addField("latestVersion", String()::class.java, FieldAttribute.REQUIRED)
            .addField("title", String()::class.java, FieldAttribute.REQUIRED)
            .addField("text", String()::class.java, FieldAttribute.REQUIRED)
            .addField("button", String()::class.java, FieldAttribute.REQUIRED)

        schema.create("GlobalRulesApp")
            .addRealmObjectField("android", schema.get("GlobalRulesAppAndroid")!!)

        schema.create("GlobalRules")
            .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
            .addRealmObjectField("termsAndConditions", schema.get("TermsConditions")!!)
            .addRealmObjectField("missingData", schema.get("GlobalRulesMissingData")!!)
            .addRealmObjectField("app", schema.get("GlobalRulesApp")!!)

        // Discover Splash
        val onboardingSchema = schema.get("DiscoverSplash")
        onboardingSchema?.let {
            if (!it.hasField("isApp")) {
                it.addField(
                    "isApp",
                    Boolean::class.java,
                    FieldAttribute.REQUIRED
                )
            }
            if (!it.hasField("androidLink")) {
                it.addField(
                    "androidLink",
                    String()::class.java
                )
            }
        }
    }
}