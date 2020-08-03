package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel
import kotlinx.android.synthetic.main.fragment_select_consonant_dialog.*

class SelectConsonantsDialogFragment : DialogFragment() {

    private lateinit var activity: ViewPagerActivity
    private lateinit var fragment: CardViewFragment
    private lateinit var idioms: ArrayList<IdiomModel>
    private var previousView = 0
    private var selectedConsonant = ""

    fun setActivity(activity: ViewPagerActivity) {
        previousView = VIEW_PAGER_ACTIVITY
        this.activity = activity
        this.idioms = activity.idioms
    }

    fun setFragment(fragment: CardViewFragment) {
        previousView = CARD_VIEW_FRAGMENT
        this.fragment = fragment
        this.idioms = fragment.idioms
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.fragment_select_consonant_dialog)

        val consonants = resources.getStringArray(R.array.string_array_consonants)

        dialog.spinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, consonants)
        dialog.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position) {
                    0 -> selectedConsonant = ""
                    1 -> selectedConsonant = "가"
                    2 -> selectedConsonant = "나"
                    3 -> selectedConsonant = "다"
                    4 -> selectedConsonant = "라"
                    5 -> selectedConsonant = "마"
                    6 -> selectedConsonant = "바"
                    7 -> selectedConsonant = "사"
                    8 -> selectedConsonant = "아"
                    9 -> selectedConsonant = "자"
                    10 -> selectedConsonant = "차"
                    11 -> selectedConsonant = "카"
                    12 -> selectedConsonant = "타"
                    13 -> selectedConsonant = "파"
                    14 -> selectedConsonant = "하"
                }
            }
        }

        dialog.frame_layout_ok.setOnClickListener {
            if (selectedConsonant.isBlank())
                dismiss()
            else {
                val idiomFound = findFirstIdiomStartWithConsonant(selectedConsonant)
                if (idiomFound == null) {
                    Toast.makeText(requireContext(), "해당 자음으로 시작하는 사자성어가 없습니다.", Toast.LENGTH_LONG).show()
                    dismiss()
                } else {
                    if (previousView == CARD_VIEW_FRAGMENT) {
                        fragment.scrollToIdiomPosition(idiomFound)
                        dismiss()
                    } else if (previousView == VIEW_PAGER_ACTIVITY) {
                        ""
                    }
                }
            }
        }

        return dialog
    }

    private fun findFirstIdiomStartWithConsonant(consonant: String): IdiomModel? {
        for (idiom in idioms) {
            if (consonant <= idiom.koreanCharacters[0].toString())
                return idiom
        }

        return null
    }

    companion object {
        private const val CARD_VIEW_FRAGMENT = 0
        private const val VIEW_PAGER_ACTIVITY = 1
    }
}