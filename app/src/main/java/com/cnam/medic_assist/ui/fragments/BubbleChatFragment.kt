package com.cnam.medic_assist.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ale.infra.rest.listeners.RainbowListener
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.R

class BubbleChatFragment : Fragment() {

    companion object {
        private const val ARG_BUBBLE_ID = "bubbleId"

        fun newInstance(bubbleId: String): BubbleChatFragment {
            val fragment = BubbleChatFragment()
            val args = Bundle()
            args.putString(ARG_BUBBLE_ID, bubbleId)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var bubbleId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bubbleId = arguments?.getString(ARG_BUBBLE_ID) ?: throw IllegalArgumentException("Bubble ID required")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bubble_chat, container, false)

        // Initialize RecyclerView and send message components here
        return view
    }

    // Fetch and display messages
    /*private fun loadMessages() {
        val messages = RainbowSdk.instance().bubbles().getMessagesForBubble(bubbleId)
        // Update RecyclerView with messages
    }*/

    /*// Send a new message
    private fun sendMessage(message: String) {
        RainbowSdk.instance().bubbles().sendMessageToBubble(bubbleId, message, object :
            RainbowListener<Void, Throwable> {
            override fun onSuccess(value: Void?) {
                // Refresh message list
                loadMessages()
            }
        })
    }*/
}
