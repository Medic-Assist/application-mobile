package com.cnam.medic_assist.ui.fragments.ProfileFragment

// import android.os.Build.VERSION_CODES.R // ajout perso
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.models.Patient
import com.cnam.medic_assist.datas.models.Proche
import com.cnam.medic_assist.datas.network.RetrofitClient
import com.cnam.medic_assist.ui.fragments.NavFragments.ProfilFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PATIENT = "arg_patient"

/**
 * A simple [Fragment] subclass.
 * Use the [MonAdresseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MonAdresseFragment(patient : Patient) : Fragment() {
    private lateinit var data: Patient
    private lateinit var tvNumero: EditText
    private lateinit var tvRue: EditText
    private lateinit var tvCP: EditText
    private lateinit var tvVille: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            data = it.getParcelable(ARG_PATIENT)!!
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

        // Initialisation des vues
        tvNumero = view.findViewById(R.id.input_numero)
        tvRue = view.findViewById(R.id.input_rue)
        tvCP = view.findViewById(R.id.input_code_postal)
        tvVille = view.findViewById(R.id.input_ville)

        showData()

        val btnBackToProfile: Button = view.findViewById(R.id.btn_back_to_profile)
        val btnModifier: Button = view.findViewById(R.id.btn_modifier)
        val btnEnregistrer: Button = view.findViewById(R.id.btn_enregistrer)

        val inputs = listOf(tvNumero, tvRue, tvCP, tvVille)

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
            val numero = tvNumero.text.toString()
            val rue = tvRue.text.toString()
            val codePostal = tvCP.text.toString()
            val ville = tvVille.text.toString()

            // Validation des champs
            if (numero.isBlank() || rue.isBlank() || codePostal.isBlank() || ville.isBlank()) {
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

        val btnModifier: Button = view.findViewById(R.id.btn_modifier)
        btnModifier.setOnClickListener {
            //TODO : rendre saisisable les input
            Toast.makeText(requireContext(), "Vous pouvez maintenant modifier votre choix", Toast.LENGTH_SHORT).show()
        }

        // Bouton Enregistrer
        val btnEnregistrer: Button = view.findViewById(R.id.btn_enregistrer)
        btnEnregistrer.setOnClickListener {
            //Récuperation des info du fragment
            val newPatient = data
            newPatient.numero_rue_principal = tvNumero.text.toString()
            newPatient.rue_principale = tvRue.text.toString()
            val cp = tvCP.text.toString()

            if (cp.length == 5 && cp.all { it.isDigit() }) {
                newPatient.codepostal_principal = cp
            } else {
                Toast.makeText(context, "Le code postal doit être un nombre à 5 chiffres.", Toast.LENGTH_SHORT).show()
            }

            newPatient.ville_principale = tvVille.text.toString()

            updateAdresse(newPatient)
        }
    }

    private fun showData() {
        tvNumero.setText(data.numero_rue_principal)
        tvRue.setText(data.rue_principale)
        tvCP.setText(data.codepostal_principal)
        tvVille.setText(data.ville_principale)
    }

    fun updateAdresse(patient : Patient) {

        RetrofitClient.instance.updatePatient(patient.iduser!!, patient).enqueue(object :
            Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Patient mis à jour.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Echec de la modification du patient: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Erreur réseau: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(patient: Patient) =
            MonAdresseFragment(patient).apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PATIENT, patient)
                }
            }
    }
}