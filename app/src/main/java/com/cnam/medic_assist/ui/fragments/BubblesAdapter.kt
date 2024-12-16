package com.cnam.medic_assist.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.room.IRainbowRoom
import com.cnam.medic_assist.R

class BubblesAdapter(
    private val bubbles: List<IRainbowRoom>,
    private val onBubbleClick: (IRainbowRoom) -> Unit
) : RecyclerView.Adapter<BubblesAdapter.BubbleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BubbleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bubble, parent, false)
        return BubbleViewHolder(view)
    }

    override fun onBindViewHolder(holder: BubbleViewHolder, position: Int) {
        val bubble = bubbles[position]
        holder.bind(bubble)
        holder.itemView.setOnClickListener { onBubbleClick(bubble) }
    }

    override fun getItemCount(): Int = bubbles.size

    class BubbleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bubbleNameTextView: TextView = itemView.findViewById(R.id.bubbleNameTextView)

        fun bind(bubble: IRainbowRoom) {
            bubbleNameTextView.text = bubble.name
        }
    }
}
