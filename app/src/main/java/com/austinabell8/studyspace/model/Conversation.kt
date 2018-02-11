package com.austinabell8.studyspace.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by austi on 2018-02-08.
 */
data class Conversation(val conversationId: String? = "",
                        val from: String? = "",
                        val preview:String? = "",
                        val photoUrl:String? = "") : Parcelable {

    constructor(parcel: Parcel?) : this(
            parcel?.readString(),
            parcel?.readString(),
            parcel?.readString(),
            parcel?.readString()
            )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(conversationId)
        parcel.writeString(from)
        parcel.writeString(preview)
        parcel.writeString(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Conversation> {
        override fun createFromParcel(parcel: Parcel): Conversation {
            return Conversation(parcel)
        }

        override fun newArray(size: Int): Array<Conversation?> {
            return arrayOfNulls(size)
        }
    }
}

