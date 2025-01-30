package com.cnam.medic_assist.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.IMMessage
import com.cnam.medic_assist.R

class MessagesAdapter(private var messages: List<IMMessage>) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    fun updateMessages(newMessages: List<IMMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }


    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)

        // Différencier les messages envoyés et reçus
        if (message.isMsgSent) {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.message_sent_background)
        } else {
            holder.itemView.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.message_received_background)
        }
    }

    override fun getItemCount(): Int = messages.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)

        fun bind(message: IMMessage) {
            messageTextView.text = message.messageContent
        }
    }
}

