package com.flipsidegroup.active10.data.models.api

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.phe.betterhealth.components.articles.ArticlePage
import com.phe.betterhealth.components.articles.ArticlePageContent
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
open class InfoPage(

    @SerializedName("analyticsTag")
    override var analyticsTag: String = "",

    @SerializedName("buttonAccessibilityLabel")
    override var buttonAccessibilityLabel: String = "",

    @SerializedName("buttonAnalyticsTag")
    override var buttonAnalyticsTag: String = "",

    @SerializedName("buttonTitle")
    override var buttonTitle: String = "",

    @SerializedName("buttonUrl")
    override var buttonUrl: String = "",

    @SerializedName("category")
    override var category: Int = 0,

    @SerializedName("categoryId")
    override var categoryId: Int = 0,

    @SerializedName("categoryLabel")
    override var categoryLabel: String = "",

    @SerializedName("categoryPosition")
    override var categoryPosition: Int = 0,

    @SerializedName("content")
    var contentList: @RawValue RealmList<InfoPageContent> = RealmList(),

    @SerializedName("description")
    override var description: String = "",

    @SerializedName("destination")
    var destination: InfoPageDestination? = null,

    @SerializedName("id")
    @PrimaryKey
    override var id: Long = 0L,

    @SerializedName("imageUrl")
    override var imageUrl: String = "",

    @SerializedName("platform")
    override var platform: String = "",

    @SerializedName("published")
    override var published: Boolean = false,

    @SerializedName("relatedArticles")
    var relatedArticles: @RawValue RealmList<Long>? = RealmList(),

    @SerializedName("relatedSectionDescription")
    override var relatedSectionDescription: String? = null,

    @SerializedName("relatedSectionTitle")
    override var relatedSectionTitle: String? = null,

    @SerializedName("slug")
    override var slug: String = "",

    @SerializedName("title")
    override var title: String = "",

    @SerializedName("view_ids")
    var viewIds: @RawValue RealmList<Int>? = null,

    @SerializedName("type")
    override var categoryView: String = ""

) : ArticlePage, Parcelable, RealmObject() {

    override val content: List<ArticlePageContent>
        get() = contentList

    override val relatedInfoPages: List<Long>
        get() = relatedArticles ?: emptyList()
}

@Parcelize
open class InfoPageContent(

    @SerializedName("body")
    override var body: String? = null,

    @SerializedName("order")
    var order: String? = null,

    @SerializedName("type")
    override var type: String = ""
) : ArticlePageContent, Parcelable, RealmObject()

@Parcelize
open class InfoPageDestination(

    @SerializedName("ios")
    var ios: String? = null,

    @SerializedName("android")
    var android: String? = null
) : Parcelable, RealmObject()

