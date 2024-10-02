package com.example.poc_medicassist_notif

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "exampleChannel"
    private val notificationId = 101
    private val REQUEST_CODE_POST_NOTIFICATION = 101
    private var selectedCalendar: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Demander la permission pour les notifications sur Android 13 et plus
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATION)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Créer le canal de notification
        createNotificationChannel()

        // Bouton pour envoyer une notification immédiate
        findViewById<Button>(R.id.notifyButton).setOnClickListener {
            Log.d("MainActivity", "Notify button clicked") // Log pour le bouton
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                sendNotification()
            } else {
                Toast.makeText(this, "Permission de notification non accordée", Toast.LENGTH_SHORT).show()
            }
        }

        // Sélection de la date
        findViewById<Button>(R.id.dateButton).setOnClickListener { showDatePicker() }

        // Sélection de l'heure
        findViewById<Button>(R.id.timeButton).setOnClickListener { showTimePicker() }

        // Planifier la notification
        findViewById<Button>(R.id.scheduleButton).setOnClickListener {
            selectedCalendar?.let { calendar ->
                scheduleNotification(calendar.timeInMillis)
            } ?: run {
                Toast.makeText(this, "Veuillez choisir une date et une heure", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Example Channel"
            val descriptionText = "This is a channel for example notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("MainActivity", "Notification channel created: $CHANNEL_ID")
        } else {
            Log.d("MainActivity", "No need to create notification channel for SDK < O")
        }
    }

    private fun sendNotification() {
        Log.d("MainActivity", "sendNotification called") // Log pour sendNotification
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Notification Title")
            .setContentText("This is an example notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(this)

        // Vérifiez le canal de notification
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            Log.e("MainActivity", "Notification channel not found")
        } else {
            Log.d("MainActivity", "Notification channel is available")
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("MainActivity", "POST_NOTIFICATIONS permission not granted")
            return
        }
        notificationManager.notify(notificationId, builder.build())
        Log.d("MainActivity", "Notification sent") // Log pour notification envoyée
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker() {
        val calendar = selectedCalendar ?: Calendar.getInstance()
        TimePickerDialog(this, { _, hourOfDay, minute ->
            selectedCalendar?.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedCalendar?.set(Calendar.MINUTE, minute)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun checkAndRequestAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder(this)
                    .setTitle("Permission d'alarme requise")
                    .setMessage("Cette application a besoin de la permission d'alarme pour programmer des notifications exactes. Veuillez l'activer dans les paramètres.")
                    .setPositiveButton("OK") { _, _ ->
                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:$packageName")
                        })
                    }
                    .setNegativeButton("Annuler", null)
                    .show()
            }
        }
    }

    private fun scheduleNotification(triggerAtMillis: Long) {
        Log.d("MainActivity", "scheduleNotification called with triggerAtMillis: ${Date(triggerAtMillis)}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
                checkAndRequestAlarmPermission()
                Log.e("MainActivity", "Exact alarm permission required and not granted")
                Toast.makeText(this, "Veuillez activer la permission d'alarme dans les paramètres", Toast.LENGTH_LONG).show()
                return
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("MainActivity", "POST_NOTIFICATIONS permission not granted")
            return
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("title", "Notification programmée")
            putExtra("message", "Cette notification a été programmée pour ${Date(triggerAtMillis)}")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        Log.d("ScheduleNotification", "AlarmManager setExactAndAllowWhileIdle for ${Date(triggerAtMillis)}")

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            Log.d("ScheduleNotification", "Notification programmée pour: ${Date(triggerAtMillis)}")
            Toast.makeText(this, "Notification programmée", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Log.e("ScheduleNotification", "SecurityException: ${e.message}")
            Toast.makeText(this, "Erreur de sécurité : ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Gestion de la réponse de la demande de permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                sendNotification()
            } else {
                Toast.makeText(this, "Permission de notification refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateFormat = android.text.format.DateFormat.getLongDateFormat(applicationContext)
        val timeFormat = android.text.format.DateFormat.getTimeFormat(applicationContext)

        AlertDialog.Builder(this)
            .setTitle("Notification Programmée")
            .setMessage(
                "Titre: $title\nMessage: $message\nÀ: ${dateFormat.format(date)} ${timeFormat.format(date)}"
            )
            .setPositiveButton("OK", null)
            .show()
    }


}
