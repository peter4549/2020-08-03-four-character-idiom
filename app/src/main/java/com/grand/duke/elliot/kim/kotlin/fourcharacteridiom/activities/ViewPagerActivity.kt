package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.*
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.adapters.RecyclerViewAdapter
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments.PageViewFragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments.SelectConsonantDialogFragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel
import kotlinx.android.synthetic.main.activity_view_pager.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class ViewPagerActivity : AppCompatActivity() {

    private lateinit var bookmarkKey: String
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private var currentPage = 0
    private var totalPagesCount = 0
    var idioms = arrayListOf<IdiomModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setBackgroundColor(ContextCompat.getColor(this,
            R.color.colorToolbar
        ))

        when(intent?.getIntExtra(MainActivity.KEY_SELECTED_PART, 0)) {
            SelectedPart.MY_IDIOMS -> {
                bookmarkKey =
                    BookmarksPreferences.KEY_MY_IDIOMS
                supportActionBar?.title = "내 사자성어"
                idioms = MainActivity.idioms
                    .filter { it.id in MainActivity.myIdiomIds } as ArrayList<IdiomModel>
            }
            SelectedPart.ALL_IDIOMS -> {
                bookmarkKey =
                    BookmarksPreferences.KEY_ALL_IDIOMS
                supportActionBar?.title = "모든 사자성어"
                idioms = MainActivity.idioms
            }
            SelectedPart.CIVIL_SERVICE_EXAMINATION_IDIOMS -> {
                bookmarkKey =
                    BookmarksPreferences.KEY_CIVIL_SERVICE_EXAMINATION_IDIOMS
                supportActionBar?.title = "공무원 사자성어"
                idioms = MainActivity.idioms
                    .filter { it.category == Category.CIVIL_SERVICE_EXAMINATION ||
                            it.category == Category.BOTH
                    } as ArrayList<IdiomModel>
            }
            SelectedPart.SAT_IDIOMS -> {
                bookmarkKey =
                    BookmarksPreferences.KEY_SAT_IDIOMS
                supportActionBar?.title = "수능 사자성어"
                idioms = MainActivity.idioms
                    .filter { it.category == Category.SAT ||
                            it.category == Category.BOTH
                    } as ArrayList<IdiomModel>
            }
            else -> {
                Toast.makeText(this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        totalPagesCount = idioms.count()
        if (totalPagesCount != 0) {
            text_view_no_idioms.visibility = View.GONE
            recyclerViewAdapter =
                RecyclerViewAdapter(
                    this,
                    idioms
                )
            view_pager.adapter = ViewPagerAdapter(this, idioms)
            view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    currentPage = position
                    setPageText(currentPage)
                    setStar(idioms[currentPage])
                }
            })

            currentPage = loadBookmark(bookmarkKey)

            moveToPage(currentPage)
            image_view_star.setOnClickListener {
                if (idioms[currentPage].id in MainActivity.myIdiomIds) {
                    Toast.makeText(this, "내 사자성어에서 제외되었습니다.", Toast.LENGTH_SHORT).show()
                    MainActivity.myIdiomIds.remove(idioms[currentPage].id)
                    image_view_star.setImageResource(R.drawable.ic_star_grey_36dp)
                } else {
                    Toast.makeText(this, "내 사자성어에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                    MainActivity.myIdiomIds.add(idioms[currentPage].id)
                    image_view_star.setImageResource(R.drawable.ic_star_blue_36dp)
                }
            }
        } else {
            image_view_star.visibility = View.GONE
            text_view_no_idioms.visibility = View.VISIBLE
        }

        MobileAds.initialize(this)
        ad_view.loadAd(AdRequest.Builder().build())
        val adListener = object : AdListener() {
            @Suppress("DEPRECATION")
            override fun onAdFailedToLoad(p0: Int) {
                println("$TAG: onAdFailedToLoad")
                super.onAdFailedToLoad(p0)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                println("$TAG: onAdLoaded")
            }
        }

        ad_view.adListener = adListener
    }

    private fun loadBookmark(key: String): Int {
        val preferences =
            getSharedPreferences(BookmarksPreferences.PREFERENCE_BOOKMARKS, Context.MODE_PRIVATE)
        return preferences.getInt(key, 0)
    }

    private fun moveToPage(currentPage: Int) {
        setPageText(currentPage)
        setStar(idioms[currentPage])
        view_pager.setCurrentItem(currentPage, false)
    }

    private fun setStar(idiom: IdiomModel) {
        val imageResources =
            if (idiom.id in MainActivity.myIdiomIds)
                R.drawable.ic_star_blue_36dp
            else
                R.drawable.ic_star_grey_36dp

        image_view_star.setImageResource(imageResources)
    }

    private fun setPageText(currentPage: Int) {
        val page = "${currentPage + 1}/${totalPagesCount}"
        text_view_page.text = page
    }

    override fun onPause() {
        saveBookmark(bookmarkKey, currentPage)
        saveMyIdiomIds()
        super.onPause()
    }

    private fun saveBookmark(key: String, page: Int) {
        val preferences =
            getSharedPreferences(BookmarksPreferences.PREFERENCE_BOOKMARKS, Context.MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putInt(key, page)
        editor.apply()
    }

    private fun saveMyIdiomIds() {
        val preferences = getSharedPreferences(ApplicationPreferences.PREFERENCES_APPLICATION, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putStringSet(
            ApplicationPreferences.KEY_MY_IDIOM_IDS,
            MainActivity.myIdiomIds.map { it.toString() }.toSet())
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_view_pager, menu)

        val searchView = menu?.findItem(R.id.item_search)?.actionView as SearchView
        searchView.setOnSearchClickListener {
            if (supportFragmentManager.findFragmentByTag(TAG_SEARCH_FRAGMENT) == null)
                openSearchFragment()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (supportFragmentManager.findFragmentByTag(TAG_SEARCH_FRAGMENT) == null)
                    openSearchFragment()
                else
                    recyclerViewAdapter.getFilter().filter(query)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (supportFragmentManager.findFragmentByTag(TAG_SEARCH_FRAGMENT) == null)
                    openSearchFragment()
                else
                    recyclerViewAdapter.getFilter().filter(newText)

                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
            R.id.item_search -> {  }
            R.id.item_view_from_consonants -> {
                val dialogFragment =
                    SelectConsonantDialogFragment()
                dialogFragment.setActivity(this)
                dialogFragment.show(supportFragmentManager,
                    TAG
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun scrollToIdiomPosition(idiom: IdiomModel) {
        if (supportFragmentManager.findFragmentByTag(TAG_SEARCH_FRAGMENT) != null)
            onBackPressed()
        moveToPage(idioms.indexOf(idiom))
        hideKeyboard()
    }

    private inner class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val idioms: ArrayList<IdiomModel>)
        : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return idioms.count()
        }

        override fun createFragment(position: Int): Fragment {
            val pageViewFragment =
                PageViewFragment()
            pageViewFragment.setIdiom(idioms[position])
            return pageViewFragment
        }
    }

    private fun openSearchFragment() {
        val searchFragment =
            SearchFragment()
        searchFragment.setAdapter(recyclerViewAdapter)
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .setCustomAnimations(
                R.anim.anim_slide_in_from_bottom,
                R.anim.anim_slide_out_to_top,
                R.anim.anim_slide_in_from_top,
                R.anim.anim_slide_out_to_bottom
            ).replace(
                R.id.frame_layout_view_pager, searchFragment,
                TAG_SEARCH_FRAGMENT
            ).commit()
    }

    private fun hideKeyboard() {
        val manager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(relative_layout_activity_view_pager.windowToken, 0)
    }

    class SearchFragment : Fragment() {

        private lateinit var adapter: RecyclerViewAdapter

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_search, container, false)

            view.recycler_view_search_results.apply {
                adapter = this@SearchFragment.adapter
                layoutManager =
                    GridLayoutManagerWrapper(
                        requireContext(),
                        1
                    )
            }

            return view
        }

        fun setAdapter(adapter: RecyclerViewAdapter) {
            this.adapter = adapter
        }
    }

    companion object {
        private const val TAG = "ViewPagerActivity"
        const val TAG_SEARCH_FRAGMENT = "tag.search.fragment"
    }
}
