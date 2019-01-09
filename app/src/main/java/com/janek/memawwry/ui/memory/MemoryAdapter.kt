package com.janek.memawwry.ui.memory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.janek.memawwry.R
import kotlinx.android.synthetic.main.memory_card_item.view.*

class MemoryAdapter(val selectionCallback: (Card, Int) -> Unit) : RecyclerView.Adapter<MemoryCardViewHolder>() {

    private var items: List<Card> = emptyList()

    fun setCards(list: List<Card>) {
        items = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryCardViewHolder {
        return MemoryCardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.memory_card_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MemoryCardViewHolder, position: Int) {
        holder.cardId.text = items[position].cardId
        holder.imgUrl.text = items[position].imageUrl
        holder.uncovered.text = if (items[position].unCovered) "showing" else "covered"

        holder.cardId.setOnClickListener { selectionCallback(items[position], position) }
    }

}

class MemoryCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cardId: TextView = view.card_id
    val imgUrl: TextView = view.img_url
    val uncovered: TextView = view.uncovered
}