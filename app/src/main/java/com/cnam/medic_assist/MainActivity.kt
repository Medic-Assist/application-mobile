package com.cnam.medic_assist

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialiser le client de localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val buttonSearch = findViewById<Button>(R.id.button_search)
        val textResult = findViewById<TextView>(R.id.text_result)

        buttonSearch.setOnClickListener {
            val destination = findViewById<EditText>(R.id.edit_text_destination).text.toString()

            if (destination.isNotBlank()) {
                // Vérifier si la permission de localisation est accordée
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // Obtenir la localisation de l'utilisateur
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            val startCoordinates = "${it.longitude},${it.latitude}"

                            // Utiliser Geocoder pour obtenir les coordonnées de l'adresse de destination
                            val geocoder = Geocoder(this, Locale.getDefault())
                            val addressList = geocoder.getFromLocationName(destination, 1)

                            if (addressList != null) {
                                if (addressList.isNotEmpty()) {
                                    val address = addressList?.get(0)
                                    val endCoordinates = "${address?.longitude},${address?.latitude}"

                                    // Effectuer la requête API pour obtenir le trajet
                                    RetrofitClient.instance.getRoute(
                                        start = startCoordinates,
                                        end = endCoordinates
                                    ).enqueue(object : Callback<RouteResponse> {
                                        override fun onResponse(
                                            call: Call<RouteResponse>,
                                            response: Response<RouteResponse>
                                        ) {
                                            if (response.isSuccessful) {
                                                val result = response.body()
                                                val durationInMinutes = (result?.duration ?: 0.0) / 60
                                                textResult.text =
                                                    "Temps estimé : %.2f minutes\nLieu : $destination".format(
                                                        durationInMinutes
                                                    )
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
                    }
                } else {
                    // Demander la permission d'accès à la localisation
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                }
            } else {
                textResult.text = "Veuillez entrer une destination."
            }
        }
    }

    // Gérer la réponse de la demande de permission de localisation
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accordée", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
