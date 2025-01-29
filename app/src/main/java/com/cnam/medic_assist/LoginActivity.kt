package com.cnam.medic_assist

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ale.infra.rest.listeners.RainbowError
import com.ale.rainbowsdk.Connection
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.datas.Constants
import com.cnam.medic_assist.datas.models.RendezVous
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
        // Vérification de l'état de connexion
        if (RainbowSdk.instance().connection().state != Connection.ConnectionState.DISCONNECTED) {
            Log.d("RainbowSDK", "Connexion déjà établie ou en cours.")
            return
        }

        RainbowSdk.instance().connection().signIn(
            login = username,
            password = password,
            host = "sandbox.openrainbow.com", // Ou "openrainbow.com" pour la production
            listener = object : Connection.ISignInListener {
                override fun onSignInSucceeded() {
                    Log.d("RainbowSDK", "Connexion réussie pour l'utilisateur : $username")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }

                override fun onSignInFailed(errorCode: Connection.ErrorCode, error: RainbowError<Unit>) {
                    Log.e("RainbowSDK", "Échec de la connexion : Code erreur - ${errorCode.name}, Détails - ${error.errorMsg}")
                    showAlertDialog("Login Failed", "Error Code: ${errorCode.name} - Details: ${error.errorMsg}")
                }
            }
        )
    }

    private fun showAlertDialog(title: String, message: String) {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.show()
        }
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
