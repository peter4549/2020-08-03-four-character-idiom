package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.android.synthetic.main.activity_view_pager.*

class ViewPagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)

        //intent.getIntExtra()

        view_pager
    }

    private inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 0
        }

        override fun createFragment(position: Int): Fragment {
            val pageViewFragment = PageViewFragment()
            //pageViewFragment.setIdiom()
            //TODO("Not yet implemented")
            return pageViewFragment
        }

    }
}
