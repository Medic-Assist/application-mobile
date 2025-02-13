package com.cnam.medic_assist.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cnam.medic_assist.R

class ContactsAdapter(private var contacts: List<ContactItem>) :
    RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int = contacts.size

    fun updateContacts(newContacts: List<ContactItem>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contactName: TextView = itemView.findViewById(R.id.contact_name)
        private val contactEmail: TextView = itemView.findViewById(R.id.contact_email)
        private val contactAvatar: ImageView = itemView.findViewById(R.id.contact_avatar)

        fun bind(contact: ContactItem) {
            contactName.text = "${contact.firstName} ${contact.lastName}"
            contactEmail.text = contact.email ?: "No Email"
            if (!contact.avatarUrl.isNullOrEmpty()) {
                Glide.with(itemView.context).load(contact.avatarUrl).into(contactAvatar)
            } else {
                contactAvatar.setImageResource(R.drawable.logo)
            }
        }
    }
}

