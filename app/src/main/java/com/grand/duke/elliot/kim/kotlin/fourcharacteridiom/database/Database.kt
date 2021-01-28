package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.QuizModel

@Database(entities = [QuizModel::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class Database: RoomDatabase() {
    abstract fun dao(): Dao
}