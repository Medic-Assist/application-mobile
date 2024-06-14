package com.cnam.medic_assist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.rdv_page)

        val listView: ListView = findViewById(R.id.list_view)

        // Sample data
        val data = listOf("RDV 1", "RDV 2", "RDV 3")
        // Add more items as needed

        // Create an ArrayAdapter using the string array and a default list item layout
        val adapter = object : ArrayAdapter<String>(this, R.layout.list_item, R.id.item_button, data) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val button = view.findViewById<Button>(R.id.item_button)
                button.text = getItem(position)
                return view
            }
        }

        // Set the adapter to the ListView
        listView.adapter = adapter
    }
}