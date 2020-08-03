package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel

class PageViewFragment : Fragment() {

    private lateinit var idiom: IdiomModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_page_view, container, false)
    }

    fun setIdiom(idiom: IdiomModel) {
        this.idiom = idiom
    }

}
