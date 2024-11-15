package com.cnam.medic_assist.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.list.ArrayItemList
import com.ale.infra.manager.room.IRainbowRoom
import com.ale.infra.manager.room.Room
import com.cnam.medic_assist.R

class BubblesAdapter(
    //private val bubbles: ArrayItemList<Room>,
    private val bubbles: ArrayItemList<Room>,
    private val onBubbleClick: (IRainbowRoom) -> Unit
) : RecyclerView.Adapter<BubblesAdapter.BubbleViewHolder>() {

    class BubbleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.bubbleName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BubbleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bubble, parent, false)
        return BubbleViewHolder(view)
    }

    override fun onBindViewHolder(holder: BubbleViewHolder, position: Int) {
        val bubble = bubbles[position]
        holder.name.text = bubble.name
        holder.itemView.setOnClickListener { onBubbleClick(bubble) }
    }

    override fun getItemCount(): Int {
        return bubbles.count
    }
}
