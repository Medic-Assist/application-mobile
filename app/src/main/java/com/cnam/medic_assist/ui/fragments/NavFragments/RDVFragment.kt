package com.cnam.medic_assist.ui.fragments.NavFragments

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.models.RendezVous
import com.cnam.medic_assist.datas.network.RetrofitClient
import com.cnam.medic_assist.datas.Constants
import com.cnam.medic_assist.datas.network.MapsRetrofitClient
import com.cnam.medic_assist.datas.network.RouteResponse
import com.cnam.medic_assist.datas.models.EtatRdv
import com.cnam.medic_assist.utils.CalendarHelper
import com.cnam.medic_assist.utils.ICalendarHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context


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

        // Initialiser la ListView et son adaptateur
        listView = view.findViewById(R.id.list_view)
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            mutableListOf()
        )
        listView.adapter = adapter

        // Gérer les clics pour afficher les détails du rendez-vous
        listView.setOnItemClickListener { _, _, position, _ ->
            val rdv = rdvList[position]
            //showRdvDetailsDialog(rdv)
            loadStatusRdv(rdv)
        }

        // Configurer la SearchView
        val searchView: SearchView = view.findViewById(R.id.searchView)
        setupSearchView(searchView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Charger les données au démarrage
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
        if (!isAdded) return // Ne pas mettre à jour si le fragment n'est pas attaché
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

        tvIntitule.text = rdv.intitule
        tvDate.text = "Date : ${formatageDate(rdv.daterdv)}"
        tvHeure.text = "Horaire : ${formatageTime(rdv.horaire)}"
        tvAdresse.text = "${rdv.nom} \n${rdv.numero_rue} ${rdv.rue}\n${rdv.codepostal} ${rdv.ville}"

        searchRoute(tvAdresse.text.toString(),tvTempsAdress,rdv)

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

        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun searchRoute(destination: String, textResult: TextView, rdv: RendezVous) {
        // Obtenir SharedPreferences directement depuis le contexte du fragment
        val sharedPreferences = requireContext().getSharedPreferences("medic-assist-sauv", Context.MODE_PRIVATE)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val startCoordinates = "${it.longitude},${it.latitude}"
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addressList = geocoder.getFromLocationName(destination, 1)

                    if (addressList != null && addressList.isNotEmpty()) {
                        val address = addressList[0]
                        val endCoordinates = "${address.longitude},${address.latitude}"

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

                                    val parts = duration.split(":")
                                    val hours = parts[0]
                                    val minutes = parts[1]

                                    val displayText = if (hours == "00") {
                                        "Temps estimé : $minutes minutes"
                                    } else {
                                        "Temps estimé : $hours heures $minutes minutes"
                                    }

                                    textResult.text = displayText

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
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
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
