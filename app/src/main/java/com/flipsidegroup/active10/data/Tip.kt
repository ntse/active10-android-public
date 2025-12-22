package com.flipsidegroup.active10.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


open class Tip(
    @SerializedName("id") @PrimaryKey var id: Int = 0,
    @SerializedName("title") var tipTitle: String = "",
    @SerializedName("description") var tipDescription: String = "",
    @SerializedName("image") var imageRes: String = ""
) : Parcelable, RealmObject() {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(tipTitle)
        parcel.writeString(tipDescription)
        parcel.writeString(imageRes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tip> {
        override fun createFromParcel(parcel: Parcel): Tip {
            return Tip(parcel)
        }

        override fun newArray(size: Int): Array<Tip?> {
            return arrayOfNulls(size)
        }
    }
}