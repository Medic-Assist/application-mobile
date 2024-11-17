package com.cnam.medic_assist

import android.os.Bundle
import com.cnam.medic_assist.ui.fragments.NavFragments.RDVFragment
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import com.cnam.medic_assist.ui.fragments.NavFragments.BubbleFragment
import com.cnam.medic_assist.ui.fragments.NavFragments.HomeFragment
import com.cnam.medic_assist.ui.fragments.NavFragments.GpsFragment
import com.cnam.medic_assist.ui.fragments.NavFragments.ProfilFragment

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val rdvFragment = RDVFragment()
    private val gpsFragment = GpsFragment()
    private val profilFragment = ProfilFragment()
    private val bubbleFragment = BubbleFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment.newInstance("test", "test"))
            .commit()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    openFragment(homeFragment)
                    true
                }
                R.id.navigation_suivi -> {
                    openFragment(rdvFragment)
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