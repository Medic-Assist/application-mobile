package com.cnam.medic_assist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ale.infra.contact.Contact
import com.ale.rainbowsdk.Connection
import com.ale.rainbowsdk.RainbowSdk
import com.cnam.medic_assist.datas.models.RoleUser
import com.cnam.medic_assist.datas.models.UserRainbow
import com.cnam.medic_assist.datas.models.Utilisateur
import com.cnam.medic_assist.datas.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextNewUsername: EditText
    private lateinit var editTextNewPassword: EditText
    private lateinit var btnRegister: Button
    private val sandboxHost = "sandbox.openrainbow.com"

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
        if (RainbowSdk.instance().connection().state != Connection.ConnectionState.DISCONNECTED) {
            Log.d("RainbowSDK", "Connexion déjà établie ou en cours.")
            return
        }

        RainbowSdk.instance().connection().signIn(
            username,
            password,
            sandboxHost, // Use "openrainbow.com" for production
            object : Connection.IConnectionListener {
                override fun onSuccess() {
                    Log.d("RainbowSDK", "Connexion réussie pour l'utilisateur : $username")

                    val myUser = RainbowSdk.instance().myProfile.connectedUser
                    val rainbowUserId = myUser.id
                    val email = myUser.loginEmail
                    val firstName = myUser.firstName
                    val lastName = myUser.lastName

                    val apiService = RetrofitClient.instance

                    val call = apiService.getUserByEmail(email)
                    call.enqueue(object : Callback<Utilisateur> {
                        override fun onResponse(
                            call: Call<Utilisateur>,
                            response: Response<Utilisateur>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                val utilisateur = response.body()
                                val idUser = utilisateur?.idUser
                                if (idUser != null) {
                                    val userRainbow = UserRainbow(
                                        idRainbow = rainbowUserId,
                                        idUser = idUser
                                    )
                                    val callSaveUserRainbow = apiService.addUserRainbow(userRainbow)
                                    callSaveUserRainbow.enqueue(object : Callback<Void> {
                                        override fun onResponse(
                                            call: Call<Void>,
                                            response: Response<Void>
                                        ) {
                                            if (response.isSuccessful) {
                                                Log.d(
                                                    "RainbowSDK",
                                                    "Mapping UserRainbow enregistré avec succès"
                                                )
                                            } else {
                                                Log.e(
                                                    "RainbowSDK",
                                                    "Échec de l'enregistrement du mapping UserRainbow"
                                                )
                                            }
                                        }

                                        override fun onFailure(call: Call<Void>, t: Throwable) {
                                            Log.e(
                                                "RainbowSDK",
                                                "Erreur lors de l'enregistrement du mapping UserRainbow: ${t.message}"
                                            )
                                        }
                                    })
                                }
                            } else {
                                Log.e(
                                    "RainbowSDK",
                                    "Utilisateur non trouvé, création en cours"
                                )
                                // Create a new user
                                val newUser = Utilisateur(
                                    prenom = firstName,
                                    nom = lastName,
                                    role = RoleUser.Patient,
                                    email = email
                                )
                                val callAddUser = apiService.addUser(newUser)
                                callAddUser.enqueue(object : Callback<Utilisateur> {
                                    override fun onResponse(
                                        call: Call<Utilisateur>,
                                        response: Response<Utilisateur>
                                    ) {
                                        if (response.isSuccessful && response.body() != null) {
                                            val createdUser = response.body()
                                            val idUser = createdUser?.idUser
                                            if (idUser != null) {
                                                val userRainbow = UserRainbow(
                                                    idRainbow = rainbowUserId,
                                                    idUser = idUser
                                                )
                                                val callSaveUserRainbow =
                                                    apiService.addUserRainbow(userRainbow)
                                                callSaveUserRainbow.enqueue(object :
                                                    Callback<Void> {
                                                    override fun onResponse(
                                                        call: Call<Void>,
                                                        response: Response<Void>
                                                    ) {
                                                        if (response.isSuccessful) {
                                                            Log.d(
                                                                "RainbowSDK",
                                                                "Mapping UserRainbow enregistré avec succès"
                                                            )
                                                        } else {
                                                            Log.e(
                                                                "RainbowSDK",
                                                                "Échec de l'enregistrement du mapping UserRainbow"
                                                            )
                                                        }
                                                    }

                                                    override fun onFailure(
                                                        call: Call<Void>,
                                                        t: Throwable
                                                    ) {
                                                        Log.e(
                                                            "RainbowSDK",
                                                            "Erreur lors de l'enregistrement du mapping UserRainbow: ${t.message}"
                                                        )
                                                    }
                                                })
                                            }
                                        } else {
                                            Log.e(
                                                "RainbowSDK",
                                                "Échec de la création de l'utilisateur"
                                            )
                                        }
                                    }

                                    override fun onFailure(
                                        call: Call<Utilisateur>,
                                        t: Throwable
                                    ) {
                                        Log.e(
                                            "RainbowSDK",
                                            "Erreur lors de la création de l'utilisateur: ${t.message}"
                                        )
                                    }
                                })
                            }
                            // Proceed to the main activity
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }

                        override fun onFailure(call: Call<Utilisateur>, t: Throwable) {
                            Log.e(
                                "RainbowSDK",
                                "Erreur lors de la récupération de l'utilisateur: ${t.message}"
                            )
                            startActivity(
                                Intent(
                                    this@LoginActivity,
                                    MainActivity::class.java
                                )
                            )
                            finish()
                        }
                    })
                }

                override fun onError(
                    errorCode: RainbowSdk.ErrorCode,
                    errorMessage: String
                ) {
                    Log.e(
                        "RainbowSDK",
                        "Échec de la connexion : Code erreur - $errorCode, Détails - $errorMessage"
                    )
                    showAlertDialog(
                        "Login Failed",
                        "Error Code: $errorCode - Details: $errorMessage"
                    )
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
