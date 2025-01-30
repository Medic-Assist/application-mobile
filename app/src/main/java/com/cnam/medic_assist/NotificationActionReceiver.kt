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
                // TODO: Ajouter un comportement pour l'action "Oui"
            }
            "ACTION_NO" -> {
                Log.d("NotificationAction", "L'utilisateur a cliqué sur Non.")
                // TODO: Ajouter un comportement pour l'action "Non"
            }
        }
    }

}
