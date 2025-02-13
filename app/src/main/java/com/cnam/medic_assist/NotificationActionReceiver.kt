package com.cnam.medic_assist

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.util.Date

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val appointmentId = intent.getIntExtra("appointmentId", -1)
        val notificationTitle = intent.getStringExtra("notificationTitle") ?: "Notification"
        val notificationType = intent.getStringExtra("notificationType") ?: "default"

        val sharedPreferences = context.getSharedPreferences("medic-assist-sauv", Context.MODE_PRIVATE)
        val refusCount = sharedPreferences.getInt("refus_$appointmentId", 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent.action) {
            "ACTION_YES" -> {
                Log.d("NotificationAction", "L'utilisateur a confirmé la notification : ID=$appointmentId")
                sharedPreferences.edit().remove("refus_$appointmentId").apply()
                notificationManager.cancel(appointmentId) // Supprime la notification
            }

            "ACTION_NO" -> {
                Log.d("NotificationAction", "L'utilisateur a cliqué sur Non. Tentative $refusCount/3")
                notificationManager.cancel(appointmentId) // Supprime la notification

                if (refusCount >= 3) {
                    Log.d("NotificationAction", "Nombre maximal de rappels atteint pour ID=$appointmentId. Arrêt des notifications.")
                    return
                }

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val newTime = System.currentTimeMillis() + (5 * 60 * 1000) // Ajout de 5 minutes

                val newIntent = Intent(context, NotificationReceiver::class.java).apply {
                    putExtra("appointmentId", appointmentId)
                    putExtra("notificationTitle", notificationTitle)
                    putExtra("notificationType", notificationType)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    appointmentId,
                    newIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!alarmManager.canScheduleExactAlarms()) {
                            Log.w("AlarmPermission", "Permission SCHEDULE_EXACT_ALARM non accordée, redirection vers les paramètres.")
                            val settingsIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            settingsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(settingsIntent)
                            return
                        }
                    }

                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        newTime,
                        pendingIntent
                    )

                    sharedPreferences.edit().putInt("refus_$appointmentId", refusCount + 1).apply()

                    Log.d("NotificationAction", "Nouvelle notification prévue à : ${Date(newTime)}. Nombre de rappels : ${refusCount + 1}/3")
                } catch (e: SecurityException) {
                    Log.e("AlarmPermission", "Erreur : ${e.message}")
                }
            }
        }
    }
}
