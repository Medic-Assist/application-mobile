package com.cnam.medic_assist.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.IMMessage
import com.ale.infra.manager.room.Room
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.ale.rainbowsdk.Im
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.R
import com.cnam.medic_assist.ui.adapters.MessagesAdapter

class ConversationFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessagesAdapter
    private lateinit var conversation: IRainbowConversation
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: ImageButton
    private var isLoadingMessages = false

    // Listener pour détecter les changements dans la liste des messages
    private val messagesChangeListener = IItemListChangeListener {
        requireActivity().runOnUiThread {
            adapter.updateMessages(conversation.messages.copyOfDataList)
            recyclerView.scrollToPosition(adapter.itemCount - 1)
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

        return view
    }

    private fun openConversation(bubbleId: String) {
        val bubble = getBubbleById(bubbleId)
        if (bubble != null) {
            val conversation = RainbowSdk.instance().im().getConversationFromRoom(bubble)
            if (conversation != null) {
                this.conversation = conversation

                // Enregistrer le listener pour détecter les changements de messages
                conversation.messages.registerChangeListener(messagesChangeListener)

                loadMessages()
            } else {
                showToast("Impossible d'ouvrir la conversation.")
            }
        }
    }

    private fun loadMessages() {
        if (!isLoadingMessages) {
            isLoadingMessages = true
            RainbowSdk.instance().im().getMessagesFromConversation(conversation, 50)
            val messages = conversation.messages.copyOfDataList
            requireActivity().runOnUiThread {
                adapter.updateMessages(messages)
                recyclerView.scrollToPosition(messages.size - 1)
                isLoadingMessages = false
            }
        }
    }

    private fun sendMessage(message: String) {
        if (::conversation.isInitialized) {
            RainbowSdk.instance().im().sendMessageToConversation(conversation, message)
            inputMessage.text.clear()
        } else {
            showToast("Conversation non initialisée.")
        }
    }

    private fun getBubbleById(bubbleId: String): Room? {
        val bubbles = RainbowSdk.instance().bubbles().getAllBubbles().copyOfDataList
        return bubbles.firstOrNull { it.id == bubbleId }
    }

    private fun showToast(message: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    private val imListener = object : Im.IRainbowImListener {
        override fun onImReceived(conversation: IRainbowConversation, message: IMMessage?) {
            if (::conversation.isInitialized && this@ConversationFragment.conversation.id == conversation.id) {
                requireActivity().runOnUiThread {
                    adapter.updateMessages(this@ConversationFragment.conversation.messages.copyOfDataList)
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }

        override fun onImSent(conversation: IRainbowConversation, message: IMMessage) {
            if (::conversation.isInitialized && this@ConversationFragment.conversation.id == conversation.id) {
                requireActivity().runOnUiThread {
                    adapter.updateMessages(this@ConversationFragment.conversation.messages.copyOfDataList)
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }

        override fun onTypingStateChanged(
            contact: IRainbowContact,
            isTyping: Boolean,
            roomJid: String?
        ) {
            if (::conversation.isInitialized && conversation.room?.jid == roomJid) {
                requireActivity().runOnUiThread {
                    val isTypingTextView = view?.findViewById<TextView>(R.id.isTypingTextView)
                    if (isTyping) {
                        isTypingTextView?.text = "${contact.firstName} est en train d'écrire..."
                        isTypingTextView?.visibility = View.VISIBLE
                    } else {
                        isTypingTextView?.visibility = View.GONE
                    }
                }
            }
        }

        override fun onMessagesListUpdated(
            status: Int,
            conversation: IRainbowConversation?,
            messages: List<IMMessage>?
        ) {
            if (::conversation.isInitialized && this@ConversationFragment.conversation.id == conversation?.id) {
                requireActivity().runOnUiThread {
                    adapter.updateMessages(this@ConversationFragment.conversation.messages.copyOfDataList)
                }
            }
        }

        override fun onMoreMessagesListUpdated(
            status: Int,
            conversation: IRainbowConversation?,
            messages: List<IMMessage?>?
        ) {
            if (::conversation.isInitialized && this@ConversationFragment.conversation.id == conversation?.id) {
                requireActivity().runOnUiThread {
                    adapter.updateMessages(this@ConversationFragment.conversation.messages.copyOfDataList)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        RainbowSdk.instance().im().registerListener(imListener)
    }

    override fun onPause() {
        super.onPause()
        RainbowSdk.instance().im().unregisterListener(imListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::conversation.isInitialized) {
            conversation.messages.unregisterChangeListener(messagesChangeListener)
        }
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

