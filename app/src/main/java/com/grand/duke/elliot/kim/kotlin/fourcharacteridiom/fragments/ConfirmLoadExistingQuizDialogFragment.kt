package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities.MainActivity
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.R
import kotlinx.android.synthetic.main.fragment_confirm_load_existing_quiz_dialog.view.*

class ConfirmLoadExistingQuizDialogFragment: DialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_confirm_load_existing_quiz_dialog, null)
        builder.setView(view)

        view.button_solve_continuously.setOnClickListener {
            (requireActivity() as MainActivity).startQuizActivity(null, null, true)
            dismiss()
        }

        view.button_start_anew.setOnClickListener {
            SelectPartDialogFragment().show(requireActivity().supportFragmentManager, tag)
            dismiss()
        }

        return builder.create()
    }
}