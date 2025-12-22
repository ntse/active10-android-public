package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class GlobalRules(
    @SerializedName("id") @PrimaryKey var id: Int = 0,
    @SerializedName("terms_conditions")
    var termsAndConditions: TermsConditions? = null,
    @SerializedName("missing_data")
    var missingData: GlobalRulesMissingData? = null,
    @SerializedName("app")
    var app: GlobalRulesApp? = null,
    @SerializedName("accessibility")
    var accessibilityStatements: RealmList<AccessibilityStatement> = RealmList()
) : RealmObject()