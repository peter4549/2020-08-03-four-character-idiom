package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "QUIZ_TABLE")
data class QuizModel (@PrimaryKey val idiomId: Int,
                      val question: String,
                      val examples: ArrayList<String>,
                      val correctAnswer: Int)