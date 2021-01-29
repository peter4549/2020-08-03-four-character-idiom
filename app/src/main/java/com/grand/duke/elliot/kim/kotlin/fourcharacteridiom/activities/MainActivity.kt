package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.*
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities.view_pager.ViewPagerActivity
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments.CardViewFragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments.ConfirmLoadExistingQuizDialogFragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments.ExitApplicationDialogFragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments.SelectPartDialogFragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.lock_screen.LockScreenService
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "매일 사자성어"
        toolbar.setBackgroundColor(ContextCompat.getColor(this,
            R.color.colorToolbar
        ))

        Timber.plant(Timber.DebugTree())

        loadMyIdioms()
        loadViewOption()

        // TODO check here.
        checkPermission()

        idioms = getIdiomsFromAssets()
        Collections.sort(idioms, Comparator { o1: IdiomModel, o2: IdiomModel ->
            return@Comparator o1.koreanCharacters.compareTo(o2.koreanCharacters)
        })

        button_my_idioms.setOnClickListener {
            selectedPart =
                SelectedPart.MY_IDIOMS
            openIdiomsView()
        }

        button_all_idioms.setOnClickListener {
            selectedPart =
                SelectedPart.ALL_IDIOMS
            openIdiomsView()
        }

        button_civil_service_examination_idioms.setOnClickListener {
            selectedPart =
                SelectedPart.CIVIL_SERVICE_EXAMINATION_IDIOMS
            openIdiomsView()
        }

        button_sat_idioms.setOnClickListener {
            selectedPart =
                SelectedPart.SAT_IDIOMS
            openIdiomsView()
        }

        button_quiz.setOnClickListener {
            val preferences = getSharedPreferences(Quiz.PREFERENCES_QUIZ, Context.MODE_PRIVATE)

            if (preferences.getBoolean(Quiz.KEY_QUIZ_IN_PROGRESS_EXIST, false)) {
                confirmToLoadExistingQuiz()
            } else
                SelectPartDialogFragment().show(supportFragmentManager,
                    TAG
                )
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

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag(TAG_CARD_VIEW_FRAGMENT) == null)
            ExitApplicationDialogFragment().show(supportFragmentManager, TAG)
        else
            super.onBackPressed()
    }

    override fun onPause() {
        saveMyIdiomIds()
        saveViewOption()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_view_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewOption = when(item.itemId) {
            R.id.item_card_view -> CARD_VIEW
            R.id.item_page_view -> PAGE_VIEW
            else -> viewOption
        }

        text_view_option.text =
            if(viewOption == CARD_VIEW) getString(R.string.view_in_card_view)
            else getString(R.string.view_in_page_view)

        return super.onOptionsItemSelected(item)
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
                R.anim.anim_slide_in_from_bottom,
                R.anim.anim_slide_out_to_top,
                R.anim.anim_slide_in_from_top,
                R.anim.anim_slide_out_to_bottom
            ).replace(
                R.id.frame_layout_scroll_view,
                CardViewFragment(),
                TAG_CARD_VIEW_FRAGMENT
            ).commit()
    }

    private fun startPageViewActivity() {
        val intent = Intent(this, ViewPagerActivity::class.java)
        intent.putExtra(
            KEY_SELECTED_PART,
            selectedPart
        )
        startActivity(intent)
    }

    private fun confirmToLoadExistingQuiz() {
        ConfirmLoadExistingQuizDialogFragment().show(supportFragmentManager,
            TAG
        )
    }

    fun startQuizActivity(part: Int?, count: Int?, solveContinuously: Boolean) {
        val intent = Intent(this, QuizActivity::class.java)

        if (solveContinuously) {
            intent.action =
                ACTION_QUIZZES_IN_PROGRESS_EXIST
            startActivity(intent)
        } else {
            intent.action =
                ACTION_NO_QUIZZES_IN_PROGRESS
            intent.putExtra(Quiz.KEY_QUIZ_PART, part)
            intent.putExtra(Quiz.KEY_QUIZ_COUNT, count)
            startActivity(intent)
        }
    }

    private fun loadMyIdioms() {
        val preferences = getSharedPreferences(ApplicationPreferences.PREFERENCES_APPLICATION, Context.MODE_PRIVATE)
        myIdiomIds = preferences.getStringSet(
            ApplicationPreferences.KEY_MY_IDIOM_IDS, setOf())?.map { it.toInt() }?.toMutableSet() ?: mutableSetOf()
    }

    fun saveMyIdiomIds() {
        val preferences = getSharedPreferences(ApplicationPreferences.PREFERENCES_APPLICATION, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putStringSet(ApplicationPreferences.KEY_MY_IDIOM_IDS, myIdiomIds.map { it.toString() }.toSet())
        editor.apply()
    }

    private fun loadViewOption() {
        val preferences = getSharedPreferences(ApplicationPreferences.PREFERENCES_APPLICATION, Context.MODE_PRIVATE)
        viewOption = preferences.getInt(
            ApplicationPreferences.KEY_VIEW_OPTION, 0)
        text_view_option.text =
            if(viewOption == CARD_VIEW) getString(R.string.view_in_card_view)
            else getString(R.string.view_in_page_view)
    }

    private fun saveViewOption() {
        val preferences = getSharedPreferences(ApplicationPreferences.PREFERENCES_APPLICATION, Context.MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putInt(
            ApplicationPreferences.KEY_VIEW_OPTION,
            viewOption
        )
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

    fun checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(this)) {
                val uri = Uri.fromParts("package", packageName, null)
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
                startActivityForResult(intent, 0)
            } else {
                val intent = Intent(applicationContext, LockScreenService::class.java)
                // startService(intent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LockScreenService().enqueueWork(this, intent)
                    startForegroundService(intent)
                } else {
                    LockScreenService().enqueueWork(this, intent)
                    startService(intent)
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0) {
            if(!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "해라", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(applicationContext, LockScreenService::class.java)
                startService(intent)
                LockScreenService().enqueueWork(this, intent)
            }
        }
    }

    fun showToast(context: Context, text: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, text, duration).show()
    }

    companion object {
        private const val TAG = "MainActivity"

        var idioms = ArrayList<IdiomModel>()
        var myIdiomIds = mutableSetOf<Int>()
        var selectedPart =
            SelectedPart.NOT_SELECTED

        const val TAG_CARD_VIEW_FRAGMENT = "tag.card.view.fragment"

        const val CARD_VIEW = 0
        const val PAGE_VIEW = 1

        var viewOption = 0

        const val KEY_SELECTED_PART = "key_selected_part"

        const val ACTION_QUIZZES_IN_PROGRESS_EXIST = "main.activity.action.quizzes.in.progress.exist"
        const val ACTION_NO_QUIZZES_IN_PROGRESS = "main.activity.action.no.quizzes.in.progress"
    }
}
