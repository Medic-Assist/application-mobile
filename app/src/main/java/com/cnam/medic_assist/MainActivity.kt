package com.cnam.medic_assist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.screens.RDVFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.cnam.medic_assist.R
import androidx.fragment.app.Fragment
import com.cnam.medic_assist.ui.fragments.BubbleFragment
import com.cnam.medic_assist.ui.fragments.HomeFragment
import com.cnam.medic_assist.ui.fragments.SuiviFragment
import com.cnam.medic_assist.ui.fragments.GpsFragment
import com.cnam.medic_assist.ui.fragments.ProfilFragment

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val suiviFragment = SuiviFragment()
    private val gpsFragment = GpsFragment()
    private val profilFragment = ProfilFragment()
    private val bubbleFragment = BubbleFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RDVFragment.newInstance())
            .commit()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    openFragment(homeFragment)
                    true
                }
                R.id.navigation_suivi -> {
                    openFragment(suiviFragment)
                    true
                }
                R.id.navigation_gps -> {
                    openFragment(gpsFragment)
                    true
                }
                R.id.navigation_profil -> {
                    openFragment(profilFragment)
                    true
                }
                R.id.navigation_bubble -> {
                    openFragment(bubbleFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}