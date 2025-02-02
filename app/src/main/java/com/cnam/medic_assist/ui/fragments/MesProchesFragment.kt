// MesProchesFragment.kt
package com.cnam.medic_assist.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cnam.medic_assist.R
import com.cnam.medic_assist.utils.ContactManager
import com.cnam.medic_assist.utils.ContactsAdapter

class MesProchesFragment : Fragment() {

    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var inputEmail: EditText
    private val contactManager = ContactManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mes_proches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contactsRecyclerView = view.findViewById(R.id.contacts_recycler_view)
        contactsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        contactsAdapter = ContactsAdapter(emptyList())
        contactsRecyclerView.adapter = contactsAdapter
        inputEmail = view.findViewById(R.id.input_email)

        val btnBackToProfile = view.findViewById<View>(R.id.btn_back_to_profile)
        btnBackToProfile.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val btnModifier = view.findViewById<View>(R.id.btn_modifier)
        val btnEnregistrer = view.findViewById<View>(R.id.btn_enregistrer)

        btnModifier.setOnClickListener {
            Toast.makeText(requireContext(), "Modification en cours", Toast.LENGTH_SHORT).show()
        }

        btnEnregistrer.setOnClickListener {
            val email = inputEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "Veuillez entrer une adresse e-mail", Toast.LENGTH_SHORT).show()
            } else {
                contactManager.inviteUserByEmail(email, requireContext())
            }
        }

        loadContacts()
    }

    private fun loadContacts() {
        contactManager.loadContacts { contacts ->
            contactsAdapter.updateContacts(contacts)
        }
    }
}
