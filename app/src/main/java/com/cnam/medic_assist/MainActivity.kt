package com.cnam.medic_assist

import android.os.Bundle
import com.cnam.medic_assist.screens.RDVFragment
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.cnam.medic_assist.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Charger le fragment RDVFragment dans l'activité
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RDVFragment.newInstance())
            .commit()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Gérer la navigation home
                    true
                }
                R.id.navigation_suivi -> {
                    // Gérer la navigation suivi
                    true
                }
                R.id.navigation_gps -> {
                    // Gérer la navigation GPS
                    true
                }
                R.id.navigation_profil -> {
                    // Gérer la navigation profil
                    true
                }
                else -> false
            }
        }
    }
}
