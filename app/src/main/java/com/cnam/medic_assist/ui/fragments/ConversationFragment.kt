package com.cnam.medic_assist.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.list.IItemListChangeListener
import com.ale.infra.manager.IMMessage
import com.ale.infra.manager.room.Room
import com.ale.infra.proxy.conversation.IRainbowConversation
import com.ale.rainbowsdk.Im
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.R
import com.cnam.medic_assist.ui.adapters.MessagesAdapter
import com.google.android.material.textfield.TextInputEditText
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ConversationFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessagesAdapter
    private lateinit var conversation: IRainbowConversation

    // Vues du layout
    private lateinit var inputMessage: TextInputEditText
    private lateinit var sendButton: ImageView
    private lateinit var typingContainer: LinearLayout
    private lateinit var typingAvatar: ImageView
    private lateinit var typingTextView: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    // En-tête bulle
    private lateinit var bubblePhoto: ImageView
    private lateinit var bubbleTitle: TextView

    private var isLoadingMessages = false

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

        // 1) Récupérer les vues
        bubblePhoto = view.findViewById(R.id.bubble_photo)
        bubbleTitle = view.findViewById(R.id.bubble_title)
        typingContainer = view.findViewById(R.id.typing_container)
        typingAvatar = view.findViewById(R.id.typing_avatar)
        typingTextView = view.findViewById(R.id.typing_text)

        swipeRefresh = view.findViewById(R.id.swipe_messages)
        recyclerView = view.findViewById(R.id.messagesRecyclerView)
        inputMessage = view.findViewById(R.id.inputMessage)
        sendButton = view.findViewById(R.id.sendButton)

        // 2) Configurer RecyclerView + Adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MessagesAdapter(emptyList())
        recyclerView.adapter = adapter

        // 3) SwipeRefresh pour charger plus de messages
        swipeRefresh.setOnRefreshListener {
            loadMoreMessages()
        }

        // 4) Bouton d’envoi
        sendButton.setOnClickListener {
            val message = inputMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
            }
        }

        // 5) Ouvrir la conversation via l’ID bulle passé en argument
        val bubbleId = arguments?.getString(ARG_BUBBLE_ID)
        bubbleId?.let {
            openConversation(it)
        }

        return view
    }

    private fun openConversation(bubbleId: String) {
        val bubble = getBubbleById(bubbleId)
        if (bubble != null) {
            // Afficher le nom de la bulle
            bubbleTitle.text = bubble.name

            // Charger la photo de la bulle si vous avez un URL ou un moyen via Rainbow
            // Ex: Glide, Picasso...
            // bubble.photoURL => si Rainbow le fournit
            // Sinon, placeholder déjà mis dans le layout
            /*
            val url = bubble.photoUrl
            if (!url.isNullOrEmpty()) {
                Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ic_user_placeholder)
                    .into(bubblePhoto)
            }
            */

            // Récup conversation
            val conv = RainbowSdk.instance().im().getConversationFromRoom(bubble)
            if (conv != null) {
                this.conversation = conv
                conversation.messages.registerChangeListener(messagesChangeListener)
                loadMessages()
            } else {
                showToast("Impossible d'ouvrir la conversation.")
            }
        } else {
            showToast("Bubble introuvable.")
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

    private fun loadMoreMessages() {
        // Logique éventuelle pour charger plus d’historique
        // ...
        swipeRefresh.isRefreshing = false
    }

    private fun sendMessage(message: String) {
        if (::conversation.isInitialized) {
            RainbowSdk.instance().im().sendMessageToConversation(conversation, message)
            inputMessage.text?.clear()
        } else {
            showToast("Conversation non initialisée.")
        }
    }

    private fun getBubbleById(bubbleId: String): Room? {
        val bubbles = RainbowSdk.instance().bubbles().getAllBubbles().copyOfDataList
        return bubbles.firstOrNull { it.id == bubbleId }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // **********************************
    // Listener IM pour mettre à jour l'UI
    // **********************************
    private val imListener = object : Im.IRainbowImListener {
        override fun onImReceived(conv: IRainbowConversation, message: IMMessage?) {
            if (::conversation.isInitialized && conversation.id == conv.id) {
                requireActivity().runOnUiThread {
                    adapter.updateMessages(conversation.messages.copyOfDataList)
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }

        override fun onImSent(conv: IRainbowConversation, message: IMMessage) {
            if (::conversation.isInitialized && conversation.id == conv.id) {
                requireActivity().runOnUiThread {
                    adapter.updateMessages(conversation.messages.copyOfDataList)
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }

        override fun onTypingStateChanged(
            contact: IRainbowContact,
            isTyping: Boolean,
            roomJid: String?
        ) {
            // Affichage "X est en train d'écrire..." + photo
            if (::conversation.isInitialized && conversation.room?.jid == roomJid) {
                requireActivity().runOnUiThread {
                    if (isTyping) {
                        typingContainer.visibility = View.VISIBLE
                        typingTextView.text = "${contact.firstName} est en train d'écrire..."
                        // Charger la photo du contact s’il y en a une
                        // Ex : Glide / Rainbow contact avatar
                        /*
                        val contactPhotoUrl = contact.photoURL
                        Glide.with(requireContext())
                            .load(contactPhotoUrl)
                            .placeholder(R.drawable.ic_user_placeholder)
                            .into(typingAvatar)
                        */
                    } else {
                        typingContainer.visibility = View.GONE
                    }
                }
            }
        }

        override fun onMessagesListUpdated(
            status: Int,
            conv: IRainbowConversation?,
            messages: List<IMMessage>?
        ) {
            if (::conversation.isInitialized && conversation.id == conv?.id) {
                requireActivity().runOnUiThread {
                    adapter.updateMessages(conversation.messages.copyOfDataList)
                }
            }
        }

        override fun onMoreMessagesListUpdated(
            status: Int,
            conv: IRainbowConversation?,
            messages: List<IMMessage?>?
        ) {
            if (::conversation.isInitialized && conversation.id == conv?.id) {
                requireActivity().runOnUiThread {
                    adapter.updateMessages(conversation.messages.copyOfDataList)
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
