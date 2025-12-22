package com.flipsidegroup.active10.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class HowItWorks(
    @SerializedName("id") @PrimaryKey var id: Int = 0,
    @SerializedName("title") var title: String = "",
    @SerializedName("description") var description: String = "",
    @SerializedName("image") var image: String = ""
) : Parcelable, RealmObject() {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HowItWorks> {
        override fun createFromParcel(parcel: Parcel): HowItWorks {
            return HowItWorks(parcel)
        }

        override fun newArray(size: Int): Array<HowItWorks?> {
            return arrayOfNulls(size)
        }
    }
}