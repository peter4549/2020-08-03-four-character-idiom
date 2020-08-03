package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel
import kotlinx.android.synthetic.main.activity_view_pager.*

class ViewPagerActivity : AppCompatActivity() {

    var idioms = arrayListOf<IdiomModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorToolbar))

        when(intent?.getIntExtra(MainActivity.KEY_SELECTED_PART, 0)) {
            SelectedPart.MY_IDIOMS -> {
                supportActionBar?.title = "내 사자성어"
                idioms = MainActivity.idioms
                    .filter { it.id in MainActivity.myIdiomIds } as ArrayList<IdiomModel>
            }
            SelectedPart.ALL_IDIOMS -> {
                supportActionBar?.title = "모든 사자성어"
                idioms = MainActivity.idioms
            }
            SelectedPart.CIVIL_SERVICE_EXAMINATION_IDIOMS -> {
                supportActionBar?.title = "공무원 사자성어"
                idioms = MainActivity.idioms
                    .filter { it.category == Category.CIVIL_SERVICE_EXAMINATION ||
                            it.category == Category.BOTH
                    } as ArrayList<IdiomModel>
            }
            SelectedPart.SAT_IDIOMS -> {
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

        view_pager.adapter = ViewPagerAdapter(this, idioms)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_view_pager, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
            R.id.item_search -> {
                
            }
            R.id.item_view_from_consonants -> {
                val dialogFragment = SelectConsonantsDialogFragment()
                dialogFragment.setActivity(this)
                dialogFragment.show(supportFragmentManager, tag)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val idioms: ArrayList<IdiomModel>) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return idioms.count()
        }

        override fun createFragment(position: Int): Fragment {
            val pageViewFragment = PageViewFragment()
            pageViewFragment.setIdiom(idioms[position])
            return pageViewFragment
        }

    }
}
