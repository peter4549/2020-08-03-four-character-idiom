package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {

    var idioms = ArrayList<IdiomModel>()
    var myIdiomIds = mutableSetOf<Int>()
    var selectedPart = SelectedPart.NOT_SELECTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        idioms = getIdiomsFromAssets()

        button_my_idioms.setOnClickListener {
            selectedPart = SelectedPart.MY_IDIOMS
            openIdiomsView()
        }

        button_all_idioms.setOnClickListener {
            selectedPart = SelectedPart.ALL_IDIOMS
            openIdiomsView()
        }

        button_civil_service_examination_idioms.setOnClickListener {
            selectedPart = SelectedPart.CIVIL_SERVICE_EXAMINATION_IDIOMS
            openIdiomsView()
        }

        button_sat_idioms.setOnClickListener {
            selectedPart = SelectedPart.SAT_IDIOMS
            openIdiomsView()
        }

        button_quiz.setOnClickListener {
            selectedPart = SelectedPart.QUIZ
        }
    }

    private fun openIdiomsView() {
        if (viewOption == CARD_VIEW)
            startCardViewFragment()
        else if (viewOption == PAGE_VIEW)
            startPageViewActivity()
    }

    private fun startCardViewFragment() {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .setCustomAnimations(
                R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left,
                R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right
            ).replace(R.id.relative_layout_activity_main, CardViewFragment(), TAG_CARD_VIEW_FRAGMENT).commit()
    }

    private fun startPageViewActivity() {
        val intent = Intent(this, ViewPagerActivity::class.java)
        intent.putExtra(KEY_SELECTED_PART, selectedPart)
        startActivity(intent)
    }

    private fun loadMyIdioms() {
        val preferences = getSharedPreferences(PREFERENCES_MY_IDIOMS, Context.MODE_PRIVATE)
        myIdiomIds = preferences.getStringSet(KEY_MY_IDIOM_IDS, setOf())?.map { it.toInt() }?.toMutableSet() ?: mutableSetOf()
    }

    fun saveMyIdiomIds() {
        val preferences = getSharedPreferences(PREFERENCES_MY_IDIOMS, Context.MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putStringSet(KEY_MY_IDIOM_IDS, myIdiomIds.map { it.toString() }.toSet())
        editor.apply()
    }

    fun showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, text, duration).show()
    }

    private fun readIdiomsFromAsset(): String {
        var text = ""
        try {
            val inputStream = assets.open("idioms.json")
            val size = inputStream.available()

            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            text = String(buffer, Charset.defaultCharset())
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return text
    }

    private fun getIdiomsFromAssets(): ArrayList<IdiomModel> {
        var idioms: Set<IdiomModel> = HashSet()
        val json: String = readIdiomsFromAsset()
        val type =
            object : TypeToken<HashSet<IdiomModel>?>() {  }.type
        try {
            idioms = Gson().fromJson(json, type)
        } catch (e: Exception) {
            showToast("데이터를 불러오는데 실패했습니다.")
            e.printStackTrace()
        }

        return ArrayList(idioms)
    }

    companion object {
        const val TAG_CARD_VIEW_FRAGMENT = "tag.card.view.fragment"

        const val KEY_CATEGORY = "main.activity.key.category"

        const val CARD_VIEW = 0
        const val PAGE_VIEW = 1

        var viewOption = 0

        const val PREFERENCES_MY_IDIOMS = "preferences_my_idioms"
        const val KEY_MY_IDIOM_IDS = "key_my_idiom_ids"

        const val KEY_SELECTED_PART = "key_selected_part"
    }
}
