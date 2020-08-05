package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.R
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities.QuizActivity
import kotlinx.android.synthetic.main.fragment_complete_quiz_dialog.view.*

class CompleteQuizDialogFragment: DialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_complete_quiz_dialog, null)
        builder.setView(view)

        view.button_ok.setOnClickListener {
            (requireActivity() as QuizActivity).showInterstitialAd()
            dismiss()
        }

        return builder.create()
    }
}