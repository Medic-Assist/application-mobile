package com.cnam.medic_assist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.net.Uri

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialiser le client de localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtenez la carte de manière asynchrone
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        handleIntentAction(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntentAction(intent)
    }

    private fun handleIntentAction(intent: Intent?) {
        intent?.let {
            if (it.action == "GET_DIRECTIONS") {
                // Coordonnées de la Cathédrale de Strasbourg
                val cathedralStrasbourg = LatLng(48.5817, 7.7500)

                // Vérifier la permission ACCESS_FINE_LOCATION
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                    // Obtenir la dernière position connue de l'utilisateur
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let { currentLocation ->
                            // Construire l'URL pour ouvrir Google Maps avec les directions
                            val uri = "https://www.google.com/maps/dir/?api=1" +
                                    "&origin=${currentLocation.latitude},${currentLocation.longitude}" +
                                    "&destination=${cathedralStrasbourg.latitude},${cathedralStrasbourg.longitude}"

                            // Ouvrir Google Maps avec les directions
                            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                            mapIntent.setPackage("com.google.android.apps.maps")
                            startActivity(mapIntent)
                        } ?: run {
                            Toast.makeText(this, "Unable to retrieve your location.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Demander la permission ACCESS_FINE_LOCATION si elle n'est pas accordée
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Vérifiez les autorisations de localisation
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            mMap.isMyLocationEnabled = true

            // Obtenir la dernière position connue de l'utilisateur
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    // Obtenez la latitude et la longitude
                    val currentLocation = LatLng(location.latitude, location.longitude)

                    // Ajoutez un marqueur sur la position actuelle et effectuez un zoom
                    mMap.addMarker(MarkerOptions().position(currentLocation).title("Vous êtes ici"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                }
            }
        } else {
            // Demander les autorisations si elles ne sont pas accordées
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Si la permission est accordée, activez la localisation
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                    mMap.isMyLocationEnabled = true

                    // Obtenir la dernière localisation
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            val currentLocation = LatLng(location.latitude, location.longitude)
                            mMap.addMarker(MarkerOptions().position(currentLocation).title("Vous êtes ici"))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}