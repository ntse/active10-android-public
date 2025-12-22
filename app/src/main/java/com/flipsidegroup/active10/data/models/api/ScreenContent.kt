package com.flipsidegroup.active10.data.models.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
open class ScreenContent(

    @SerializedName("id")
    @PrimaryKey
    var id: Long = 0L,

    @SerializedName("slug")
    var slug: String = "",

    @SerializedName("title")
    var title: String = "",

    @SerializedName("type")
    var type: String = "",

    @SerializedName("description")
    var description: String = "",

    @SerializedName("analyticsTag")
    var analyticsTag: String? = null,

    @SerializedName("article_ids")
    var infoPageIds: @RawValue RealmList<Long>? = RealmList(),

    @SerializedName("children_ids")
    var childrenIds: @RawValue RealmList<Long>? = RealmList(),

    @SerializedName("alternative_children_ids")
    var alternativeChildrenIds: @RawValue RealmList<Long>? = RealmList(),

    @SerializedName("properties")
    var properties: @RawValue RealmList<ScreenProperty> = RealmList(),

    @SerializedName("media")
    var media: @RawValue RealmList<ScreenMedia> = RealmList()
) : Parcelable, RealmObject() {

    fun getPropertyValue(key: String): String? = properties.firstOrNull { it.key == key }?.value

    fun getMediaUrlByLabel(label: String): String? = media.firstOrNull { it.label == label }?.url

    val actionTitle get() = getPropertyValue("action")

    val actionSlug get() = getPropertyValue("action_slug")

    val tooltipText get() = getPropertyValue("tooltip_text")

    val tooltipTitle get() = getPropertyValue("tooltip_title")

    val firstButtonTitle get() = getPropertyValue("first_button_title")

    val secondButtonTitle get() = getPropertyValue("second_button_title")

    val firstImageUrl get() = getMediaUrlByLabel("first_image")
}

@Parcelize
open class ScreenProperty(

    @SerializedName("key")
    var key: String = "",

    @SerializedName("value")
    var value: String = "",
) : Parcelable, RealmObject()

@Parcelize
open class ScreenMedia(

    @SerializedName("id")
    @PrimaryKey
    var id: Long = 0L,

    @SerializedName("type")
    var type: String = "",

    @SerializedName("tag")
    var tag: String = "",

    @SerializedName("resourceId")
    var resourceId: String = "",

    @SerializedName("label")
    var label: String = "",

    @SerializedName("url")
    var url: String? = null,
) : Parcelable, RealmObject()
