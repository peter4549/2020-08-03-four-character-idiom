package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.Category
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.Quiz
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.R
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.SelectedPart
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments.CompleteQuizDialogFragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.fragments.QuizFragment
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.QuizModel
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.room.Dao
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.room.Database
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class QuizActivity : AppCompatActivity() {

    private lateinit var idioms: ArrayList<IdiomModel>
    private lateinit var interstitialAd: InterstitialAd
    private lateinit var quizzes: ArrayList<QuizModel>
    private lateinit var dao : Dao
    private lateinit var database : Database
    private lateinit var toolbarTitle: String
    private var currentPage = 0
    var solvedQuizIds = mutableSetOf<Int>()

    private val adListener = object : AdListener() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorToolbar))

        database = Room.databaseBuilder(application,
            Database::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
        dao = database.dao()

        if (intent.action == MainActivity.ACTION_NO_QUIZZES_IN_PROGRESS) {
            CoroutineScope(Dispatchers.IO).launch {
                dao.nukeTable()
            }

            val count = intent.getIntExtra(Quiz.KEY_QUIZ_COUNT, 0)

            when (intent.getIntExtra(Quiz.KEY_QUIZ_PART, 0)) {
                SelectedPart.MY_IDIOMS -> {
                    toolbarTitle = "퀴즈 (내 사자성어)"
                    supportActionBar?.title = toolbarTitle
                    idioms = MainActivity.idioms
                        .filter { it.id in MainActivity.myIdiomIds }
                        .shuffled().take(count) as ArrayList<IdiomModel>
                }
                SelectedPart.ALL_IDIOMS -> {
                    toolbarTitle = "퀴즈 (모든 사자성어)"
                    supportActionBar?.title = toolbarTitle
                    idioms = MainActivity.idioms.shuffled().take(count) as ArrayList<IdiomModel>
                }
                SelectedPart.CIVIL_SERVICE_EXAMINATION_IDIOMS -> {
                    toolbarTitle = "퀴즈 (공무원 사자성어)"
                    supportActionBar?.title = toolbarTitle
                    idioms = MainActivity.idioms
                        .filter {
                            it.category == Category.CIVIL_SERVICE_EXAMINATION ||
                                    it.category == Category.BOTH
                        }.shuffled().take(count) as ArrayList<IdiomModel>
                }
                SelectedPart.SAT_IDIOMS -> {
                    toolbarTitle = "퀴즈 (수능 사자성어)"
                    supportActionBar?.title = toolbarTitle
                    idioms = MainActivity.idioms
                        .filter {
                            it.category == Category.SAT ||
                                    it.category == Category.BOTH
                        }.shuffled().take(count) as ArrayList<IdiomModel>
                }
                else -> {
                    Toast.makeText(this, "데이터를 불러오지 못했습니다.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            quizzes = createQuizzes(idioms)
        } else if (intent.action == MainActivity.ACTION_QUIZZES_IN_PROGRESS_EXIST) {
            val preferences = getSharedPreferences(Quiz.PREFERENCES_QUIZ, Context.MODE_PRIVATE)
            currentPage = preferences.getInt(Quiz.KEY_QUIZ_CURRENT_PAGE, 0)
            solvedQuizIds = preferences.getStringSet(Quiz.KEY_SOLVED_QUIZ_IDS, setOf())
                ?.map { it.toInt() }!!.toMutableSet()
            toolbarTitle = preferences.getString(Quiz.KEY_TOOLBAR_TITLE, "퀴즈")!!
            supportActionBar?.title = toolbarTitle

            runBlocking {
                launch(Dispatchers.IO) {
                    quizzes = dao.getAll() as ArrayList<QuizModel>
                }
            }
        }

        quizzes.shuffle(Random(0))
        view_pager.adapter = ViewPagerAdapter(this, quizzes)
        view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setPageText(position)
            }
        })

        moveToPage(currentPage)

        interstitialAd = InterstitialAd(this)
        interstitialAd.adUnitId = getString(R.string.interstitial_ad_unit_id)
        interstitialAd.loadAd(AdRequest.Builder().build())
        ad_view.loadAd(AdRequest.Builder().build())
        ad_view.adListener = adListener
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    private fun moveToPage(position: Int) {
        view_pager.setCurrentItem(position, false)
        setPageText(position)
    }

    private fun setPageText(currentPage: Int) {
        this.currentPage = currentPage
        val page = "${currentPage + 1}/${quizzes.count()}"
        val correctCount = "정답 수: ${solvedQuizIds.count()}/${quizzes.count()}"
        text_view_page.text = page
        text_view_correct_count.text = correctCount
    }

    fun updateSolvedQuizzesText() {
        val correctCount = "정답 수: ${solvedQuizIds.count()}/${quizzes.count()}"
        text_view_correct_count.text = correctCount
    }

    override fun onPause() {
        saveQuizzesInProgress()
        super.onPause()
    }

    private fun saveQuizzesInProgress() {
        val preferences = getSharedPreferences(Quiz.PREFERENCES_QUIZ, Context.MODE_PRIVATE)
        val editor = preferences.edit()

        editor.putInt(Quiz.KEY_QUIZ_CURRENT_PAGE, currentPage)
        editor.putBoolean(Quiz.KEY_QUIZ_IN_PROGRESS_EXIST, true)
        editor.putStringSet(Quiz.KEY_SOLVED_QUIZ_IDS, solvedQuizIds.map { it.toString() }.toSet())
        editor.putString(Quiz.KEY_TOOLBAR_TITLE, toolbarTitle)
        editor.apply()
    }

    private fun createQuizzes(idioms: ArrayList<IdiomModel>): ArrayList<QuizModel> {
        return runBlocking(Dispatchers.IO) {
            launch {
                for (idiom in idioms) {
                    val examples = MainActivity.idioms.shuffled().filter { it != idiom }
                        .take(3).map { "${it.koreanCharacters} (${it.chineseCharacters})" }
                            as ArrayList<String>
                    val correctAnswer = Random().nextInt(4)
                    examples.add(
                        correctAnswer,
                        "${idiom.koreanCharacters} (${idiom.chineseCharacters})"
                    )

                    val quiz = QuizModel(idiom.id, idiom.description, examples, correctAnswer)
                    dao.insert(quiz)
                }
            }.join()
            return@runBlocking dao.getAll() as ArrayList<QuizModel>
        }
    }

    fun checkAllQuizzesSolved() {
        if (quizzes.count() == solvedQuizIds.count())
            CompleteQuizDialogFragment().show(supportFragmentManager, TAG)
    }

    fun showInterstitialAd() {
        if (interstitialAd.isLoaded)
            interstitialAd.show()
        else
            println("$TAG: interstitial wasn't loaded")
    }

    private inner class ViewPagerAdapter(fragmentActivity: FragmentActivity, private val quizzes: ArrayList<QuizModel>)
        : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return quizzes.count()
        }

        override fun createFragment(position: Int): Fragment {
            val quizFragment =
                QuizFragment()
            quizFragment.setQuiz(quizzes[position])
            return quizFragment
        }
    }

    companion object {
        private const val TAG = "QuizActivity"
        private const val DATABASE_NAME = "quiz_activity_database_name_v1.0"
    }
}
