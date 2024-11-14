package com.cnam.medic_assist.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.cnam.medic_assist.R
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfilFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfilFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfilFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // J'initialise les vues et les écouteurs ici
        super.onViewCreated(view, savedInstanceState)

        val profileImageView: ImageView = view.findViewById(R.id.profile_image)
        val usernameTextView: TextView = view.findViewById(R.id.username)
        val mesInformations: TextView = view.findViewById(R.id.mes_informations)
        val mesProches: TextView = view.findViewById(R.id.mes_proches)
        val monAdresse: TextView = view.findViewById(R.id.mon_adresse)
        val moyenLocomotion: TextView = view.findViewById(R.id.moyen_locomotion)

        mesInformations.setOnClickListener {
            findNavController().navigate(R.id.action_profilFragment_to_mesInformationsFragment)
        }

        mesProches.setOnClickListener {
            findNavController().navigate(R.id.action_profilFragment_to_mesProchesFragment)
        }

        monAdresse.setOnClickListener {
            findNavController().navigate(R.id.action_profilFragment_to_monAdresseFragment)
        }

        moyenLocomotion.setOnClickListener {
            // Naviguer vers le fragment MoyenLocomotionFragment
            findNavController().navigate(R.id.action_profilFragment_to_moyenLocomotionFragment)
        }


    }
}