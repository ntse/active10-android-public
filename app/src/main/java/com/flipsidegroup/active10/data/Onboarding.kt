package com.flipsidegroup.active10.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Onboarding(
    @SerializedName("id") @PrimaryKey var id: Int = 0,
    @SerializedName("ready_to_get_started") var onboardingPermission: OnboardingPermission? = null,
    @SerializedName("motion_fitness") var motionFitness: String = "",
    @SerializedName("location") var location: String = "",
    @SerializedName("notifications") var notifications: String = "",
    @SerializedName("goals") var goals: String = "",
    @SerializedName("about_you") var aboutYou: String = ""
) : RealmObject(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(OnboardingPermission::class.java.classLoader),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeParcelable(onboardingPermission, flags)
        parcel.writeString(motionFitness)
        parcel.writeString(location)
        parcel.writeString(notifications)
        parcel.writeString(goals)
        parcel.writeString(aboutYou)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Onboarding> {
        override fun createFromParcel(parcel: Parcel): Onboarding {
            return Onboarding(parcel)
        }

        override fun newArray(size: Int): Array<Onboarding?> {
            return arrayOfNulls(size)
        }
    }
}