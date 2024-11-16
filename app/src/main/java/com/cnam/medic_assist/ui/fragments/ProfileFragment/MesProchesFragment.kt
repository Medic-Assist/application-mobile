package com.cnam.medic_assist.ui.fragments.ProfileFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.cnam.medic_assist.R

class MesProchesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // étendre la mise en page pour ce fragment (mes proches)
        return inflater.inflate(R.layout.fragment_mes_proches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBackToProfile: Button = view.findViewById(R.id.btn_back_to_profile)
        btnBackToProfile.setOnClickListener {
            // Retourner au ProfilFragment
            parentFragmentManager.popBackStack()
        }

        val btnModifier: Button = view.findViewById(R.id.btn_modifier)
        val btnEnregistrer: Button = view.findViewById(R.id.btn_enregistrer)

        btnModifier.setOnClickListener {
            // TODO : logique de modification
        }

        btnEnregistrer.setOnClickListener {
            // TODO : logique pour enregister les modifs
        }
    }
}
