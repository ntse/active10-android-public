package com.flipsidegroup.active10.data

import com.google.gson.annotations.SerializedName
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class Notifications(
    @SerializedName("id") @PrimaryKey var id: Int = 0,
    @SerializedName("onboarding")
    var onboardingNotifications: RealmList<OnboardingNotifications> = RealmList(),
    @SerializedName("lapsed")
    var lapsedNotifications: RealmList<LapsedNotifications> = RealmList(),
    @SerializedName("local")
    var localNotifications: RealmList<LocalNotification> = RealmList(),
    @SerializedName("reminder")
    var reminder: String = ""
) : RealmObject()