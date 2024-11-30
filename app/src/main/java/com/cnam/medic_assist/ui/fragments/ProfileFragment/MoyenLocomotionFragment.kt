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
import com.cnam.medic_assist.datas.Constants
import com.cnam.medic_assist.datas.models.ModeTransport
import com.cnam.medic_assist.datas.models.Patient
import com.cnam.medic_assist.datas.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PATIENT = "arg_patient"
class MoyenLocomotionFragment (patient : Patient): Fragment() {
    private var modes : List<ModeTransport> = listOf(ModeTransport("Chargement..."))
    private lateinit var spinnerModesLocomotion: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_moyen_locomotion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchData()

        // Bouton Retour
        val btnBackToProfile: Button = view.findViewById(R.id.btn_back_to_profile)
        btnBackToProfile.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        spinnerModesLocomotion = requireView().findViewById(R.id.spinner_modes_locomotion)
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

    private fun fetchData() {
        RetrofitClient.instance.getModesTransports().enqueue(object :
            Callback<List<ModeTransport>> {
            override fun onResponse(call: Call<List<ModeTransport>>, response: Response<List<ModeTransport>>) {
                if (isAdded) {
                    if (response.isSuccessful && response.body() != null) {
                        modes = response.body()!!
                        updateSpinner(modes)
                        Toast.makeText(requireContext(), "Chargement des informations réussi.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "${response.message()}", Toast.LENGTH_SHORT).show()
                        modes = Constants.modes_transports
                    }
                }
            }

            override fun onFailure(call: Call<List<ModeTransport>>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Erreur réseau : ${t.message}. Chargement des données par défaut.", Toast.LENGTH_SHORT).show()
                }
                modes = Constants.modes_transports
            }
        })
    }

    private fun updateSpinner(modes: List<ModeTransport>) {
        // Convertir les objets ModeTransport en une liste de chaînes
        val modeNames = modes.map { it.transport_mode }

        // Créer l'adaptateur avec cette liste
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, modeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModesLocomotion.adapter = adapter
    }


    companion object {
        @JvmStatic
        fun newInstance(patient: Patient) =
            MoyenLocomotionFragment(patient).apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PATIENT, patient)
                }
            }
    }

}
