package com.cnam.medic_assist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonMap = findViewById<Button>(R.id.button_map)
        buttonMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        val buttonDirections = findViewById<Button>(R.id.button_directions)
        buttonDirections.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.action = "GET_DIRECTIONS"
            // Définir la destination à la cathédrale de Strasbourg
            intent.putExtra("DESTINATION", "Cathédrale de Strasbourg")
            startActivity(intent)
        }

        val buttonSearch = findViewById<Button>(R.id.button_search)
        buttonSearch.setOnClickListener {
            val destination = findViewById<EditText>(R.id.edit_text_destination).text.toString()
            if (destination.isNotBlank()) {
                val intent = Intent(this, MapActivity::class.java)
                intent.action = "GET_DIRECTIONS"
                intent.putExtra("DESTINATION", destination)
                startActivity(intent)
            }
        }

        val textResult = findViewById<TextView>(R.id.text_result)

        buttonSearch.setOnClickListener {
            val destination = findViewById<EditText>(R.id.edit_text_destination).text.toString()
            if (destination.isNotBlank()) {
                val startCoordinates = "7.788539675444039,48.812850103549344" // Coordonnées de départ (exemple)
                val endCoordinates = "7.684643371015784,48.59018005009095" // Coordonnées d'arrivée (à modifier selon le lieu)

                RetrofitClient.instance.getRoute(
                    start = startCoordinates,
                    end = endCoordinates
                ).enqueue(object : Callback<RouteResponse> {
                    override fun onResponse(call: Call<RouteResponse>, response: Response<RouteResponse>) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            val durationInMinutes = (result?.duration ?: 0.0) / 60
                            textResult.text = "Temps estimé : %.2f minutes\nLieu : $destination".format(durationInMinutes)
                        } else {
                            textResult.text = "Erreur lors de la récupération du trajet."
                        }
                    }

                    override fun onFailure(call: Call<RouteResponse>, t: Throwable) {
                        textResult.text = "Erreur réseau : ${t.message}"
                    }
                })
            } else {
                textResult.text = "Veuillez entrer une destination."
            }
        }

    }
}
