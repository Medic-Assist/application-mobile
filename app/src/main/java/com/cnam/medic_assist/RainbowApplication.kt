package com.cnam.medic_assist

import android.app.Application
import android.util.Log
import com.ale.rainbowsdk.RainbowSdk
import com.ale.rainbowsdk.Connection

class RainbowApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("RainbowSDK", "Initialisation du SDK Rainbow...")
        RainbowSdk.instance().initialize(
            applicationContext = this,
            applicationId = "a21246a0946111efa94a41c51a51acb0",//"39c230b0825a11efa8b43145d0d41bca",
            applicationSecret = "hH0KBTb3gltywRmA2FwXfBzNTJXfzdEwHeXbCND9yG5IqSfvZ5TcO4X4jUywN2kC" //"nSxEXiZcNSaOQ9XgPxqMsWrC6f8uLnYv7a8C9DlJ9k2pUKq4uLDtsGX528yNxTUh"
        )
        // Vérifiez l'état de la connexion après l'initialisation
        if (RainbowSdk.instance().connection().state == Connection.ConnectionState.DISCONNECTED) {
            Log.d("RainbowSDK", "Le SDK Rainbow est initialisé mais la connexion est en état DISCONNECTED.")
        } else {
            Log.d("RainbowSDK", "Le SDK Rainbow est initialisé et en cours de connexion.")
        }
    }
}
