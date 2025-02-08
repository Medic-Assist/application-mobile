package com.cnam.medic_assist

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ale.infra.rest.listeners.RainbowError
import com.ale.rainbowsdk.Connection
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.datas.Constants
import com.cnam.medic_assist.datas.models.RendezVous
import com.cnam.medic_assist.datas.models.Utilisateur
import com.cnam.medic_assist.datas.models.UtilisateurRequete
import com.cnam.medic_assist.datas.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextNewUsername: EditText
    private lateinit var editTextNewPassword: EditText
    private lateinit var btnRegister: Button
    private val sandboxHost = "openrainbow.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextNewUsername = findViewById(R.id.usernameEditText)
        editTextNewPassword = findViewById(R.id.passwordEditText)
        btnRegister = findViewById(R.id.loginButton)

        btnRegister.setOnClickListener { onClickConnexion() }

        // Vérifiez la permission pour les alarmes exactes
        requestExactAlarmPermission()
    }

    private fun onClickConnexion() {
        val inputUsername = editTextNewUsername.text.toString()
        val inputPassword = editTextNewPassword.text.toString()

        if (inputUsername.isNotBlank() && inputPassword.isNotBlank()) {
            loginWithRainbow(inputUsername, inputPassword)
        } else {
            showAlertDialog("Connection Failed", "Username or password was not filled")
        }
    }

    private fun loginWithRainbow(username: String, password: String) {
        if (RainbowSdk.instance().connection().state != Connection.ConnectionState.DISCONNECTED) {
            Log.d("RainbowSDK", "Connexion déjà établie ou en cours.")
            return
        }

        RainbowSdk.instance().connection().signIn(
            login = username,
            password = password,
            host = "sandbox.openrainbow.com", // Utilisez "openrainbow.com" pour la production
            listener = object : Connection.ISignInListener {
                override fun onSignInSucceeded() {
                    val user = RainbowSdk().user().getConnectedUser()
                    Log.d("RainbowSDK", "Connexion Rainbow réussie pour l'utilisateur : $username")
                        user.firstName?.let { user.lastName?.let { it1 ->
                            sendUserDataToServer(username, it,
                                it1
                            )
                        } }
                }

                override fun onSignInFailed(errorCode: Connection.ErrorCode, error: RainbowError<Unit>) {
                    Log.e("RainbowSDK", "Échec de la connexion : Code erreur - ${errorCode.name}, Détails - ${error.errorMsg}")
                    showAlertDialog("Login Failed", "Error Code: ${errorCode.name} - Details: ${error.errorMsg}")
                }
            }
        )
    }

    private fun sendUserDataToServer(email: String, firstname: String, lastname: String) {
        val userData = UtilisateurRequete(
            email = email,
            nom = lastname,
            prenom = firstname
        )

        Log.d("RequestBody", "Envoyé : $userData")

        RetrofitClient.instance.callUser(userData).enqueue(object : Callback<Utilisateur> {
            override fun onResponse(call: Call<Utilisateur>, response: Response<Utilisateur>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    // enregistrer email en cache
                    val sharedPref = getSharedPreferences("UserCache", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        user.iduser?.let { putInt("id", it) }
                        apply()
                    }
                    Log.d("SendUserData", "Email enregistré en cache : $email")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                    Log.d("SendUserData", "Utilisateur envoyé avec succès : ${user.prenom} ${user.nom}")
                } else {
                    Log.e("SendUserData", "Erreur serveur : ${response.code()}")
                    // deconnecte l'utilisateur
                    RainbowSdk.instance().connection().signOut()
                }
            }

            override fun onFailure(call: Call<Utilisateur>, t: Throwable) {
                Log.e("SendUserData", "Échec de la requête : ${t.message}")
                RainbowSdk.instance().connection().signOut()
            }
        })
    }

    private fun fetchUserAppointments(userId: Int) {
        RetrofitClient.instance.getRendezvousByUserId(userId).enqueue(object : Callback<List<RendezVous>> {
            override fun onResponse(call: Call<List<RendezVous>>, response: Response<List<RendezVous>>) {
                if (response.isSuccessful && response.body() != null) {
                    Constants.rdvList = response.body()!!
                    Log.d("FetchAppointments", "Rendez-vous récupérés : ${Constants.rdvList.size}")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Log.e("FetchAppointments", "Erreur lors de la récupération des rendez-vous.")
                    showAlertDialog("Data Error", "Impossible de récupérer les rendez-vous.")
                }
            }

            override fun onFailure(call: Call<List<RendezVous>>, t: Throwable) {
                Log.e("FetchAppointments", "Échec de la requête : ${t.message}")
                showAlertDialog("Network Error", "Erreur de connexion au serveur.")
            }
        })
    }

    private fun showAlertDialog(title: String, message: String) {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("OK", null)
            builder.show()
        }
    }

    private fun testNotifications() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // Parcourir tous les rendez-vous de Constants.rdvList
        Constants.rdvList.forEach { rendezVous ->
            try {
                val dateTimeString = "${rendezVous.daterdv} ${rendezVous.horaire}"
                val timeInMillis = dateFormat.parse(dateTimeString)?.time ?: 0L

                // Filtrer les rendez-vous passés
                if (timeInMillis > System.currentTimeMillis()) {
                    Log.d("NotificationTest", "Planification de la notification pour : ${rendezVous.intitule} à $dateTimeString")
                    scheduleNotification(
                        appointmentId = rendezVous.idrdv ?: 0,
                        timeInMillis = timeInMillis,
                        title = rendezVous.intitule
                    )
                } else {
                    Log.d("NotificationTest", "Rendez-vous ignoré (passé) : ${rendezVous.intitule}")
                }
            } catch (e: Exception) {
                Log.e("NotificationTest", "Erreur lors de la planification pour ${rendezVous.intitule} : ${e.message}")
            }
        }
    }

    private fun convertToMillis(date: String, time: String): Long {
        return try {
            val dateTimeString = "$date $time" // Combine la date et l'heure
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            dateFormat.parse(dateTimeString)?.time ?: 0L // Retourne 0L en cas d'erreur
        } catch (e: Exception) {
            Log.e("Date Conversion", "Erreur lors de la conversion : ${e.message}")
            0L
        }
    }

    private fun scheduleNotification(appointmentId: Int, timeInMillis: Long, title: String) {
        val adjustedTimeInMillis = timeInMillis - 3600000 // Soustraire 1 heure (3600000 ms)

        val intent = Intent(this, NotificationReceiver::class.java).apply {
            putExtra("appointmentId", appointmentId)
            putExtra("notificationTitle", title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            appointmentId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            adjustedTimeInMillis,
            pendingIntent
        )

        Log.d(
            "NotificationTest",
            "Notification planifiée : [ID: $appointmentId, Titre: $title, Temps (ms): $adjustedTimeInMillis, Date/Heure : ${java.util.Date(adjustedTimeInMillis)}]"
        )
    }

    private fun requestExactAlarmPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.d("PermissionCheck", "Redirection vers les paramètres pour accorder la permission.")
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            } else {
                Log.d("PermissionCheck", "Permission SCHEDULE_EXACT_ALARM déjà accordée.")
            }
        }
    }
}
