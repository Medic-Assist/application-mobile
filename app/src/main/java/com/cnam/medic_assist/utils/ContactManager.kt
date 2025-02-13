package com.cnam.medic_assist.utils

import android.content.Context
import android.widget.Toast
import com.ale.rainbowsdk.RainbowSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactManager {

    fun loadContacts(callback: (List<ContactItem>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val contacts = RainbowSdk.instance().contacts().rainbowContacts
            val contactList = contacts.copyOfDataList.map { contact ->
                ContactItem(
                    firstName = contact.firstName ?: "Unknown First Name",
                    lastName = contact.lastName ?: "Unknown Last Name",
                    email = contact.getFirstEmailAddress() ?: "No Email",
                    avatarUrl = contact.getAvatarUrl()
                )
            }
            withContext(Dispatchers.Main) {
                callback(contactList)
            }
        }
    }

    fun inviteUserByEmail(email: String, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RainbowSdk.instance().invitations().inviteUserByEmail(email)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Invitation envoyée à $email", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur d'invitation: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

// Data class to hold contact information
data class ContactItem(val firstName: String, val lastName: String, val email: String?, val avatarUrl: String?)
