package com.cnam.medic_assist.data.network

import com.cnam.medic_assist.data.model.CentreMedical
import com.cnam.medic_assist.data.model.Patient
import com.cnam.medic_assist.data.model.Proche
import com.cnam.medic_assist.data.model.RendezVous
import com.cnam.medic_assist.data.model.Utilisateur
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Obtenir tous les utilisateurs
    @GET("/utilisateurs")
    fun getAllUsers(): Call<List<Utilisateur>>

    // Ajouter un utilisateur
    @POST("/utilisateurs")
    fun addUser(@Body user: Utilisateur): Call<Utilisateur>

    // Obtenir un utilisateur par ID
    @GET("/utilisateurs/{id}")
    fun getUserById(@Path("id") id: Int): Call<Utilisateur>

    // Mettre à jour un utilisateur
    @PUT("/utilisateurs/{id}")
    fun updateUser(@Path("id") id: Int, @Body user: Utilisateur): Call<Void>

    // Supprimer un utilisateur
    @DELETE("/utilisateurs/{id}")
    fun deleteUser(@Path("id") id: Int): Call<Void>

    //Renvoie l'utilisateur de l'appli associer au compte rainbow donné
    @GET("utilisateurs/rainbow/{email}")
    fun getUtilisateurRainbow(@Path("email") email: String): Call<List<Utilisateur>>

    //Renvoie la list de rendez-vous associé au patient donné
    @GET("rendezvous/patient/{idUser}")
    fun getRendezvousByUserId(@Path("idUser") idUser: Int): Call<List<RendezVous>>
    // Mettre à jour un utilisateur
    @PUT("/rendezvous/{id}")
    fun updateRDV(@Path("id") id: Int, @Body rdv: RendezVous): Call<Void>

    //Liste des proches pour un patient donné
    @GET("utilisateurs/proches/{id}")
    fun getProchesByPatientId(@Path("id") id: Int): Call<List<Proche>>

    //patient(s) associé au nom prénom donnés
    @GET("utilisateurs/patients/search")
    fun searchPatient(@Query("prenom") prenom: String, @Query("nom") nom: String): Call<List<Patient>>

    //liste des centres médicaux
    @GET("centres")
    fun getCentresMedicaux(): Call<List<CentreMedical>>
}