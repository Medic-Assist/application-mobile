package com.cnam.medic_assist.ui.fragments.NavFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.cnam.medic_assist.R
import com.cnam.medic_assist.ui.fragments.ProfileFragment.MesInformationsFragment
import com.cnam.medic_assist.ui.fragments.ProfileFragment.MesProchesFragment
import com.cnam.medic_assist.ui.fragments.ProfileFragment.MonAdresseFragment
import com.cnam.medic_assist.ui.fragments.ProfileFragment.MoyenLocomotionFragment

class ProfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mesInformations: TextView = view.findViewById(R.id.mes_informations)
        val mesProches: TextView = view.findViewById(R.id.mes_proches)
        val monAdresse: TextView = view.findViewById(R.id.mon_adresse)
        val moyenLocomotion: TextView = view.findViewById(R.id.moyen_locomotion)

        mesInformations.setOnClickListener {
            openProfileSubFragment(MesInformationsFragment())
        }

        mesProches.setOnClickListener {
            openProfileSubFragment(MesProchesFragment())
        }

        monAdresse.setOnClickListener {
            openProfileSubFragment(MonAdresseFragment())
        }

        moyenLocomotion.setOnClickListener {
            openProfileSubFragment(MoyenLocomotionFragment())
        }
    }

    private fun openProfileSubFragment(fragment: Fragment) {
        // Remplace le contenu principal par le sous-fragment
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null) // Permet le retour en arrière
            .commit()
    }
}
