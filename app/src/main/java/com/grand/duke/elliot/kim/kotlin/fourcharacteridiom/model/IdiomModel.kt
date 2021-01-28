package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IdiomModel(
    val id: Int,
    val category: Int,
    val koreanCharacters: String,
    val chineseCharacters: String,
    val description: String,
    val meanings: ArrayList<String>
): Parcelable {

    fun formatMeanings(): String {
        var formattedMeanings = ""
        for (index in meanings.indices) {
            formattedMeanings += "${chineseCharacters[index]}: ${meanings[index]}"

            if (index < meanings.count() - 1)
                formattedMeanings += "\n"
        }

        return formattedMeanings
    }
}