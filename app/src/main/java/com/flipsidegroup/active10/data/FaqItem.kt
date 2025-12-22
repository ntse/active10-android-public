package com.flipsidegroup.active10.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class FaqItem(
    @SerializedName("id") @PrimaryKey var id: Int = 0,
    @SerializedName("title") var title: String = "",
    @SerializedName("text") var description: String = ""
) : Parcelable, RealmObject() {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FaqItem> {
        override fun createFromParcel(parcel: Parcel): FaqItem {
            return FaqItem(parcel)
        }

        override fun newArray(size: Int): Array<FaqItem?> {
            return arrayOfNulls(size)
        }
    }
}