package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel
import kotlinx.android.synthetic.main.fragment_page_view.view.*

class PageViewFragment : Fragment() {

    private lateinit var idiom: IdiomModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_page_view, container, false)
        view.text_view_korean_characters.text = idiom.koreanCharacters
        view.text_view_chinese_characters.text = idiom.chineseCharacters
        view.text_view_description.text = idiom.description
        view.text_view_meaning_of_each_character.text = idiom.formatMeanings()

        return view
    }

    fun setIdiom(idiom: IdiomModel) {
        this.idiom = idiom
    }

}
