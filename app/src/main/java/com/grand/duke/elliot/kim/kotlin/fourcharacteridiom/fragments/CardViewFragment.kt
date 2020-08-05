package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.*
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities.MainActivity
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.adapters.RecyclerViewAdapter
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel
import kotlinx.android.synthetic.main.fragment_card_view.view.*

class CardViewFragment : Fragment() {

    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var bookmarkKey: String
    private lateinit var recyclerView: RecyclerView
    var idioms = arrayListOf<IdiomModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_card_view, container, false)

        (requireActivity() as MainActivity).setSupportActionBar(view.toolbar)
        (requireActivity() as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        view.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),
            R.color.colorToolbar
        ))
        setHasOptionsMenu(true)

        recyclerView = view.recycler_view

        when(MainActivity.selectedPart) {
            SelectedPart.MY_IDIOMS -> {
                bookmarkKey =
                    BookmarksPreferences.KEY_MY_IDIOMS
                (requireActivity() as MainActivity).supportActionBar?.title = "내 사자성어"
                idioms = MainActivity.idioms
                    .filter { it.id in MainActivity.myIdiomIds } as ArrayList<IdiomModel>
            }
            SelectedPart.ALL_IDIOMS -> {
                bookmarkKey =
                    BookmarksPreferences.KEY_ALL_IDIOMS
                (requireActivity() as MainActivity).supportActionBar?.title = "모든 사자성어"
                idioms =
                    MainActivity.idioms
            }
            SelectedPart.CIVIL_SERVICE_EXAMINATION_IDIOMS -> {
                bookmarkKey =
                    BookmarksPreferences.KEY_CIVIL_SERVICE_EXAMINATION_IDIOMS
                (requireActivity() as MainActivity).supportActionBar?.title = "공무원 사자성어"
                idioms = MainActivity.idioms
                    .filter { it.category == Category.CIVIL_SERVICE_EXAMINATION ||
                        it.category == Category.BOTH
                } as ArrayList<IdiomModel>
            }
            SelectedPart.SAT_IDIOMS -> {
                bookmarkKey =
                    BookmarksPreferences.KEY_SAT_IDIOMS
                (requireActivity() as MainActivity).supportActionBar?.title = "수능 사자성어"
                idioms = MainActivity.idioms
                    .filter { it.category == Category.SAT ||
                            it.category == Category.BOTH
                    } as ArrayList<IdiomModel>
            }
        }

        if (idioms.isNotEmpty()) {
            view.text_view_no_idioms.visibility = View.GONE
            adapter =
                RecyclerViewAdapter(
                    requireActivity() as MainActivity,
                    idioms
                )
            view.recycler_view.apply {
                adapter = this@CardViewFragment.adapter
                layoutManager =
                    GridLayoutManagerWrapper(
                        requireContext(),
                        1
                    )
            }

            scrollToIdiomPosition(loadBookmark(bookmarkKey))
        } else
            view.text_view_no_idioms.visibility = View.VISIBLE

        return view
    }

    private fun loadBookmark(key: String): Int {
        val preferences =
            requireContext().getSharedPreferences(BookmarksPreferences.PREFERENCE_BOOKMARKS, Context.MODE_PRIVATE)
        return preferences.getInt(key, 0)
    }

    override fun onPause() {
        (requireActivity() as MainActivity).saveMyIdiomIds()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        (requireActivity() as MainActivity).menuInflater.inflate(
            R.menu.menu_card_view, menu)

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
            android.R.id.home -> requireActivity().onBackPressed()
            R.id.item_view_from_consonants -> {
                val dialogFragment =
                    SelectConsonantDialogFragment()
                dialogFragment.setFragment(this)
                dialogFragment.show(requireActivity().supportFragmentManager, tag)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun scrollToIdiomPosition(idiom: IdiomModel) =
        recyclerView.scrollToPosition(adapter.getPosition(idiom))

    private fun scrollToIdiomPosition(position: Int) =
        recyclerView.scrollToPosition(position)
}
