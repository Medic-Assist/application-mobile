package com.cnam.medic_assist

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.cnam.medic_assist.screens.RDVFragment
import androidx.appcompat.app.AppCompatActivity
import com.ale.rainbowsdk.RainbowSdk
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

        class RainbowApplication : Application() {

            override fun onCreate() {
                super.onCreate()
                RainbowSdk().initialize(
                    applicationContext = this,
                    applicationId = "2357e3b0775611efbc25252c8c078f84",
                    applicationSecret = "MJ1yS7Hj34SDSnfHeDxEnsoQ6py1DDalFBOM3QVSvZOEFgFtiIZOLDPVTYE704Xz"

                )
            }
        }
    }
}
