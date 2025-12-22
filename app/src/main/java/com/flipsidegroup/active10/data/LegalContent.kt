package com.flipsidegroup.active10.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.phe.betterhealth.components.articles.ArticlePageContent
import io.realm.RealmObject
import kotlinx.android.parcel.Parcelize

@Parcelize
open class LegalContent(
    @SerializedName("type")
    override var type: String = "",
    @SerializedName("body")
    override var body: String = ""
) : RealmObject(), Parcelable, ArticlePageContent