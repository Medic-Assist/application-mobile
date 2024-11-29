package com.cnam.medic_assist.ui.fragments.NavFragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cnam.medic_assist.MapActivity
import com.cnam.medic_assist.R
import com.cnam.medic_assist.datas.network.MapsRetrofitClient
import com.cnam.medic_assist.datas.network.RouteResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class GpsFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialiser les éléments de l'interface utilisateur
        val buttonMap = view.findViewById<Button>(R.id.button_map)
        val buttonDirections = view.findViewById<Button>(R.id.button_directions)
        val buttonSearch = view.findViewById<Button>(R.id.button_search)
        val buttonSearchMap = view.findViewById<Button>(R.id.button_searchMap)
        val editTextDestination = view.findViewById<EditText>(R.id.edit_text_destination)
        val textResult = view.findViewById<TextView>(R.id.text_result)

        // Initialiser le client de localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Bouton pour afficher la carte
        buttonMap.setOnClickListener {
            startActivity(Intent(requireContext(), MapActivity::class.java))
        }

        // Bouton pour obtenir les directions vers une destination
        buttonDirections.setOnClickListener {
            val intent = Intent(requireContext(), MapActivity::class.java)
            intent.action = "GET_DIRECTIONS"
            intent.putExtra("DESTINATION", "Cathédrale de Strasbourg")
            startActivity(intent)
        }

        // Bouton pour chercher un itinéraire
        buttonSearchMap.setOnClickListener {
            val destination = editTextDestination.text.toString()
            if (destination.isNotBlank()) {
                val intent = Intent(requireContext(), MapActivity::class.java)
                intent.action = "GET_DIRECTIONS"
                intent.putExtra("DESTINATION", destination)
                startActivity(intent)
            } else {
                textResult.text = "Veuillez entrer une destination."
            }
        }
        buttonSearch.setOnClickListener {
            val destination = editTextDestination.text.toString()
            if (destination.isNotBlank()) {
                searchRoute(destination, textResult)
            } else {
                textResult.text = "Veuillez entrer une destination."
            }
        }
    }


    private fun searchRoute(destination: String, textResult: TextView) {
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

                                    // Split the duration (hh:mm:ss)
                                    val parts = duration.split(":")
                                    val hours = parts[0]
                                    val minutes = parts[1]

                                    // Check if there are hours and display accordingly
                                    val displayText = if (hours == "00") {
                                        // No hours, display only minutes
                                        "Temps estimé : $minutes minutes\nLieu : $destination"
                                    } else {
                                        // Show hours and minutes
                                        "Temps estimé : $hours heures $minutes minutes\nLieu : $destination"
                                    }

                                    textResult.text = displayText
                                } else {
                                    textResult.text = "Erreur lors de la récupération du trajet."
                                }
                            }

                            override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                                textResult.text = "Erreur réseau : ${t.message}"
                            }
                        })
                    } else {
                        textResult.text = "Adresse non trouvée."
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission accordée", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Permission refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
