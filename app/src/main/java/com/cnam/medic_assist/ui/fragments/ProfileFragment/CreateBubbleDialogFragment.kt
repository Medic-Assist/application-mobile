package com.cnam.medic_assist.ui.fragments.ProfileFragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.ale.infra.contact.Contact
import com.ale.infra.contact.IRainbowContact
import com.ale.infra.manager.room.CreateRoomBody
import com.ale.infra.manager.room.IRainbowRoom
import com.ale.infra.rest.listeners.RainbowError
import com.ale.infra.rest.listeners.RainbowListener
import com.ale.infra.rest.room.AddRoomParticipantsError
import com.ale.infra.rest.room.RoomRepository
import com.ale.listener.IRainbowContactsSearchListener
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.models.CentreMedical
import com.cnam.medic_assist.datas.models.Patient
import com.cnam.medic_assist.datas.models.RDVSend
import com.cnam.medic_assist.datas.models.RendezVous
import com.cnam.medic_assist.datas.network.RetrofitClient
import com.cnam.medic_assist.ui.fragments.NavFragments.BubbleFragment
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar


class CreateBubbleDialogFragment : DialogFragment() {

    private lateinit var spinnerCentreMedical: Spinner
    private lateinit var editTextBubbleName: EditText
    private lateinit var editTextParticipants: Spinner
    private lateinit var btnCreateBubble: Button
    private lateinit var btnCancel: Button
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText

