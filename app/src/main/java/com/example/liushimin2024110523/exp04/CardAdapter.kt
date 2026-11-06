package com.example.liushimin2024110523.exp04

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/** View holders draw model state; all input is sent to the controller. */
class CardAdapter(
    private val game: CardMatchingGame,
    private val onCardClick: (Int) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardHolder>() {
    class CardHolder(val cardView: PlayingCardView) : RecyclerView.ViewHolder(cardView)
    override fun getItemCount() = game.cardCount
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val margin = (4 * parent.resources.displayMetrics.density).toInt()
        return CardHolder(PlayingCardView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                parent.resources.getDimensionPixelSize(R.dimen.card_height)
            ).apply {
                setMargins(margin, margin, margin, margin)
            }
        }).also { holder ->
            holder.cardView.setOnClickListener {
                val position = holder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) onCardClick(position)
            }
        }
    }
    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.cardView.bind(game.cardAtIndex(position), position, animateChange = false)
    }
    override fun onBindViewHolder(holder: CardHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) onBindViewHolder(holder, position)
        else holder.cardView.bind(game.cardAtIndex(position), position, animateChange = true)
    }
    override fun onViewRecycled(holder: CardHolder) {
        holder.cardView.resetAnimation()
        super.onViewRecycled(holder)
    }
    fun refreshCards() = notifyItemRangeChanged(0, itemCount, Unit)
}
