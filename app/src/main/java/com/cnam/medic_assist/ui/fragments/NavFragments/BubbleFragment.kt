package com.cnam.medic_assist.ui.fragments.NavFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ale.infra.manager.room.CreateRoomBody
import com.ale.infra.manager.room.IRainbowRoom
import com.ale.infra.rest.listeners.RainbowListener
import com.ale.infra.rest.room.RoomRepository
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.R

class BubbleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bubble, container, false)

        // Boutons pour créer et lister les bulles
        val createBubbleButton: Button = view.findViewById(R.id.createBubbleButton)
        val listBubblesButton: Button = view.findViewById(R.id.listBubblesButton)

        createBubbleButton.setOnClickListener { createBubble() }
        listBubblesButton.setOnClickListener { listBubbles() }

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
            }
        })
    }

    private fun listBubbles() {
        // Obtenez toutes les bulles via le SDK
        val bubbles = RainbowSdk.instance().bubbles().getAllBubbles()

    }

}
