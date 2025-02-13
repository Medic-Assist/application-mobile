package com.cnam.medic_assist.ui.fragments.NavFragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.room.CreateRoomBody
import com.ale.infra.manager.room.IRainbowRoom
import com.ale.infra.rest.listeners.RainbowListener
import com.ale.infra.rest.room.RoomRepository
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.MainActivity
import com.cnam.medic_assist.R
import com.cnam.medic_assist.ui.adapters.BubblesAdapter
import com.cnam.medic_assist.ui.fragments.ConversationFragment
import androidx.recyclerview.widget.DividerItemDecoration
import android.graphics.drawable.InsetDrawable
import com.cnam.medic_assist.ui.fragments.ProfileFragment.CreateBubbleDialogFragment


class BubbleFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var createBubbleButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bubble, container, false)

        val createBubbleButton: Button = view.findViewById(R.id.createBubbleButton)
        recyclerView = view.findViewById(R.id.bubblesRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)


        // séparateurs entre les lignes
        val dividerItemDecoration = DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        )
        recyclerView.addItemDecoration(dividerItemDecoration)

        // chargement des bulles
        listBubbles()

        //createBubbleButton.setOnClickListener { createBubble() }
        createBubbleButton.setOnClickListener {
            val dialog = CreateBubbleDialogFragment()
            dialog.show(parentFragmentManager, "CreateBubbleDialogFragment")
        }
        return view
    }

    /*private fun createBubble() {
        val body = CreateRoomBody.Builder()
            .name("Nouvelle Bulle")
            .topic("Sujet de discussion")
            .isVisible(true)
            .withHistory(true)
            .disableNotifications(false)
            .autoAcceptInvitation(true)
            .muteParticipantsUponEntry(false)
            .playEntryTone(true)
            .build()

        RainbowSdk.instance().bubbles().createBubble(body, object : RainbowListener<IRainbowRoom, RoomRepository.CreateRoomError> {
            override fun onSuccess(bubble: IRainbowRoom) {
                Log.d("RainbowSDK", "Bulle créée avec succès : ${bubble.name}")
                requireActivity().runOnUiThread { listBubbles() }
            }
        })
    }*/

    public fun listBubbles() {
        val bubbles = RainbowSdk.instance().bubbles().getAllBubbles().copyOfDataList

        val adapter = BubblesAdapter(bubbles) { bubble ->
            openBubbleChat(bubble)
        }
        recyclerView.adapter = adapter
    }

    private fun openBubbleChat(bubble: IRainbowRoom) {
        val conversationFragment = ConversationFragment.newInstance(bubble.id)
        Log.d("bubbleid", bubble.id)
        // Remplacer le fragment actuel par ConversationFragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, conversationFragment)
            .addToBackStack(null)
            .commit()
    }
}
