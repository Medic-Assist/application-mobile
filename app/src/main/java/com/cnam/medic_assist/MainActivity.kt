package com.cnam.medic_assist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonMap = findViewById<Button>(R.id.button_map)
        val buttonDirections = findViewById<Button>(R.id.button_directions)

        buttonMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        buttonDirections.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java).apply {
                action = "GET_DIRECTIONS"
            }
            startActivity(intent)
        }
    }
}