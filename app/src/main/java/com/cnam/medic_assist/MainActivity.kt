package com.cnam.medic_assist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText

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
    }
}
