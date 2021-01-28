package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.QuizModel

@Dao
interface Dao {
    @Query("SELECT * FROM QUIZ_TABLE")
    fun getAll(): List<QuizModel>

    @Query("DELETE FROM QUIZ_TABLE")
    fun nukeTable()

    @Insert
    fun insert(quiz: QuizModel)
}