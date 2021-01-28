package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities.view_pager

import androidx.lifecycle.ViewModel
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.blank
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel

class ViewPagerViewModel: ViewModel() {
    var bookmarkKey: String = blank
    var currentPage = 0
    var totalPagesCount = 0
    var idioms = arrayListOf<IdiomModel>()
}