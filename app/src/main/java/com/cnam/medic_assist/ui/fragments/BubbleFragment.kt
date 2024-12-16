package com.cnam.medic_assist.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ale.infra.manager.room.CreateRoomBody
import com.ale.infra.manager.room.IRainbowRoom
import com.ale.infra.rest.listeners.RainbowListener
import com.ale.infra.rest.room.RoomRepository
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.R

class BubbleFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bubble, container, false)

        // Bouton pour créer une bulle
        val createBubbleButton: Button = view.findViewById(R.id.createBubbleButton)
        recyclerView = view.findViewById(R.id.bubblesRecyclerView)

        // Configurer RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Lister les bulles automatiquement
        listBubbles()

        // Création de bulle avec mise à jour de la liste
        createBubbleButton.setOnClickListener { createBubble() }

        return view
    }

    private fun createBubble() {
        val body = CreateRoomBody.Builder()
            .name("Nouvelle Bulle") // Nom de la bulle
            .topic("Sujet de discussion") // Sujet ou description de la bulle
            .isVisible(true) // Définir si la bulle est publique ou privée
            .withHistory(true) // Garder ou non l'historique
            .disableNotifications(false) // Activer ou désactiver les notifications
            .autoAcceptInvitation(true) // Accepter automatiquement les invitations
            .muteParticipantsUponEntry(false) // Ne pas couper les participants en entrant
            .playEntryTone(true) // Jouer un son à l'entrée
            .build()

        // Appel à la méthode createBubble avec les bons types
        RainbowSdk.instance().bubbles().createBubble(body, object : RainbowListener<IRainbowRoom, RoomRepository.CreateRoomError> {
            override fun onSuccess(bubble: IRainbowRoom) {
                Log.d("RainbowSDK", "Bulle créée avec succès : ${bubble.name}")

                // Recharger la liste après la création de la bulle
                requireActivity().runOnUiThread { listBubbles() }
            }
        })
    }

    private fun listBubbles() {
        // Obtenir toutes les bulles via le SDK
        val bubbles = RainbowSdk.instance().bubbles().getAllBubbles()

        // Adapter pour afficher les bulles
        val adapter = BubblesAdapter(bubbles) { bubble ->
            openBubbleChat(bubble)
        }
        recyclerView.adapter = adapter
    }

    private fun openBubbleChat(bubble: IRainbowRoom) {
        // Ouvrir la bulle choisie
        val chatFragment = BubbleChatFragment.newInstance(bubble.id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, chatFragment)
            .addToBackStack(null)
            .commit()
    }
}
