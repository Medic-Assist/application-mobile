package com.cnam.medic_assist.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.IMMessage
import com.cnam.medic_assist.R
import java.text.SimpleDateFormat
import java.util.Locale

class MessagesAdapter(private var messages: List<IMMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    fun updateMessages(newMessages: List<IMMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.isMsgSent) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SENT -> {
                val view = inflater.inflate(R.layout.item_message_sent, parent, false)
                SentMessageViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_message_received, parent, false)
                ReceivedMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    // ViewHolder pour un message envoyé
    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.message_content)
        private val timeTextView: TextView = itemView.findViewById(R.id.message_time)
        private val stateImageView: ImageView = itemView.findViewById(R.id.message_state)

        fun bind(message: IMMessage) {
            contentTextView.text = message.messageContent
            // Exemple d’affichage d’une heure "HH:mm"
            timeTextView.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(message.messageDate)
            // Gérer l’état (envoyé, lu, etc.) si votre message le permet
            // stateImageView.setImageResource(...)
        }
    }

    // ViewHolder pour un message reçu
    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contentTextView: TextView = itemView.findViewById(R.id.message_content)
        private val timeTextView: TextView = itemView.findViewById(R.id.message_time)

        fun bind(message: IMMessage) {
            contentTextView.text = message.messageContent
            timeTextView.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(message.messageDate)
            // stateImageView.setImageResource(...) ou setTint(...)
        }
    }
}
