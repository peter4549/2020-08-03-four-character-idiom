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
import kotlinx.android.synthetic.main.fragment_select_part_dialog.view.*

class SelectPartDialogFragment: DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = requireActivity().layoutInflater.inflate(R.layout.fragment_select_part_dialog, null)
        builder.setView(view)

        view.button_my_idioms.setOnClickListener(onClickListener)
        view.button_all_idioms.setOnClickListener(onClickListener)
        view.button_civil_service_examination_idioms.setOnClickListener(onClickListener)
        view.button_sat_idioms.setOnClickListener(onClickListener)

        return builder.create()
    }

    private val onClickListener = View.OnClickListener { view ->
        when(view.id) {
            R.id.button_my_idioms -> MainActivity.selectedPart = SelectedPart.MY_IDIOMS
            R.id.button_all_idioms -> MainActivity.selectedPart = SelectedPart.ALL_IDIOMS
            R.id.button_civil_service_examination_idioms -> MainActivity.selectedPart = SelectedPart.CIVIL_SERVICE_EXAMINATION_IDIOMS
            R.id.button_sat_idioms -> MainActivity.selectedPart = SelectedPart.SAT_IDIOMS
        }

        SelectCountDialogFragment().show(requireActivity().supportFragmentManager, tag)
        dismiss()
    }
}