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
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.ale.infra.manager.room.Room
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.datas.network.ApiService
import com.cnam.medic_assist.datas.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val appointmentId = intent.getIntExtra("appointmentId", -1)
        val idRdv = intent.getIntExtra("idRdv", -1) //
        val idBulle = intent.getStringExtra("idbulle")
        val notificationTitle = intent.getStringExtra("notificationTitle") ?: "Notification"
        val notificationType = intent.getStringExtra("notificationType") ?: "default"

        val sharedPreferences = context.getSharedPreferences("medic-assist-sauv", Context.MODE_PRIVATE)
        val refusCount = sharedPreferences.getInt("refus_$appointmentId", 0)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent.action) {
            "ACTION_YES" -> {
                Log.d("NotificationAction", "L'utilisateur a confirmé la notification : ID=$appointmentId idRdv=$idRdv")

                val status = if (notificationTitle.contains("Êtes-vous en route ?")) {
                    "Patient parti"
                } else {
                    "Patient arrivé au RDV"
                }

                updateStatusToAPI(context, idRdv, status) // ✅ Modification avec `idRdv`
                notificationManager.cancel(appointmentId) // Supprime la notification
            }

            "ACTION_NO" -> {
                Log.d("NotificationAction", "L'utilisateur a refusé la notification : ID=$appointmentId")

                val status = if (notificationTitle.contains("Êtes-vous en route ?")) {
                    "Retard du patient possible"
                } else {
                    "Retard du RDV Possible"
                }

                updateStatusToAPI(context, idRdv, status) // ✅ Modification avec `idRdv`
                notificationManager.cancel(appointmentId) // Supprime la notification

                // Relancer la notification dans 5 min si refusé
                if (status == "Retard du patient possible" || status == "Retard du RDV Possible") {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val newTime = System.currentTimeMillis() + (5 * 60 * 1000) // 5 minutes après

                    val newIntent = Intent(context, NotificationReceiver::class.java).apply {
                        putExtra("appointmentId", appointmentId)
                        putExtra("idRdv", idRdv) // ✅ Ajout de l'ID du RDV pour la relance
                        putExtra("notificationTitle", notificationTitle)
                        putExtra("notificationType", notificationType)
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        appointmentId + 1000, // ID différent pour éviter les conflits
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

    private fun updateStatusToAPI(context: Context, idRDV: Int, status: String) {
        val apiService = RetrofitClient.instance
        val requestBody = mapOf("intituleEtat" to status)

        apiService.updateStatusRDV(idRDV, requestBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("API", "Statut du RDV $idRDV mis à jour avec succès : $status")
                    // envoie dans la bulle rainbow en récuperant l'id bubble de sharedpref
                    sendRainbowMessage(context, status)

                } else {
                    Log.e("API", "Erreur mise à jour statut du RDV $idRDV : ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Erreur réseau mise à jour statut du RDV $idRDV : ${t.message}")
            }
        })
    }

    private fun sendRainbowMessage(context: Context, message: String) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val bubbleId = sharedPreferences.getString("bubble_id", null)

        if (bubbleId == null) {
            Log.e("Rainbow", "Aucune bulle trouvée dans SharedPreferences")
            Toast.makeText(context, "Erreur: Aucune bulle enregistrée.", Toast.LENGTH_SHORT).show()
            return
        }

        // Récupération de la conversation de la bulle
        val room = getBubbleById(bubbleId)
        if (room == null) {
            Log.e("Rainbow", "Impossible de récupérer la bulle Rainbow avec l'ID: $bubbleId")
            Toast.makeText(context, "Erreur: Bulle introuvable.", Toast.LENGTH_SHORT).show()
            return
        }

        val conv = RainbowSdk.instance().im().getConversationFromRoom(room)
        if (conv == null) {
            Log.e("Rainbow", "Impossible d'obtenir la conversation pour la bulle: $bubbleId")
            return
        }

        // Envoi du message
        RainbowSdk.instance().im().sendMessageToConversation(conv, message)
        Log.d("Rainbow", "Message envoyé à la bulle $bubbleId : $message")
    }

    private fun getBubbleById(bubbleId: String): Room? {
        val bubbles = RainbowSdk.instance().bubbles().getAllBubbles().copyOfDataList
        return bubbles.firstOrNull { it.id == bubbleId }
    }
}
