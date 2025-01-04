package com.cnam.medic_assist.ui.fragments.ProfileFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.cnam.medic_assist.R
import android.widget.Toast

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MesInformationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MesInformationsFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_mes_informations, container, false)
    }

    // Pour faire fonctionner le bouton retour
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBackToProfile: Button = view.findViewById(R.id.btn_back_to_profile)
        val btnModifier: Button = view.findViewById(R.id.btn_modifier)
        val btnEnregistrer: Button = view.findViewById(R.id.btn_enregistrer)

        val editTextFields = listOf(
            view.findViewById<EditText>(R.id.editTextNom),
            view.findViewById<EditText>(R.id.editTextPrenom),
            view.findViewById<EditText>(R.id.editTextDateNaissance),
            view.findViewById<EditText>(R.id.editTextSexe),
            view.findViewById<EditText>(R.id.editTextEmail),
            view.findViewById<EditText>(R.id.editTextIndicatif),
            view.findViewById<EditText>(R.id.editTextTelephone)
        )

        // Initialisation : afficher uniquement le bouton "Modifier"
        btnModifier.visibility = View.VISIBLE
        btnEnregistrer.visibility = View.GONE

        // Bouton Retour
        btnBackToProfile.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Bouton Modifier
        btnModifier.setOnClickListener {
            editTextFields.forEach { it.isEnabled = true } // on active la modification des inputs

            btnModifier.visibility = View.GONE
            btnEnregistrer.visibility = View.VISIBLE
        }

        // Bouton enregistrer (quand on clique)
        btnEnregistrer.setOnClickListener {
            editTextFields.forEach { it.isEnabled = false }
            btnModifier.isEnabled = true
            btnEnregistrer.isEnabled = false

            // Récupérer les valeurs des champs
            val nom = editTextFields[0].text.toString()
            val prenom = editTextFields[1].text.toString()
            val dateNaissance = editTextFields[2].text.toString()
            val sexe = editTextFields[3].text.toString()
            val email = editTextFields[4].text.toString()
            val indicatif = editTextFields[5].text.toString()
            val telephone = editTextFields[6].text.toString()

            // Vérifier les champs obligatoires
            if (nom.isBlank() || prenom.isBlank() || email.isBlank()) {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs obligatoires.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO : enregistrement en base

            // Après enregistrement, affichage d'un message de succès
            Toast.makeText(
                requireContext(),
                "Données enregistrées avec succès !",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    companion object {
        /**
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return Nouvelle instance de MesInformationsFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MesInformationsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}