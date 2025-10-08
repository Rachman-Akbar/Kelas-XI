package com.komputerkit.earningapp.data.model

import android.os.Parcel
import android.os.Parcelable

data class Question(
    val id: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val difficulty: String = "easy", // easy, medium, hard
    val subject: String = "",
    val explanation: String = ""
) : Parcelable {
    constructor() : this("", "", emptyList(), "", "easy", "", "")
    
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.readString() ?: "easy",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(question)
        parcel.writeStringList(options)
        parcel.writeString(correctAnswer)
        parcel.writeString(difficulty)
        parcel.writeString(subject)
        parcel.writeString(explanation)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }
}
