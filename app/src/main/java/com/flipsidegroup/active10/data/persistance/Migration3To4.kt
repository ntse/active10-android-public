package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration


class Migration3To4 : RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        var aboutCommunityDescriptionSchema = schema.get("AboutCommunityDescription")
        if (aboutCommunityDescriptionSchema == null) {
            aboutCommunityDescriptionSchema = schema.create("AboutCommunityDescription")
                .addField("text", String()::class.java, FieldAttribute.REQUIRED)
        }

        var communityAppSchema = schema.get("CommunityApp")
        if (communityAppSchema == null) {
            communityAppSchema = schema.create("CommunityApp")
                .addField("appName", String()::class.java, FieldAttribute.REQUIRED)
                .addField("appDescription", String()::class.java, FieldAttribute.REQUIRED)
                .addField("storeLink", String()::class.java, FieldAttribute.REQUIRED)
                .addField("icon", String()::class.java, FieldAttribute.REQUIRED)
        }

        if (schema.get("AboutCommunity") == null) {
            schema.create("AboutCommunity")
                .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addRealmObjectField(
                    "communityDescription",
                    aboutCommunityDescriptionSchema!!
                )
                .addRealmListField("appList", communityAppSchema!!)
        }

        var discoverSplashSchema = schema.get("DiscoverSplash")
        if (discoverSplashSchema == null) {
            discoverSplashSchema = schema.create("DiscoverSplash")
                .addField("splashTitle", String()::class.java, FieldAttribute.REQUIRED)
                .addField("splashText", String()::class.java, FieldAttribute.REQUIRED)
                .addField("splashButton", String()::class.java, FieldAttribute.REQUIRED)
                .addField("splashLink", String()::class.java, FieldAttribute.REQUIRED)
        }

        var discoverSchema = schema.get("Discover")
        if (discoverSchema == null) {
            discoverSchema = schema.create("Discover")
                .addField("discoverId", Int::class.java, FieldAttribute.REQUIRED)
                .addField("discoverDescription", String()::class.java, FieldAttribute.REQUIRED)
                .addField("discoverAction", String()::class.java, FieldAttribute.REQUIRED)
                .addField("discoverColor", String()::class.java, FieldAttribute.REQUIRED)
                .addField("borderColour", String()::class.java, FieldAttribute.REQUIRED)
                .addField("discoverNameType", String()::class.java)
                .addField("discoverNameText", String()::class.java)
                .addField("discoverNameImageUrl", String()::class.java)
                .addField("discoverNameImage", String()::class.java)
                .addField("discoverAltText", String()::class.java)
                .addField("discoverListOrder", Int::class.java, FieldAttribute.REQUIRED)
                .addRealmObjectField("discoverSplash", discoverSplashSchema!!)
        }

        var discoverTipCtaSchema = schema.get("DiscoverTipCta")
        if (discoverTipCtaSchema == null) {
            discoverTipCtaSchema = schema.create("DiscoverTipCta")
                .addField("buttonCTA", String()::class.java, FieldAttribute.REQUIRED)
                .addField("link", String()::class.java, FieldAttribute.REQUIRED)
        }

        var discoverTipSchema = schema.get("DiscoverTip")
        if (discoverTipSchema == null) {
            discoverTipSchema = schema.create("DiscoverTip")
                .addField("id", Int::class.java, FieldAttribute.REQUIRED)
                .addField("image", String()::class.java, FieldAttribute.REQUIRED)
                .addField("title", String()::class.java, FieldAttribute.REQUIRED)
                .addField("message", String()::class.java, FieldAttribute.REQUIRED)
                .addField("fulltext", String()::class.java, FieldAttribute.REQUIRED)
                .addRealmObjectField("discoverTipCta", discoverTipCtaSchema!!)
        }

        if (schema.get("DiscoverResponse") == null) {
            schema.create("DiscoverResponse")
                .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addRealmListField("discoverList", discoverSchema!!)
                .addRealmListField("tipsList", discoverTipSchema!!)
        }

        if (schema.get("FaqItem") == null) {
            schema.create("FaqItem")
                .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("title", String()::class.java, FieldAttribute.REQUIRED)
                .addField("description", String()::class.java, FieldAttribute.REQUIRED)
        }

        if (schema.get("Goal") == null) {
            schema.create("Goal")
                .addField("goalId", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("goal", String()::class.java, FieldAttribute.REQUIRED)
        }

        if (schema.get("HowItWorks") == null) {
            schema.create("HowItWorks")
                .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("title", String()::class.java, FieldAttribute.REQUIRED)
                .addField("description", String()::class.java, FieldAttribute.REQUIRED)
                .addField("image", String()::class.java, FieldAttribute.REQUIRED)
        }

        var onboardingPermissionSchema = schema.get("OnboardingPermission")
        if (onboardingPermissionSchema == null) {
            onboardingPermissionSchema = schema.create("OnboardingPermission")
                .addField("id", Int::class.java, FieldAttribute.REQUIRED)
                .addField("introNewUser", String()::class.java, FieldAttribute.REQUIRED)
                .addField("introMigratinUser", String()::class.java, FieldAttribute.REQUIRED)
                .addField("motionFitness", String::class.java, FieldAttribute.REQUIRED)
                .addField("location", String()::class.java, FieldAttribute.REQUIRED)
                .addField("notifications", String()::class.java, FieldAttribute.REQUIRED)
                .addField("termsLink", String()::class.java, FieldAttribute.REQUIRED)
        }

        if (schema.get("Onboarding") == null) {
            schema.create("Onboarding")
                .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addRealmObjectField("onboardingPermission", onboardingPermissionSchema!!)
                .addField("motionFitness", String()::class.java, FieldAttribute.REQUIRED)
                .addField("location", String()::class.java, FieldAttribute.REQUIRED)
                .addField("notifications", String::class.java, FieldAttribute.REQUIRED)
                .addField("goals", String()::class.java, FieldAttribute.REQUIRED)
        }

        if (schema.get("Tip") == null) {
            schema.create("Tip")
                .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
                .addField("tipTitle", String()::class.java, FieldAttribute.REQUIRED)
                .addField("tipDescription", String()::class.java, FieldAttribute.REQUIRED)
                .addField("imageRes", String::class.java, FieldAttribute.REQUIRED)
        }
    }
}