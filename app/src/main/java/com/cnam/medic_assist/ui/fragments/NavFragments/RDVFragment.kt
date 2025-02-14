package com.cnam.medic_assist.ui.fragments.NavFragments

import android.Manifest
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cnam.medic_assist.NotificationReceiver
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.models.RendezVous
import com.cnam.medic_assist.datas.network.RetrofitClient
import com.cnam.medic_assist.datas.Constants
import com.cnam.medic_assist.datas.network.MapsRetrofitClient
import com.cnam.medic_assist.datas.network.RouteResponse
import com.cnam.medic_assist.datas.models.EtatRdv
import com.cnam.medic_assist.ui.fragments.ConversationFragment
import com.cnam.medic_assist.utils.CalendarHelper
import com.cnam.medic_assist.utils.ICalendarHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RDVFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private var rdvList: List<RendezVous> = listOf()
    private lateinit var calendarHelper: ICalendarHelper
    private var etatRdv : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendarHelper = CalendarHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.rdv_page, container, false)

        listView = view.findViewById(R.id.list_view)
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            mutableListOf()
        )
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val rdv = rdvList[position]
            //showRdvDetailsDialog(rdv)
            loadStatusRdv(rdv)
        }

        val searchView: SearchView = view.findViewById(R.id.searchView)
        setupSearchView(searchView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (rdvList.isEmpty()) {
            fetchData()
        } else {
            reloadData()
        }

    }

    private fun fetchData() {
        // recuperer l'id en cache
        val sharedPref = requireContext().getSharedPreferences("UserCache", Context.MODE_PRIVATE)
        val id = sharedPref.getInt("id", 1) // null est la valeur par défaut si aucune valeur n'est trouvée
        if (id != null) {
            Log.d("UserCache", "Email récupéré depuis le cache : $id")
        } else {
            Log.d("UserCache", "Aucune valeur trouvée dans le cache.")
        }
        RetrofitClient.instance.getRendezvousByUserId(id).enqueue(object : Callback<List<RendezVous>> {
            override fun onResponse(call: Call<List<RendezVous>>, response: Response<List<RendezVous>>) {
                if (isAdded) {
                    if (response.isSuccessful && response.body() != null) {
                        rdvList = response.body()!!
                        reloadData()
                        Toast.makeText(requireContext(), "Nombre de rendez-vous : ${rdvList.size}", Toast.LENGTH_SHORT).show()
                    } else {
                        loadDefaultRdvData()
                    }
                }
            }

            override fun onFailure(call: Call<List<RendezVous>>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Erreur réseau : ${t.message}. Chargement des données par défaut.", Toast.LENGTH_SHORT).show()
                }
                loadDefaultRdvData()
            }
        })
    }

    private fun reloadData() {
        if (!isAdded) return
        val rdvStrings = rdvList.map { "${it.intitule} - ${formatageDate(it.daterdv)}" }
        adapter.clear()
        adapter.addAll(rdvStrings)
        adapter.notifyDataSetChanged()
    }

    private fun setupSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (::adapter.isInitialized) {
                    adapter.filter.filter(newText)
                }
                return false
            }
        })
    }

    private fun showRdvDetailsDialog(rdv: RendezVous) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_rdv_details)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val tvIntitule = dialog.findViewById<TextView>(R.id.dialog_intitule)
        val tvDate = dialog.findViewById<TextView>(R.id.dialog_date)
        val tvHeure = dialog.findViewById<TextView>(R.id.dialog_heure)
        val tvTitreAdresse = dialog.findViewById<TextView>(R.id.titre_adresse)
        val tvAdresse = dialog.findViewById<TextView>(R.id.dialog_adresse)
        val tvTempsAdress = dialog.findViewById<TextView>(R.id.dialog_adresse_Temps)
        val tvTitreEtatRdv = dialog.findViewById<TextView>(R.id.titre_etatRdv)
        val tvEtatRdv = dialog.findViewById<TextView>(R.id.etatRdv)
        val closeButton = dialog.findViewById<ImageView>(R.id.close_button)
        val addToCalendarButton = dialog.findViewById<Button>(R.id.add_to_calendar_button)
        val btnOpenConversation = dialog.findViewById<Button>(R.id.btn_open_conversation)
        tvIntitule.text = rdv.intitule
        tvDate.text = "Date : ${formatageDate(rdv.daterdv)}"
        tvHeure.text = "Horaire : ${formatageTime(rdv.horaire)}"
        tvAdresse.text = "${rdv.nom} \n${rdv.numero_rue} ${rdv.rue}\n${rdv.codepostal} ${rdv.ville}"

        searchRoute(tvAdresse.text.toString(), tvTempsAdress, rdv)

        if(tvAdresse.text == ""){
            tvTitreAdresse.text = "";
        }else{
            tvTitreAdresse.text = "Centre Médical :";
        }

        tvEtatRdv.text = etatRdv

        if(tvEtatRdv.text == ""){
            tvTitreEtatRdv.text = "";
        }else{
            tvTitreEtatRdv.text = "Etat du RDV :";
        }


        closeButton.setOnClickListener { dialog.dismiss() }

        addToCalendarButton.setOnClickListener {
            calendarHelper.addEventToCalendar(rdv)
            Toast.makeText(requireContext(), "Rendez-vous ajouté au calendrier.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnOpenConversation.setOnClickListener {
            dialog.dismiss()
            if (!rdv.idbullerainbow.isEmpty()) {
                openBubbleChat(rdv.idbullerainbow)
            } else {
                Toast.makeText(requireContext(), "Aucun Bubble ID associé au RDV", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }
    private fun openBubbleChat(bubbleId: String) {
        val conversationFragment = ConversationFragment.newInstance(bubbleId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, conversationFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun searchRoute(destination: String, textResult: TextView, rdv: RendezVous) {
        Log.d("searchRoute", "Recherche de l'itinéraire pour la destination : $destination")
        val sharedPreferences = requireContext().getSharedPreferences("medic-assist-sauv", Context.MODE_PRIVATE)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val startCoordinates = "${it.longitude},${it.latitude}"
                    Log.d("searchRoute", "Coordonnées de départ : $startCoordinates")

                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addressList = geocoder.getFromLocationName(destination, 1)

                    if (addressList != null && addressList.isNotEmpty()) {
                        val address = addressList[0]
                        val endCoordinates = "${address.longitude},${address.latitude}"
                        Log.d("searchRoute", "Coordonnées de destination : $endCoordinates")

                        MapsRetrofitClient.instance.getRoute(
                            start = startCoordinates,
                            end = endCoordinates
                        ).enqueue(object : Callback<RouteResponse> {
                            override fun onResponse(
                                call: Call<RouteResponse>,
                                response: Response<RouteResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val result = response.body()
                                    val duration = result?.duration ?: "00:00:00"
                                    Log.d("searchRoute", "Durée estimée du trajet : $duration")

                                    val parts = duration.split(":")
                                    val hours = parts[0]
                                    val minutes = parts[1]

                                    val displayText = if (hours == "00") {
                                        "Temps estimé : $minutes minutes"
                                    } else {
                                        "Temps estimé : $hours heures $minutes minutes"
                                    }

                                    textResult.text = displayText
                                    saveRdvToPreferences(rdv, displayText)

                                    // Enregistrer les résultats dans SharedPreferences
                                    with(sharedPreferences.edit()) {
                                        putString("tempsdetrajet", displayText)
                                        apply()
                                    }

                                } else {
                                    val errorMessage = "Erreur lors de la récupération du trajet."
                                    textResult.text = errorMessage
                                    with(sharedPreferences.edit()) {
                                        putString("tempsdetrajet", errorMessage)
                                        apply()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                                val errorMessage = "Erreur réseau : ${t.message}"
                                textResult.text = errorMessage
                                with(sharedPreferences.edit()) {
                                    putString("tempsdetrajet", errorMessage)
                                    apply()
                                }
                                Log.d("DEBUGNICO", "Erreur réseau pour ${rdv.intitule} (ID: ${rdv.idrdv}): $errorMessage")
                            }
                        })
                    } else {
                        val errorMessage = "Adresse non trouvée."
                        textResult.text = errorMessage
                        with(sharedPreferences.edit()) {
                            putString("tempsdetrajet", errorMessage)
                            apply()
                        }
                        Log.d("DEBUGNICO", "Adresse non trouvée pour ${rdv.intitule} (ID: ${rdv.idrdv}): $errorMessage")
                    }
                }
            }
        } else {
            Log.w("searchRoute", "Permission de localisation non accordée.")
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun saveRdvToPreferences(rdv: RendezVous, travelTime: String) {
        if (rdv.daterdv.isNotEmpty() && rdv.horaire.isNotEmpty() && rdv.idCentreMedical != null && travelTime.isNotEmpty()) {
            val sharedPreferences = requireContext().getSharedPreferences("medic-assist-sauv", Context.MODE_PRIVATE)
            val key = rdv.idrdv.toString()
            val newData = "${rdv.idrdv}|${rdv.idCentreMedical}|$travelTime|${rdv.nom}|${rdv.intitule}|${rdv.daterdv}|${rdv.horaire}"

            // 🔹 1. SUPPRIMER toutes les anciennes données avant d'écrire les nouvelles
            sharedPreferences.edit().clear().apply()

            // 🔹 2. Vérifier si l'entrée est déjà la même pour éviter une écriture inutile
            val existingData = sharedPreferences.getString(key, null)
            if (existingData != null && existingData == newData) {
                Log.d("saveRdvToPreferences", "✅ Données identiques déjà enregistrées pour le RDV ID: $key. Aucune mise à jour nécessaire.")
            } else {
                with(sharedPreferences.edit()) {
                    putString(key, newData)
                    apply()
                }
                Log.d("saveRdvToPreferences", "🔄 Mise à jour des données pour le RDV ID: $key : $newData")
                scheduleNotificationsFromSavedData()
            }
        } else {
            Log.w("saveRdvToPreferences", "⚠️ Données incomplètes pour le RDV ID: ${rdv.idrdv}")
        }
    }


    private fun scheduleNotificationsFromSavedData() {
        val sharedPreferences = requireContext().getSharedPreferences("medic-assist-sauv", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        Log.d("scheduleNotifications", "Démarrage de la planification des notifications.")

        allEntries.forEach { (key, value) ->
            val data = (value as String).split("|")
            if (data.size == 7) {
                try {
                    val idRdv = data[0].toInt()
                    val travelTime = data[2]
                    val rdvName = data[4]
                    val rdvDate = data[5]
                    val rdvTime = data[6]

                    // 🔹 1. Convertir la date du RDV en millisecondes
                    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")

                    val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = outputDateFormat.format(inputDateFormat.parse(rdvDate)!!)

                    val dateTimeString = "$formattedDate $rdvTime"
                    Log.d("scheduleNotifications", "📅 Date convertie : $dateTimeString")

                    val finalDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val rdvMillis = finalDateFormat.parse(dateTimeString)?.time ?: return@forEach

                    // 🔹 2. Vérifier si le rendez-vous est déjà passé
                    val currentTimeMillis = System.currentTimeMillis()
                    if (rdvMillis < currentTimeMillis) {
                        Log.w("scheduleNotifications", "❌ RDV déjà passé. Pas de notification pour ID: $idRdv")
                        return@forEach
                    }

                    // 🔹 3. Supprimer les notifications existantes AVANT de les recréer
                    cancelScheduledNotification(idRdv * 10)
                    cancelScheduledNotification(idRdv * 10 + 1)
                    cancelScheduledNotification(idRdv * 10 + 2)

                    // 🔹 4. Extraire le temps de trajet en minutes
                    val travelMinutes = travelTime.filter { it.isDigit() }.toIntOrNull() ?: 0
                    val travelMillis = travelMinutes * 60 * 1000

                    // 🔹 5. Calcul des horaires des notifications
                    val reminderTime = rdvMillis - travelMillis - (60 * 60 * 1000)
                    val departureTime = rdvMillis - travelMillis
                    val rdvTimeMillis = rdvMillis

                    Log.d("scheduleNotifications", "📌 Planification des notifications pour ID RDV: $idRdv")
                    Log.d("scheduleNotifications", "⏳ Rappel prévu à : ${Date(reminderTime)}")
                    Log.d("scheduleNotifications", "🚗 Départ prévu à : ${Date(departureTime)}")
                    Log.d("scheduleNotifications", "🏥 RDV prévu à : ${Date(rdvTimeMillis)}")

                    // 🔹 6. Replanifier les notifications
                    scheduleNotification(
                        idRdv * 10,
                        idRdv,
                        reminderTime,
                        "Rappel : Rendez-vous ($rdvName)\nDate : $rdvDate\nHeure : $rdvTime\nTemps trajet : $travelTime"
                    )

                    scheduleNotificationWithAction(
                        idRdv * 10 + 1,
                        idRdv,
                        departureTime,
                        "Êtes-vous en route ?\nRendez-vous : $rdvName\nDate : $rdvDate\nHeure : $rdvTime\nTemps trajet : $travelTime"
                    )

                    scheduleNotificationWithAction(
                        idRdv * 10 + 2,
                        idRdv,
                        rdvTimeMillis,
                        "Êtes-vous arrivé ?\nRendez-vous : $rdvName\nDate : $rdvDate\nHeure : $rdvTime"
                    )

                } catch (e: Exception) {
                    Log.e("scheduleNotifications", "❌ Erreur lors de la planification pour la clé $key : ${e.message}")
                }
            } else {
                Log.w("scheduleNotifications", "⚠️ Données mal formatées pour la clé $key : $value")
            }
        }
    }


    private fun cancelScheduledNotification(appointmentId: Int) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            appointmentId,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Log.d("scheduleNotifications", "🚫 Annulation de la notification ID: $appointmentId")
    }




    private fun scheduleNotification(appointmentId: Int, idRdv: Int, timeInMillis: Long, title: String) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w("AlarmPermission", "Permission SCHEDULE_EXACT_ALARM non accordée.")
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                return
            }
        }

        val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
            putExtra("appointmentId", appointmentId)
            putExtra("idRdv", idRdv) // 🔹 Ajout de l'ID du rendez-vous
            putExtra("notificationTitle", title)
            putExtra("notificationType", "default")
        }


        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            appointmentId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            Log.d("NotificationDebug", "Planification Notification simple : ID=$appointmentId, Heure=${Date(timeInMillis)}")
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Log.e("AlarmPermission", "Erreur : ${e.message}")
        }
    }

    private fun scheduleNotificationWithAction(appointmentId: Int, idRdv: Int, timeInMillis: Long, title: String) {
        val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
            putExtra("appointmentId", appointmentId)
            putExtra("idRdv", idRdv) // 🔹 Ajout de l'ID du rendez-vous
            putExtra("notificationTitle", title)
            putExtra("notificationType", "action")
        }



        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            appointmentId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            Log.d("Notification", "Notification avec actions planifiée : ID=$appointmentId, Titre=$title, Heure=${Date(timeInMillis)}")
        } catch (e: SecurityException) {
            Log.e("AlarmPermission", "Erreur : ${e.message}")
        }
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


    private fun formatageTime(time: String): String {
        return try {
            val horaireFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            outputTimeFormat.format(horaireFormat.parse(time))
        } catch (e: Exception) {
            Log.w("RDVFragment", "Erreur de formatage de l'heure : $time")
            "Heure invalide"
        }
    }

    private fun loadDefaultRdvData() {
        if (Constants.rdvList.isNotEmpty()) {
            rdvList = Constants.rdvList
            reloadData()
            if (isAdded) {
                Toast.makeText(requireContext(), "Données par défaut chargées", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (isAdded) {
                Toast.makeText(requireContext(), "Aucune donnée par défaut disponible.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadStatusRdv(rdv : RendezVous) {

        RetrofitClient.instance.getStatusRDV(rdv.idrdv!!).enqueue(object :
            Callback<EtatRdv> {
            override fun onResponse(call: Call<EtatRdv>, response: Response<EtatRdv>) {
                if (isAdded) {
                    if (response.isSuccessful) {
                        etatRdv = response.body()!!.intitule
                        showRdvDetailsDialog(rdv)
                        Toast.makeText(requireContext(), "Chargement de l'etat reussi.", Toast.LENGTH_SHORT).show()
                    } else {
                        etatRdv = ""
                        Toast.makeText(requireContext(), "Aucun etat enregistré pour ce RDV.", Toast.LENGTH_SHORT).show()

                        showRdvDetailsDialog(rdv)
                    }
                }
            }

            override fun onFailure(call: Call<EtatRdv>, t: Throwable) {
                if (isAdded) {
                    showRdvDetailsDialog(rdv)
                    Toast.makeText(requireContext(), "Erreur réseau : ${t.message}. Aucun etat chargé..", Toast.LENGTH_SHORT).show()
                }

            }
        })
    }

    companion object {
        fun newInstance(): RDVFragment {
            return RDVFragment()
        }
    }
}