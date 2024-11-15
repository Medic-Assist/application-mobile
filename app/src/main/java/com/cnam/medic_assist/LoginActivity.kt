package com.cnam.medic_assist

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
}
