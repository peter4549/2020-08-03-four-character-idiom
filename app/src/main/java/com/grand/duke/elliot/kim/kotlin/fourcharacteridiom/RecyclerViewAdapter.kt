package com.grand.duke.elliot.kim.kotlin.fourcharacteridiom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.grand.duke.elliot.kim.kotlin.fourcharacteridiom.model.IdiomModel
import kotlinx.android.synthetic.main.item_view.view.*

class RecyclerViewAdapter(private val activity: MainActivity, private val idioms: ArrayList<IdiomModel>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return idioms.size
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        val idiom = idioms[position]

        holder.view.text_view_korean_characters.text = idiom.koreanCharacters
        holder.view.text_view_chinese_characters.text = idiom.chineseCharacters
        holder.view.text_view_description.text = idiom.description
        holder.view.text_view_meaning_of_each_character.text = idiom.formatMeanings()

        val imageResources =
            if (idiom.id in activity.myIdiomIds)
                R.drawable.ic_star_yellow_48dp
            else
                R.drawable.ic_star_grey_48dp

        holder.view.image_view_star.setImageResource(imageResources)

        holder.view.image_view_star.setOnClickListener {
            if (idiom.id in activity.myIdiomIds) {
                activity.myIdiomIds.remove(idiom.id)
                holder.view.image_view_star.setImageResource(R.drawable.ic_star_grey_48dp)
                if (activity.selectedPart == SelectedPart.MY_IDIOMS) {
                    remove(idiom)
                }
            } else {
                activity.myIdiomIds.add(idiom.id)
                holder.view.image_view_star.setImageResource(R.drawable.ic_star_yellow_48dp)
            }
        }
    }

    private fun remove(idiom: IdiomModel) {
        val position = getPosition(idiom)
        idioms.removeAt(position)
        notifyItemRemoved(position)
        activity.showToast("목록에서 제거되었습니다.")
    }

    private fun getPosition(idiom: IdiomModel) = idioms.indexOf(idiom)
}