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

        val appointmentId = intent.getIntExtra("appointmentId", -1) // ID de la notification
        val idRdv = intent.getIntExtra("idRdv", -1) // ✅ ID du rendez-vous
        val notificationTitle = intent.getStringExtra("notificationTitle") ?: "Notification"
        val notificationType = intent.getStringExtra("notificationType") ?: "default"

        Log.d("NotificationReceiver", "📩 Notification reçue : ID Notif = $appointmentId, ID RDV = $idRdv, Type=$notificationType, Titre=$notificationTitle")

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "appointments_channel",
                "Rappels de Rendez-vous",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        when (notificationType) {
            "default" -> {
                val notification = NotificationCompat.Builder(context, "appointments_channel")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(notificationTitle)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(appointmentId, notification)
            }

            "action" -> {
                // ✅ Inclure l'ID du RDV dans les intents pour que `NotificationActionReceiver` puisse l'utiliser
                val yesIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                    action = "ACTION_YES"
                    putExtra("appointmentId", appointmentId) // ID de la notification
                    putExtra("idRdv", idRdv) // ✅ ID du RDV
                    putExtra("notificationTitle", notificationTitle)
                    putExtra("notificationType", notificationType)
                }
                val yesPendingIntent = PendingIntent.getBroadcast(
                    context, appointmentId, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val noIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                    action = "ACTION_NO"
                    putExtra("appointmentId", appointmentId) // ID de la notification
                    putExtra("idRdv", idRdv) // ✅ ID du RDV
                    putExtra("notificationTitle", notificationTitle)
                    putExtra("notificationType", notificationType)
                }
                val noPendingIntent = PendingIntent.getBroadcast(
                    context, appointmentId + 1, noIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(context, "appointments_channel")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentTitle(notificationTitle)
                    .addAction(android.R.drawable.ic_menu_agenda, "Oui", yesPendingIntent)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Non", noPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()

                notificationManager.notify(appointmentId, notification)
            }
        }
    }
}
