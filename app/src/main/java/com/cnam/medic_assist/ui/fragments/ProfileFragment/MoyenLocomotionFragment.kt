package com.cnam.medic_assist.ui.fragments.ProfileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cnam.medic_assist.R

class MoyenLocomotionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_moyen_locomotion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bouton Retour
        val btnBackToProfile: Button = view.findViewById(R.id.btn_back_to_profile)
        btnBackToProfile.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Spinner pour les options de locomotion
        val spinnerModesLocomotion: Spinner = view.findViewById(R.id.spinner_modes_locomotion)
        val modes = arrayOf("Voiture", "Taxi", "Transports en commun", "Vélo", "Marche")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, modes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModesLocomotion.adapter = adapter

        // Bouton Modifier
        val btnModifier: Button = view.findViewById(R.id.btn_modifier)
        btnModifier.setOnClickListener {
            spinnerModesLocomotion.isEnabled = true
            Toast.makeText(requireContext(), "Vous pouvez maintenant modifier votre choix", Toast.LENGTH_SHORT).show()
        }

        // Bouton Enregistrer
        val btnEnregistrer: Button = view.findViewById(R.id.btn_enregistrer)
        btnEnregistrer.setOnClickListener {
            val selectedMode = spinnerModesLocomotion.selectedItem.toString()
            spinnerModesLocomotion.isEnabled = false
            Toast.makeText(requireContext(), "Mode de locomotion enregistré : $selectedMode", Toast.LENGTH_SHORT).show()
        }
    }
}
