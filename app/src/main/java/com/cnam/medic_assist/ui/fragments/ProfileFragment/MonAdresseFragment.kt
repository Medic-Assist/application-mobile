package com.cnam.medic_assist.ui.fragments.ProfileFragment

// import android.os.Build.VERSION_CODES.R // ajout perso
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.cnam.medic_assist.R
import android.widget.Toast


class MonAdresseFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_mon_adresse, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBackToProfile: Button = view.findViewById(R.id.btn_back_to_profile)
        val btnModifier: Button = view.findViewById(R.id.btn_modifier)
        val btnEnregistrer: Button = view.findViewById(R.id.btn_enregistrer)

        val inputNumero: EditText = view.findViewById(R.id.input_numero)
        val inputRue: EditText = view.findViewById(R.id.input_rue)
        val inputCodePostal: EditText = view.findViewById(R.id.input_code_postal)
        val inputPays: EditText = view.findViewById(R.id.input_pays)

        val inputs = listOf(inputNumero, inputRue, inputCodePostal, inputPays)

        // Initialisation : verrouiller les champs
        inputs.forEach { it.isEnabled = false }
        btnEnregistrer.visibility = View.GONE // Masquer "Enregistrer" par défaut

        // Retour au profil
        btnBackToProfile.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Bouton Modifier
        btnModifier.setOnClickListener {
            inputs.forEach { it.isEnabled = true } // Déverrouiller les champs
            btnModifier.visibility = View.GONE // Masquer "Modifier"
            btnEnregistrer.visibility = View.VISIBLE // Afficher "Enregistrer"
        }

        // Bouton Enregistrer
        btnEnregistrer.setOnClickListener {
            val numero = inputNumero.text.toString()
            val rue = inputRue.text.toString()
            val codePostal = inputCodePostal.text.toString()
            val pays = inputPays.text.toString()

            // Validation des champs
            if (numero.isBlank() || rue.isBlank() || codePostal.isBlank() || pays.isBlank()) {
                // Afficher un message d'erreur à l'utilisateur
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sauvegarde des données (à implémenter selon votre logique)

            // Verrouiller les champs
            inputs.forEach { it.isEnabled = false }
            btnModifier.visibility = View.VISIBLE // Réafficher "Modifier"
            btnEnregistrer.visibility = View.GONE // Masquer "Enregistrer"
        }
    }

    companion object {
        const val ARG_PARAM1 = "param1"
        const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MonAdresseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}