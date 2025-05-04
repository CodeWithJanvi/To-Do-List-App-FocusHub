package com.example.myapplication1

import android.os.Parcel
import android.os.Parcelable

data class Task(
    val id: Int,
    var taskTitle: String,
    var taskDate: String,
    var taskTime: String,
    var isCompleted: Boolean,
    var isStarred: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(taskTitle)
        parcel.writeString(taskDate)
        parcel.writeString(taskTime)
        parcel.writeByte(if (isCompleted) 1 else 0)
        parcel.writeByte(if (isStarred) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
