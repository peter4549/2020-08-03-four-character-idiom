package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities.view_pager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewPagerViewModelFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(ViewPagerViewModel::class.java)) {
            return ViewPagerViewModel() as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}