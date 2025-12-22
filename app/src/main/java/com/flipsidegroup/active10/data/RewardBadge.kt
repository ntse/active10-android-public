package com.flipsidegroup.active10.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RewardBadge(
    @SerializedName("id") @PrimaryKey var id: Int = 0,
    @SerializedName("title") var title: String = "",
    @SerializedName("category") var category: String = "",
    @SerializedName("slug") var slug: String = "",
    @SerializedName("text") var text: String = "",
    @SerializedName("on_image") var onImage: String = "",
    @SerializedName("off_image") var offImage: String = "",
    @SerializedName("animation") var animation: String = "",
    @SerializedName("how_to") var howTo: String = "",
    @SerializedName("share") var share: String = "",
    @SerializedName("position") var position: Int = 0,
    @SerializedName("isEarned") var isEarned: Boolean = false,
    @SerializedName("repetitions") var repetitions: Int = 0,

) : Parcelable, RealmObject() {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(category)
        parcel.writeString(slug)
        parcel.writeString(text)
        parcel.writeString(onImage)
        parcel.writeString(offImage)
        parcel.writeString(animation)
        parcel.writeString(howTo)
        parcel.writeString(share)
        parcel.writeInt(position)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RewardBadge

        if (id != other.id) return false
        if (title != other.title) return false
        if (category != other.category) return false
        if (slug != other.slug) return false
        if (text != other.text) return false
        if (onImage != other.onImage) return false
        if (offImage != other.offImage) return false
        if (animation != other.animation) return false
        if (howTo != other.howTo) return false
        if (share != other.share) return false
        if (position != other.position) return false
        if (isEarned != other.isEarned) return false
        if (repetitions != other.repetitions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + slug.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + onImage.hashCode()
        result = 31 * result + offImage.hashCode()
        result = 31 * result + animation.hashCode()
        result = 31 * result + howTo.hashCode()
        result = 31 * result + share.hashCode()
        result = 31 * result + position
        result = 31 * result + isEarned.hashCode()
        result = 31 * result + repetitions.hashCode()
        return result
    }

    companion object CREATOR : Parcelable.Creator<RewardBadge> {
        override fun createFromParcel(parcel: Parcel): RewardBadge {
            return RewardBadge(parcel)
        }

        override fun newArray(size: Int): Array<RewardBadge?> {
            return arrayOfNulls(size)
        }
    }

}
