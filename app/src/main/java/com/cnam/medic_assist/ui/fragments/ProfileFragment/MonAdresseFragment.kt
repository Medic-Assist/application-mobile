package com.cnam.medic_assist.ui.fragments.ProfileFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.models.Patient

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

    // Pour faire fonctionner le bouton retour
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation des vues
        tvNumero = view.findViewById(R.id.input_numero)
        tvRue = view.findViewById(R.id.input_rue)
        tvCP = view.findViewById(R.id.input_code_postal)
        tvVille = view.findViewById(R.id.input_ville)

        showData()

        val btnBackToProfile: Button = view.findViewById(R.id.btn_back_to_profile)
        btnBackToProfile.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showData() {
        tvNumero.setText(data.numero_rue_principal)
        tvRue.setText(data.rue_principale)
        tvCP.setText("${data.codepostal_principal}")
        tvVille.setText(data.ville_principale)
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