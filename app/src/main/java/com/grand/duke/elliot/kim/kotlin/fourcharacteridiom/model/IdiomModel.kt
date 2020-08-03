package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model

data class IdiomModel(
    val id: Int,
    val category: Int,
    val koreanCharacters: String,
    val chineseCharacters: String,
    val description: String,
    val meanings: ArrayList<String>) {

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