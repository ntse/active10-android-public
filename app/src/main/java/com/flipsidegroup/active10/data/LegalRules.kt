package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class LegalRules(
    @SerializedName("pageType") var pageType: String = "",
    @SerializedName("title") var title: String = "",
    @SerializedName("content") var content: RealmList<LegalContent>? = null,
    @SerializedName("version") var version: Int = 0,
    var id: Int = 0
) : RealmObject()