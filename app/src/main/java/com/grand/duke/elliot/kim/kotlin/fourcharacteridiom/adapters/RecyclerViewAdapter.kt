package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities.MainActivity
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.R
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.SelectedPart
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.activities.ViewPagerActivity
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel
import kotlinx.android.synthetic.main.item_view.view.*
import kotlinx.android.synthetic.main.item_view_search_result.view.*

class RecyclerViewAdapter(private val activity: AppCompatActivity, private val idioms: ArrayList<IdiomModel>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var filteredIdioms = idioms

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.scheduleLayoutAnimation()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemViewId =
            if (activity is MainActivity)
                R.layout.item_view
            else
                R.layout.item_view_search_result

        val view = LayoutInflater.from(parent.context).inflate(itemViewId, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredIdioms.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val idiom = filteredIdioms[position]

        if (activity is MainActivity)
            bindViewHolderInMainActivity(activity, holder, idiom)
        else if (activity is ViewPagerActivity)
            bindViewHolderInViewPagerActivity(holder, idiom)
    }

    private fun bindViewHolderInMainActivity(activity: MainActivity, holder: ViewHolder, idiom: IdiomModel) {
        holder.view.text_view_korean_characters.text = idiom.koreanCharacters
        holder.view.text_view_chinese_characters.text = idiom.chineseCharacters
        holder.view.text_view_description.text = idiom.description
        holder.view.text_view_meaning_of_each_character.text = idiom.formatMeanings()

        val imageResources =
            if (idiom.id in MainActivity.myIdiomIds)
                R.drawable.ic_star_blue_36dp
            else
                R.drawable.ic_star_grey_36dp

        holder.view.image_view_star.setImageResource(imageResources)
        holder.view.image_view_star.setOnClickListener {
            if (idiom.id in MainActivity.myIdiomIds) {
                activity.showToast("내 사자성어에서 제외되었습니다.", Toast.LENGTH_SHORT)
                MainActivity.myIdiomIds.remove(idiom.id)
                holder.view.image_view_star.setImageResource(R.drawable.ic_star_grey_36dp)
                if (MainActivity.selectedPart == SelectedPart.MY_IDIOMS) {
                    remove(idiom)
                }
            } else {
                activity.showToast("내 사자성어에 추가되었습니다.", Toast.LENGTH_SHORT)
                MainActivity.myIdiomIds.add(idiom.id)
                holder.view.image_view_star.setImageResource(R.drawable.ic_star_blue_36dp)
            }
        }
    }

    private fun bindViewHolderInViewPagerActivity(holder: ViewHolder, idiom: IdiomModel) {
        val text = "${idiom.koreanCharacters} (${idiom.chineseCharacters})"
        holder.view.text_view.text = text
        holder.view.setOnClickListener {
            (activity as ViewPagerActivity).scrollToIdiomPosition(idiom)
        }
    }

    private fun remove(idiom: IdiomModel) {
        val position = getPosition(idiom)
        filteredIdioms.removeAt(position)
        idioms.remove(idiom)
        Thread.sleep(200L)
        notifyItemRemoved(position)
    }

    fun getPosition(idiom: IdiomModel) = filteredIdioms.indexOf(idiom)

    fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val searchWord = charSequence.toString()
                filteredIdioms = if (searchWord.isBlank())
                    idioms
                else {
                    val filteringIdioms = arrayListOf<IdiomModel>()

                    for (idiom in idioms) {
                        if (idiom.koreanCharacters.contains(searchWord))
                            filteringIdioms.add(idiom)
                    }

                    filteringIdioms
                }

                return FilterResults().apply {
                    values = filteredIdioms
                }
            }

            override fun publishResults(charSequence: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                if (results?.values != null)
                    filteredIdioms = results.values as ArrayList<IdiomModel>

                notifyDataSetChanged()
            }
        }
    }
}