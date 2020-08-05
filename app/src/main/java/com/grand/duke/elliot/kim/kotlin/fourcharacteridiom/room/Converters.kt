package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun arrayListToJson(value: ArrayList<String>) = Gson().toJson(value)

    @TypeConverter
    fun jsonToArrayList(value: String): ArrayList<String> {
        val arrayListType = object : TypeToken<ArrayList<String>>() {  }.type
        return Gson().fromJson(value, arrayListType)
    }
}