package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel
import kotlinx.android.synthetic.main.fragment_card_view.view.*

class CardViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_card_view, container, false)

        var idioms = arrayListOf<IdiomModel>()

        when((requireActivity() as MainActivity).selectedPart) {
            SelectedPart.MY_IDIOMS -> {
                idioms = (requireActivity() as MainActivity).idioms
                    .filter { it.id in (requireActivity() as MainActivity).myIdiomIds } as ArrayList<IdiomModel>
            }
            SelectedPart.ALL_IDIOMS -> {
                idioms = (requireActivity() as MainActivity).idioms
            }
            SelectedPart.CIVIL_SERVICE_EXAMINATION_IDIOMS -> {
                idioms = (requireActivity() as MainActivity).idioms
                    .filter { it.category == Category.CIVIL_SERVICE_EXAMINATION ||
                        it.category == Category.BOTH
                } as ArrayList<IdiomModel>
            }
            SelectedPart.SAT_IDIOMS -> {
                idioms = (requireActivity() as MainActivity).idioms
                    .filter { it.category == Category.SAT ||
                            it.category == Category.BOTH
                    } as ArrayList<IdiomModel>
            }
        }

        view.recycler_view.apply {
            adapter = RecyclerViewAdapter(requireActivity() as MainActivity, idioms)
            layoutManager = GridLayoutManagerWrapper(requireContext(), 1)
        }

        return view
    }
}
