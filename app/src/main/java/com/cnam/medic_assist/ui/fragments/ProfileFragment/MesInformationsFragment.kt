package com.cnam.medic_assist.ui.fragments.ProfileFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.models.Patient
import com.cnam.medic_assist.datas.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale


private const val ARG_PATIENT = "arg_patient"

/**
 * A simple [Fragment] subclass.
 * Use the [MesInformationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MesInformationsFragment(patient: Patient) : Fragment() {

    private lateinit var data: Patient
    private lateinit var tvNom: EditText
    private lateinit var tvPrenom: EditText
    private lateinit var tvAnniv: EditText
    private lateinit var tvMail: EditText
    private lateinit var tvNumTel: EditText
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
        return inflater.inflate(R.layout.fragment_mes_informations, container, false)
    }

    // Pour faire fonctionner le bouton retour
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation des vues
        tvNom = view.findViewById(R.id.editTextNom)
        tvPrenom = view.findViewById(R.id.editTextPrenom)
        tvAnniv = view.findViewById(R.id.editTextDateNaissance)
        tvMail = view.findViewById(R.id.editTextEmail)
        tvNumTel = view.findViewById(R.id.editTextTelephone)

        showData()

        val btnBackToProfile: Button = view.findViewById(R.id.btn_back_to_profile)
        btnBackToProfile.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Bouton Enregistrer
        val btnEnregistrer: Button = view.findViewById(R.id.btn_enregistrer)
        btnEnregistrer.setOnClickListener {
            //Récuperation des info du fragment
            val newPatient = data
            newPatient.prenom = tvPrenom.text.toString()
            newPatient.nom = tvNom.text.toString()
            newPatient.date_naissance = reFormatageDate(tvAnniv.text.toString())
            newPatient.mail = tvMail.text.toString()
            val tel = tvNumTel.text.toString()

            if ((tel.length == 0 || tel.length == 10) && tel.all { it.isDigit() }) {
                newPatient.numero_tel = tel
            } else {
                Toast.makeText(
                    context,
                    "Le numéro de tel n'est pas conforme.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            updateInfos(newPatient)
        }
    }

    private fun showData() {
        tvNom.setText(data.nom)
        tvPrenom.setText(data.prenom)
        tvNumTel.setText(data.numero_tel)
        tvMail.setText(data.mail)
        tvAnniv.setText("${formatageDate(data.date_naissance)}")
    }

    fun updateInfos(patient : Patient) {

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

    private fun formatageDate(date: String): String {
        val inputFormats = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )
        val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        for (format in inputFormats) {
            try {
                val parsedDate = format.parse(date)
                return outputDateFormat.format(parsedDate)
            } catch (e: Exception) {
                Log.w("RDVFragment", "Erreur de formatage de la date : $date")
            }
        }

        return "Date invalide"
    }

    private fun reFormatageDate(date: String): String { //pour remetre en base

        val inputFormats = listOf(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()))
        val outputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        for (format in inputFormats) {
            try {
                val parsedDate = format.parse(date)
                return outputDateFormat.format(parsedDate)
            } catch (e: Exception) {
                Log.w("RDVFragment", "Erreur de formatage de la date : $date")
            }
        }

        return "Date invalide"
    }

    companion object {
        @JvmStatic
        fun newInstance(patient: Patient) =
            MesInformationsFragment(patient).apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PATIENT, patient)
                }
            }
    }
}