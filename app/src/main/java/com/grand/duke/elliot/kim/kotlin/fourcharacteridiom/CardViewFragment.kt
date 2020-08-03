package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel
import kotlinx.android.synthetic.main.fragment_card_view.view.*

class CardViewFragment : Fragment() {

    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView
    var idioms = arrayListOf<IdiomModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_card_view, container, false)

        (requireActivity() as MainActivity).setSupportActionBar(view.toolbar)
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        view.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorToolbar))
        setHasOptionsMenu(true)

        recyclerView = view.recycler_view

        when((requireActivity() as MainActivity).selectedPart) {
            SelectedPart.MY_IDIOMS -> {
                (requireActivity() as MainActivity).supportActionBar?.title = "내 사자성어"
                idioms = MainActivity.idioms
                    .filter { it.id in MainActivity.myIdiomIds } as ArrayList<IdiomModel>
            }
            SelectedPart.ALL_IDIOMS -> {
                (requireActivity() as MainActivity).supportActionBar?.title = "모든 사자성어"
                idioms = MainActivity.idioms
            }
            SelectedPart.CIVIL_SERVICE_EXAMINATION_IDIOMS -> {
                (requireActivity() as MainActivity).supportActionBar?.title = "공무원 사자성어"
                idioms = MainActivity.idioms
                    .filter { it.category == Category.CIVIL_SERVICE_EXAMINATION ||
                        it.category == Category.BOTH
                } as ArrayList<IdiomModel>
            }
            SelectedPart.SAT_IDIOMS -> {
                (requireActivity() as MainActivity).supportActionBar?.title = "수능 사자성어"
                idioms = MainActivity.idioms
                    .filter { it.category == Category.SAT ||
                            it.category == Category.BOTH
                    } as ArrayList<IdiomModel>
            }
        }

        adapter = RecyclerViewAdapter(requireActivity() as MainActivity, idioms)
        view.recycler_view.apply {
            adapter = this@CardViewFragment.adapter
            layoutManager = GridLayoutManagerWrapper(requireContext(), 1)
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        (requireActivity() as MainActivity).menuInflater.inflate(R.menu.menu_card_view, menu)

        val searchView = menu.findItem(R.id.item_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.getFilter().filter(newText)
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_start_with_consonants -> {
                val dialogFragment = SelectConsonantsDialogFragment()
                dialogFragment.setFragment(this)
                dialogFragment.show(requireActivity().supportFragmentManager, tag)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun scrollToIdiomPosition(idiom: IdiomModel) =
        recyclerView.scrollToPosition(adapter.getPosition(idiom))
}
