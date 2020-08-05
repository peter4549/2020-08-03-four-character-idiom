package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities.QuizActivity
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.R
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.QuizModel
import kotlinx.android.synthetic.main.fragment_quiz.*
import kotlinx.android.synthetic.main.fragment_quiz.view.*

class QuizFragment: Fragment() {
    private lateinit var quiz: QuizModel

    fun setQuiz(quiz: QuizModel) {
        this.quiz = quiz
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz, container, false)
        val text = "Q. ${quiz.question}"
        view.text_view_question.text = text

        val example1 = "① ${quiz.examples[0]}"
        val example2 = "② ${quiz.examples[1]}"
        val example3 = "③ ${quiz.examples[2]}"
        val example4 = "④ ${quiz.examples[3]}"

        view.button_example_1.text = example1
        view.button_example_2.text = example2
        view.button_example_3.text = example3
        view.button_example_4.text = example4

        initUIState(view)

        view.button_example_1.setOnClickListener(onClickListener)
        view.button_example_2.setOnClickListener(onClickListener)
        view.button_example_3.setOnClickListener(onClickListener)
        view.button_example_4.setOnClickListener(onClickListener)

        return view
    }

    private val onClickListener = View.OnClickListener { view ->
        var selectedAnswer = -1

        setButtonsEnabled(false)
        when(view.id) {
            R.id.button_example_1 -> selectedAnswer = 0
            R.id.button_example_2 -> selectedAnswer = 1
            R.id.button_example_3 -> selectedAnswer = 2
            R.id.button_example_4 -> selectedAnswer = 3
        }

        confirmCorrectAnswer(selectedAnswer)
    }

    private fun initUIState(view: View) {
        if (quiz.idiomId in (requireActivity() as QuizActivity).solvedQuizIds) {
            when (quiz.correctAnswer) {
                0 -> view.button_example_1.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
                1 -> view.button_example_2.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
                2 -> view.button_example_3.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
                3 -> view.button_example_4.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
            }

            view.button_example_1.isEnabled = false
            view.button_example_2.isEnabled = false
            view.button_example_3.isEnabled = false
            view.button_example_4.isEnabled = false
        }
    }

    private fun confirmCorrectAnswer(answer: Int) {
        if (answer == quiz.correctAnswer)
            correctAnswerEvent(answer)
        else
            incorrectAnswerEvent()
    }

    private fun correctAnswerEvent(answer: Int) {
        when(answer) {
            0 -> button_example_1.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
            1 -> button_example_2.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
            2 -> button_example_3.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
            3 -> button_example_4.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
        }

        Toast.makeText(requireContext(), "정답입니다.", Toast.LENGTH_SHORT).show()
        (requireActivity() as QuizActivity).solvedQuizIds.add(quiz.idiomId)
        (requireActivity() as QuizActivity).updateSolvedQuizzesText()
        (requireActivity() as QuizActivity).checkAllQuizzesSolved()
    }

    private fun incorrectAnswerEvent() {
        Toast.makeText(requireContext(), "다시 생각해보세요.", Toast.LENGTH_SHORT).show()
        Thread.sleep(400L)

        setButtonsEnabled(true)
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        button_example_1.isEnabled = enabled
        button_example_2.isEnabled = enabled
        button_example_3.isEnabled = enabled
        button_example_4.isEnabled = enabled
    }
}