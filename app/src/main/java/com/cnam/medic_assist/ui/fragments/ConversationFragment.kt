package com.cnam.medic_assist.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.room.Room
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.R
import com.cnam.medic_assist.ui.adapters.MessagesAdapter

class ConversationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessagesAdapter
    private lateinit var conversation: IRainbowConversation
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: ImageButton

    private val messagesChangeListener = object : IItemListChangeListener {
        override fun dataChanged() {
            loadMessages()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_conversation, container, false)

        recyclerView = view.findViewById(R.id.messagesRecyclerView)
        inputMessage = view.findViewById(R.id.inputMessage)
        sendButton = view.findViewById(R.id.sendButton)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MessagesAdapter(emptyList())
        recyclerView.adapter = adapter

        val bubbleId = arguments?.getString(ARG_BUBBLE_ID)
        if (bubbleId != null) {
            openConversation(bubbleId)
        }

        sendButton.setOnClickListener {
            val message = inputMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
            }
        }

        // Common messages
        val btnArrived = view.findViewById<Button>(R.id.btnArrived)
        val btnParking = view.findViewById<Button>(R.id.btnParking)
        val btnLate = view.findViewById<Button>(R.id.btnLate)
        val btnNotThere = view.findViewById<Button>(R.id.btnNotThere)

        btnArrived.setOnClickListener { sendMessage("Je suis là.") }
        btnParking.setOnClickListener { sendMessage("Je me gare.") }
        btnLate.setOnClickListener { sendMessage("Je suis en retard.") }
        btnNotThere.setOnClickListener { sendMessage("Je ne peux pas venir à mon rendez-vous.") }

        return view
    }

    private fun getBubbleById(bubbleId: String): Room? {
        val bubbles = RainbowSdk.instance().bubbles().getAllBubbles().copyOfDataList
        return bubbles.firstOrNull { it.id == bubbleId }
    }

    private fun openConversation(bubbleId: String) {
        val bubble = getBubbleById(bubbleId)
        if (bubble != null) {
            val conversation = RainbowSdk.instance().im().getConversationFromRoom(bubble)
            if (conversation != null) {
                this.conversation = conversation

                loadMessages()

                conversation.messages.registerChangeListener(messagesChangeListener)
            }
        }
    }

    private fun loadMessages() {
        RainbowSdk.instance().im().getMessagesFromConversation(conversation, 50)
        val messages = conversation.messages.copyOfDataList
        requireActivity().runOnUiThread {
            adapter.updateMessages(messages)
            recyclerView.scrollToPosition(messages.size - 1)
        }
    }

    private fun sendMessage(message: String) {
        RainbowSdk.instance().im().sendMessageToConversation(conversation, message)
        inputMessage.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        conversation.messages.unregisterChangeListener(messagesChangeListener)
    }

    companion object {
        private const val ARG_BUBBLE_ID = "bubble_id"

        fun newInstance(bubbleId: String): ConversationFragment {
            val fragment = ConversationFragment()
            val args = Bundle()
            args.putString(ARG_BUBBLE_ID, bubbleId)
            fragment.arguments = args
            return fragment
        }
    }
}
