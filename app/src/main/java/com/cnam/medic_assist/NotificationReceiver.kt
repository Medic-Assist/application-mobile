package com.cnam.medic_assist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            Log.e("NotificationReceiver", "Contexte ou intent null reçu.")
            return
        }

        val appointmentId = intent.getIntExtra("appointmentId", -1)
        val notificationTitle = intent.getStringExtra("notificationTitle") ?: "Notification"
        val notificationType = intent.getStringExtra("notificationType") ?: "default"

        Log.d("NotificationReceiver", "Notification reçue : ID=$appointmentId, Type=$notificationType, Titre=$notificationTitle")

        // Initialiser le NotificationManager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Créer un NotificationChannel si nécessaire
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "appointments_channel",
                "Rappels de Rendez-vous",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal pour les rappels de rendez-vous."
            }
            notificationManager.createNotificationChannel(channel)
            Log.d("NotificationReceiver", "NotificationChannel créé.")
        }

        when (notificationType) {
            "default" -> {
                Log.d("NotificationReceiver", "Création d'une notification simple.")
                val notification = NotificationCompat.Builder(context, "appointments_channel")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(notificationTitle)
                    .setContentText("Vous avez un rendez-vous prévu.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true) // Annule la notification lorsqu'elle est cliquée
                    .build()

                notificationManager.notify(appointmentId, notification)
                Log.d("NotificationReceiver", "Notification simple envoyée : ID=$appointmentId.")
            }

            "action" -> {
                Log.d("NotificationReceiver", "Création d'une notification avec actions (Oui/Non).")

                // Action "Oui"
                val yesIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                    action = "ACTION_YES"
                    putExtra("appointmentId", appointmentId)
                }
                val yesPendingIntent = PendingIntent.getBroadcast(
                    context,
                    appointmentId,
                    yesIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Action "Non"
                val noIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                    action = "ACTION_NO"
                    putExtra("appointmentId", appointmentId)
                }
                val noPendingIntent = PendingIntent.getBroadcast(
                    context,
                    appointmentId + 1, // ID différent pour éviter des conflits
                    noIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, "appointments_channel")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(notificationTitle)
                    .setContentText("Êtes-vous en route pour votre rendez-vous ?")
                    .addAction(
                        android.R.drawable.ic_menu_agenda,
                        "Oui",
                        yesPendingIntent
                    ) // Bouton "Oui"
                    .addAction(
                        android.R.drawable.ic_menu_close_clear_cancel,
                        "Non",
                        noPendingIntent
                    ) // Bouton "Non"
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true) // Annule la notification lorsqu'elle est cliquée
                    .build()

                notificationManager.notify(appointmentId, notification)
                Log.d("NotificationReceiver", "Notification avec actions envoyée : ID=$appointmentId.")
            }

            else -> {
                Log.w("NotificationReceiver", "Type de notification inconnu : $notificationType")
            }
        }
    }
}
