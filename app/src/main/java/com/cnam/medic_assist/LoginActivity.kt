package com.cnam.medic_assist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class LoginActivity() : AppCompatActivity() {
    private val PREFS_NAME = "LoginPrefs"
    private lateinit var editTextNewUsername: EditText
    private lateinit var editTextNewPassword: EditText
    private lateinit var btnRegister: Button

    constructor(parcel: Parcel) : this() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextNewUsername = findViewById(R.id.usernameEditText)
        editTextNewPassword = findViewById(R.id.passwordEditText)
        btnRegister = findViewById(R.id.loginButton)

        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        editor.putString("username", "test")
        editor.putString("password", "test")
        editor.apply()
    }

    fun OnClickConnexion(view: View) {
        if(editTextNewPassword.text != null && editTextNewUsername != null){
            val inputUsername = editTextNewUsername.text.toString()
            val inputPassword = editTextNewPassword.text.toString()

            login(inputUsername, inputPassword)
        }
        else{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Connection Failed")
            builder.setMessage("Username or password was not filled")
            builder.show()
        }

    }

    fun login(inputUsername :String, inputPassword :String ){
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val trueUsername = sharedPreferences.getString("username", null)
        val truePassword = sharedPreferences.getString("password", null)

        if (inputUsername == trueUsername && inputPassword == truePassword) {
            val i = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(i)
        }
        else{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Fail")
            builder.setMessage("password or username false")
            builder.show()
        }
    }
}
