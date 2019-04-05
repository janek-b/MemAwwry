package com.janek.memawwry.ui.memory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.janek.memawwry.R
import com.janek.memawwry.data.CardState
import com.janek.memawwry.data.MemoryCard
import com.janek.memawwry.ui.GlideApp
import kotlinx.android.synthetic.main.memory_card_item.view.*

class MemoryAdapter(
    private val cardClickListener: (MemoryCard) -> Unit
) : ListAdapter<MemoryCard, MemoryCardViewHolder>(
    MemoryCardDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryCardViewHolder {
        return MemoryCardViewHolder(parent.inflate(R.layout.memory_card_item))
    }

    override fun onBindViewHolder(holder: MemoryCardViewHolder, position: Int) {
        holder.bind(getItem(position), cardClickListener)
    }

}

class MemoryCardDiffCallback : DiffUtil.ItemCallback<MemoryCard>() {
    override fun areItemsTheSame(oldItem: MemoryCard, newItem: MemoryCard): Boolean {
        return oldItem.cardId == newItem.cardId
    }

    override fun areContentsTheSame(oldItem: MemoryCard, newItem: MemoryCard): Boolean {
        return oldItem == newItem
    }
}

class MemoryCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cardImage: ImageView = itemView.card_image

    fun bind(item: MemoryCard, cardClickListener: (MemoryCard) -> Unit) {
        GlideApp.with(itemView.context)
            .load(getImageUrl(item))
            .fallback(R.drawable.dog_paw)
            .centerCrop()
            .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
            .into(cardImage)

        itemView.setOnClickListener { if (item.state != CardState.UNCOVERED) cardClickListener(item) }
    }

    private fun getImageUrl(item: MemoryCard): String? {
        return if (item.state == CardState.SELECTED || item.state == CardState.UNCOVERED) item.imageUrl else null
    }
}


private fun ViewGroup.inflate(
    @LayoutRes layoutRes: Int,
    attachToRoot: Boolean = false
): View = LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)