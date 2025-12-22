package com.flipsidegroup.active10.data.persistance

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration14to15 : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema

        schema.create("InfoPageContent")
            .addField("body", String()::class.java)
            .addField("order", String()::class.java)
            .addField("type", String()::class.java, FieldAttribute.REQUIRED)

        schema.create("InfoPageDestination")
            .addField("ios", String()::class.java)
            .addField("android", String()::class.java)

        schema.create("InfoPage")
            .addField("analyticsTag", String()::class.java, FieldAttribute.REQUIRED)
            .addField("buttonAccessibilityLabel", String()::class.java, FieldAttribute.REQUIRED)
            .addField("buttonAnalyticsTag", String()::class.java, FieldAttribute.REQUIRED)
            .addField("buttonTitle", String()::class.java, FieldAttribute.REQUIRED)
            .addField("buttonUrl", String()::class.java, FieldAttribute.REQUIRED)
            .addField("category", Int::class.java, FieldAttribute.REQUIRED)
            .addField("categoryId", Int::class.java, FieldAttribute.REQUIRED)
            .addField("categoryLabel", String()::class.java, FieldAttribute.REQUIRED)
            .addField("categoryPosition", Int::class.java, FieldAttribute.REQUIRED)
            .addRealmListField("contentList", schema.get("InfoPageContent")!!)
            .addField("description", String()::class.java, FieldAttribute.REQUIRED)
            .addRealmObjectField("destination", schema.get("InfoPageDestination")!!)
            .addField("id", Long::class.java, FieldAttribute.PRIMARY_KEY)
            .addField("imageUrl", String()::class.java, FieldAttribute.REQUIRED)
            .addField("platform", String()::class.java, FieldAttribute.REQUIRED)
            .addField("published", Boolean::class.java, FieldAttribute.REQUIRED)
            .addRealmListField("relatedArticles", Long::class.java)
            .setNullable("relatedArticles", true)
            .addField("relatedSectionDescription", String()::class.java)
            .addField("relatedSectionTitle", String()::class.java)
            .addField("slug", String()::class.java, FieldAttribute.REQUIRED)
            .addField("title", String()::class.java, FieldAttribute.REQUIRED)
            .addRealmListField("viewIds", Int::class.java)
            .setNullable("viewIds", true)
            .addField("categoryView", String()::class.java, FieldAttribute.REQUIRED)

        schema.create("DiscoverCategory")
            .addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
            .addField("name", String()::class.java, FieldAttribute.REQUIRED)
            .addField("position", Int::class.java, FieldAttribute.REQUIRED)

        schema.create("ScreenProperty")
            .addField("key", String()::class.java, FieldAttribute.REQUIRED)
            .addField("value", String()::class.java, FieldAttribute.REQUIRED)

        schema.create("ScreenMedia")
            .addField("id", Long::class.java, FieldAttribute.PRIMARY_KEY)
            .addField("type", String()::class.java, FieldAttribute.REQUIRED)
            .addField("tag", String()::class.java, FieldAttribute.REQUIRED)
            .addField("resourceId", String()::class.java, FieldAttribute.REQUIRED)
            .addField("label", String()::class.java, FieldAttribute.REQUIRED)
            .addField("url", String()::class.java)

        schema.create("ScreenContent")
            .addField("id", Long::class.java, FieldAttribute.PRIMARY_KEY)
            .addField("slug", String()::class.java, FieldAttribute.REQUIRED)
            .addField("title", String()::class.java, FieldAttribute.REQUIRED)
            .addField("type", String()::class.java, FieldAttribute.REQUIRED)
            .addField("description", String()::class.java, FieldAttribute.REQUIRED)
            .addField("analyticsTag", String()::class.java)
            .addRealmListField("infoPageIds", Long::class.java)
            .setNullable("infoPageIds", true)
            .addRealmListField("childrenIds", Long::class.java)
            .setNullable("childrenIds", true)
            .addRealmListField("alternativeChildrenIds", Long::class.java)
            .setNullable("alternativeChildrenIds", true)
            .addRealmListField("properties", schema.get("ScreenProperty")!!)
            .addRealmListField("media", schema.get("ScreenMedia")!!)

    }
}