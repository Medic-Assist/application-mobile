package com.cnam.medic_assist.ui.fragments.NavFragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.Constants
import com.cnam.medic_assist.datas.models.Patient
import com.cnam.medic_assist.datas.network.RetrofitClient
import com.cnam.medic_assist.ui.fragments.ProfileFragment.MesInformationsFragment
import com.cnam.medic_assist.ui.fragments.ProfileFragment.MesProchesFragment
import com.cnam.medic_assist.ui.fragments.ProfileFragment.MonAdresseFragment
import com.cnam.medic_assist.ui.fragments.ProfileFragment.MoyenLocomotionFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilFragment : Fragment() {
    private lateinit var patient: Patient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = requireContext().getSharedPreferences("UserCache", Context.MODE_PRIVATE)
        val id = sharedPreferences.getInt("id", 1) // null est la valeur par défaut si aucune valeur n'est trouvée
        if (id != null) {
            Log.d("UserCache", "Email récupéré depuis le cache : $id")
        } else {
            Log.d("UserCache", "Aucune valeur trouvée dans le cache.")
        }
        fetchData(id)

        val mesInformations: TextView = view.findViewById(R.id.mes_informations)
        val mesProches: TextView = view.findViewById(R.id.mes_proches)
        val monAdresse: TextView = view.findViewById(R.id.mon_adresse)
        val moyenLocomotion: TextView = view.findViewById(R.id.moyen_locomotion)

        mesInformations.setOnClickListener {
            if (::patient.isInitialized) {
                openProfileSubFragment(MesInformationsFragment.newInstance(patient))
            } else {
                Toast.makeText(requireContext(), "Patient non chargé, veuillez réessayer", Toast.LENGTH_SHORT).show()
            }
        }

        mesProches.setOnClickListener {
            if (::patient.isInitialized) {
                openProfileSubFragment(MesProchesFragment.newInstance(patient))
            } else {
                Toast.makeText(requireContext(), "Patient non chargé, veuillez réessayer", Toast.LENGTH_SHORT).show()
            }
        }

        monAdresse.setOnClickListener {
            if (::patient.isInitialized) {
                openProfileSubFragment(MonAdresseFragment.newInstance(patient))
            } else {
                Toast.makeText(requireContext(), "Patient non chargé, veuillez réessayer", Toast.LENGTH_SHORT).show()
            }
        }

        moyenLocomotion.setOnClickListener {
            if (::patient.isInitialized) {
                openProfileSubFragment(MoyenLocomotionFragment.newInstance(patient))
            } else {
                Toast.makeText(requireContext(), "Patient non chargé, veuillez réessayer", Toast.LENGTH_SHORT).show()
            }
        }
    }

    public fun fetchData(id : Int) {

        RetrofitClient.instance.getPatientById(id).enqueue(object :
            Callback<Patient> {
            override fun onResponse(call: Call<Patient>, response: Response<Patient>) {
                if (isAdded) {
                    if (response.isSuccessful && response.body() != null) {
                        patient = response.body()!!
                        Toast.makeText(requireContext(), "Chargement des informations réussi.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "${response.message()}", Toast.LENGTH_SHORT).show()

                        loadDefaultPatientData()
                    }
                }
            }

            override fun onFailure(call: Call<Patient>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Erreur réseau : ${t.message}. Chargement des données par défaut.", Toast.LENGTH_SHORT).show()
                }
                loadDefaultPatientData()
            }
        })
    }

    private fun loadDefaultPatientData() {
        if (Constants.patients.get(1) != null) {
            patient = Constants.patients.get(1)
            if (isAdded) {
                Toast.makeText(requireContext(), "Données par défaut chargées", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (isAdded) {
                Toast.makeText(requireContext(), "Aucune donnée par défaut disponible.", Toast.LENGTH_SHORT).show()
            }
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
