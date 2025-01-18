package com.cnam.medic_assist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val appointmentId = intent?.getIntExtra("appointmentId", -1) ?: return
        val notificationTitle = intent.getStringExtra("notificationTitle") ?: "Rendez-vous"

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "appointments_channel",
                "Rappels de Rendez-vous",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context!!, "appointments_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(notificationTitle)
            .setContentText("Vous avez un rendez-vous prévu.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(appointmentId, notification)
    }
}