    private var centresMedicaux: List<CentreMedical> = listOf()
    private var utilisateurs: List<Patient> = listOf()

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt() // 90% de la largeur de l'écran
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_create_bubble, container, false)

        spinnerCentreMedical = view.findViewById(R.id.spinnerCentreMedical)
        editTextBubbleName = view.findViewById(R.id.editTextBubbleName)
        editTextParticipants = view.findViewById(R.id.spinnerParticipants)
        btnCreateBubble = view.findViewById(R.id.btnCreateBubble)
        btnCancel = view.findViewById(R.id.btnCancel)
        editTextDate = view.findViewById(R.id.editTextDate)
        editTextTime = view.findViewById(R.id.editTextTime)

        editTextDate.setOnClickListener { showDatePickerDialog() }
        editTextTime.setOnClickListener { showTimePickerDialog() }
        btnCreateBubble.setOnClickListener { createBubble() }
        btnCancel.setOnClickListener { dismiss() }

        loadCentresMedicaux()
        loadUtilisateurs()

        return view
    }

    private fun loadCentresMedicaux() {
        val apiService = RetrofitClient.instance
        apiService.getCentresMedicaux().enqueue(object : Callback<List<CentreMedical>> {
            override fun onResponse(call: Call<List<CentreMedical>>, response: Response<List<CentreMedical>>) {
                if (response.isSuccessful) {
                    centresMedicaux = response.body() ?: listOf()
                    val centreNames = centresMedicaux.map { it.nom ?: "Centre inconnu" }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, centreNames)
                    spinnerCentreMedical.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<CentreMedical>>, t: Throwable) {
                Log.e("CreateBubbleDialog", "Erreur chargement centres médicaux: ${t.message}")
            }
        })
    }
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            // Assure que le mois a deux chiffres et convertit en format API (YYYY-MM-DD)
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            editTextDate.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }


    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val time = String.format("%02d:%02d", selectedHour, selectedMinute)
            editTextTime.setText(time)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun loadUtilisateurs() {
        RetrofitClient.instance.getAllPatients().enqueue(object : Callback<List<Patient>> {
            override fun onResponse(call: Call<List<Patient>>, response: Response<List<Patient>>) {
                if (response.isSuccessful) {
                    utilisateurs = response.body()?.map { patient ->
                        Patient(
                            iduser = patient.iduser ?: 0,
                            nom = patient.nom ?: "",
                            prenom = patient.prenom ?: "",
                            mail = patient.mail ?: "",
                            date_naissance = patient.date_naissance ?: "",
                            numero_rue_principal = patient.numero_rue_principal ?: "",
                            rue_principale = patient.rue_principale ?: "",
                            codepostal_principal = patient.codepostal_principal ?: "",
                            ville_principale = patient.ville_principale ?: "",

                        )
                    } ?: listOf()

                    val patientNames = utilisateurs.map { "${it.nom} ${it.prenom}" }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, patientNames)
                    view?.findViewById<Spinner>(R.id.spinnerParticipants)?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<Patient>>, t: Throwable) {
                Log.e("CreateBubbleDialog", "Erreur chargement utilisateurs: ${t.message}")
            }
        })
    }
    private fun createBubble() {
        val bubbleName = editTextBubbleName.text.toString()
        val selectedCentreIndex = spinnerCentreMedical.selectedItemPosition
        val centreMedical = centresMedicaux.getOrNull(selectedCentreIndex)

        val selectedPatientIndex = view?.findViewById<Spinner>(R.id.spinnerParticipants)?.selectedItemPosition ?: -1
        val selectedPatient = utilisateurs.getOrNull(selectedPatientIndex)

        if (bubbleName.isEmpty() || centreMedical == null || selectedPatient == null) {
            Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val body = CreateRoomBody.Builder()
            .name(bubbleName)
            .topic("Discussion pour ${centreMedical.nom}")
            .isVisible(true)
            .withHistory(true)
            .disableNotifications(false)
            .autoAcceptInvitation(true)
            .muteParticipantsUponEntry(false)
            .playEntryTone(true)
            .build()

        val selectedDate = editTextDate.text.toString()
        val selectedTime = editTextTime.text.toString()

        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(context, "Veuillez sélectionner une date et un horaire", Toast.LENGTH_SHORT).show()
            return
        }
        val sharedPref = requireActivity().getSharedPreferences("UserCache", Context.MODE_PRIVATE)
        val idDoctor = sharedPref.getInt("id", 0) // 0 si l'ID n'est pas trouvé



        RainbowSdk.instance().bubbles().createBubble(body, object : RainbowListener<IRainbowRoom, RoomRepository.CreateRoomError> {
            override fun onSuccess(bubble: IRainbowRoom) {
                Log.d("RainbowSDK", "Bulle créée avec succès : ${bubble.name}")
                addParticipantToBubble(bubble, selectedPatient)
                // 🔹 Création du RDV après avoir récupéré l'ID de la bulle
                val rendezVous = RDVSend(
                    intitule = bubbleName,
                    horaire = selectedTime,
                    dateRDV = selectedDate,
                    idBulleRainbow = bubble.id, // 🔥 On met l'ID de la bulle ici !
                    idUser = selectedPatient.iduser ?: 0,
                    idCentreMed = centreMedical.idCentreMed ?: 1,
                    isADRPrincipale = false,
                    idDoctor = idDoctor
                )

                Log.d("RendezVous_JSON", Gson().toJson(rendezVous)) // Vérifier que l'ID est bien envoyé
                sendRendezvousToServer(rendezVous)
                requireActivity().runOnUiThread {
                    (parentFragment as? BubbleFragment)?.listBubbles()
                    dismiss()
                }
            }

            override fun onError(error: RainbowError<RoomRepository.CreateRoomError>) {
                Log.e("RainbowSDK", "Erreur création de la bulle: ${error.errorCode}")
            }
        })
    }

    private fun addParticipantToBubble(bubble: IRainbowRoom, patient: Patient) {
        // Rechercher le contact par son nom (asynchrone)
 //       val  contactsR  = RainbowSdk.instance().contacts().rainbowContacts.toArray()
        //       Log.d("RainbowSDK", "Contacts trouvés : ${contactsR.size}")

        val contact = RainbowSdk.instance().contacts().getContactFromId ("6746e9cae6d1354b9a9e9624");

         if (contact != null ) {
             // Ajouter le participant à la bulle avec la bonne interface RainbowListener
             RainbowSdk.instance().bubbles().addParticipantToBubble(
                 bubble,
                 contact,
                 object : RainbowListener<Unit, AddRoomParticipantsError> { // Correction ici
                     override fun onSuccess(result: Unit) {
                         Log.d(
                             "RainbowSDK",
                             "Participant ajouté avec succès à la bulle : ${bubble.name}"
                         )
                     }

                     override fun onError(error: RainbowError<AddRoomParticipantsError>) { // Correction ici
                         Log.e(
                             "RainbowSDK",
                             "Erreur lors de l'ajout du participant: ${error.errorMsg}"
                         )
                     }
                 }
             )
         }


    }
    private fun sendRendezvousToServer(rendezVous: RDVSend) {
        Log.d("sendRendezvous", "Envoi RDV avec ID bulle: ${rendezVous.idBulleRainbow}")

        val apiService = RetrofitClient.instance
        apiService.addRendezvous(rendezVous).enqueue(object : Callback<RDVSend> {
            override fun onResponse(call: Call<RDVSend>, response: Response<RDVSend>) {
                if (response.isSuccessful) {
                    if (isAdded && context != null) { // 🔹 Vérifier si le fragment est attaché
                        Toast.makeText(requireContext(), "Rendez-vous créé avec succès !", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (isAdded && context != null) {
                        Toast.makeText(requireContext(), "Erreur lors de la création du RDV", Toast.LENGTH_SHORT).show()
                    }
                    Log.e("CreateBubbleDialog", "Erreur API: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<RDVSend>, t: Throwable) {
                if (isAdded && context != null) {
                    Toast.makeText(requireContext(), "Échec de la connexion au serveur", Toast.LENGTH_SHORT).show()
                }
                Log.e("CreateBubbleDialog", "Erreur réseau: ${t.message}")
            }
        })
    }



}

