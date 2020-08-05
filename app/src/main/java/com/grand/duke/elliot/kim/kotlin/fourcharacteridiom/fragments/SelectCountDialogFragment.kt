package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities.MainActivity
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.R
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.SelectedPart
import kotlinx.android.synthetic.main.fragment_select_count_dialog.view.*

class SelectCountDialogFragment: DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_select_count_dialog, null)
        builder.setView(view)

        view.button_random_25_quizzes.setOnClickListener(onClickListener)
        view.button_random_50_quizzes.setOnClickListener(onClickListener)
        view.button_random_100_quizzes.setOnClickListener(onClickListener)
        view.button_random_200_quizzes.setOnClickListener(onClickListener)

        return builder.create()
    }

    private val onClickListener = View.OnClickListener { view ->
        when(view.id) {
            R.id.button_random_25_quizzes -> checkEnoughIdiomsAndStartQuiz(25)
            R.id.button_random_50_quizzes -> checkEnoughIdiomsAndStartQuiz(50)
            R.id.button_random_100_quizzes -> checkEnoughIdiomsAndStartQuiz(100)
            R.id.button_random_200_quizzes -> checkEnoughIdiomsAndStartQuiz(200)
        }
    }

    private fun checkEnoughIdiomsAndStartQuiz(count: Int) {
        var confirmed = true
        if (MainActivity.selectedPart == SelectedPart.MY_IDIOMS) {
            if (count > MainActivity.myIdiomIds.count()) {
                (requireActivity() as MainActivity).showToast("사자성어가 충분하지 않습니다.")
                confirmed = false
            }
        }

        if (confirmed)
            (requireActivity() as MainActivity).startQuizActivity(
                MainActivity.selectedPart, count, false)

        dismiss()
    }
}