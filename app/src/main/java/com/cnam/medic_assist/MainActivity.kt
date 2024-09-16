package com.cnam.medic_assist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cnam.medic_assist.screens.RDVFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Charger le fragment RDVFragment dans l'activité
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RDVFragment.newInstance())
            .commit()
    }
}
