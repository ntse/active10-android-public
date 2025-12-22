package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject


open class TermsConditions(
    @SerializedName("latest_version")
    var latestVersion: String = "",
    @SerializedName("title")
    var title: String = "",
    @SerializedName("text")
    var text: String = "",
    @SerializedName("button")
    var button: String = "",
    @SerializedName("agree")
    var agree: String = "",
    @SerializedName("links")
    var links: RealmList<TermsAndConditionsLinks> = RealmList()
) : RealmObject()