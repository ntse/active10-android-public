package com.flipsidegroup.active10.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject

open class OnboardingPermission(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("intro_new_user") var introNewUser: String = "",
    @SerializedName("intro_migratins_user") var introMigratinUser: String = "",
    @SerializedName("motion_fitness") var motionFitness: String = "",
    @SerializedName("location") var location: String = "",
    @SerializedName("notifications") var notifications: String = "",
    @SerializedName("terms_link") var termsLink: String = ""
) : RealmObject(), Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(introNewUser)
        parcel.writeString(introMigratinUser)
        parcel.writeString(motionFitness)
        parcel.writeString(location)
        parcel.writeString(notifications)
        parcel.writeString(termsLink)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OnboardingPermission> {
        override fun createFromParcel(parcel: Parcel): OnboardingPermission {
            return OnboardingPermission(parcel)
        }

        override fun newArray(size: Int): Array<OnboardingPermission?> {
            return arrayOfNulls(size)
        }
    }
}