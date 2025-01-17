package com.cnam.medic_assist

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            "ACTION_YES" -> {
                Log.d("NotificationAction", "L'utilisateur a cliqué sur Oui.")
                Toast.makeText(context, "Vous avez répondu Oui.", Toast.LENGTH_SHORT).show()
            }
            "ACTION_NO" -> {
                Log.d("NotificationAction", "L'utilisateur a cliqué sur Non.")
                Toast.makeText(context, "Vous avez répondu Non.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
