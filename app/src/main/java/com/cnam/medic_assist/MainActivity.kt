package com.cnam.medic_assist

import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.cnam.medic_assist.screens.RDVFragment
import androidx.appcompat.app.AppCompatActivity
import com.ale.rainbowsdk.RainbowSdk
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.cnam.medic_assist.R

// Importation des fragments de la navbar
import androidx.fragment.app.Fragment
import com.cnam.medic_assist.ui.fragments.HomeFragment
import com.cnam.medic_assist.ui.fragments.SuiviFragment
import com.cnam.medic_assist.ui.fragments.GpsFragment
import com.cnam.medic_assist.ui.fragments.ProfilFragment

class MainActivity : AppCompatActivity() {


    // Ajout des fragments
    private val homeFragment = HomeFragment()
    private val suiviFragment = SuiviFragment()
    private val gpsFragment = GpsFragment()
    private val profilFragment = ProfilFragment()

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
                    openFragment(homeFragment)
                    true
                }
                R.id.navigation_suivi -> {
                    // Gérer la navigation suivi
                    openFragment(suiviFragment)
                    true
                }
                R.id.navigation_gps -> {
                    // Gérer la navigation GPS
                    openFragment(gpsFragment)
                    true
                }
                R.id.navigation_profil -> {
                    // Gérer la navigation profil
                    openFragment(profilFragment)
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

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
