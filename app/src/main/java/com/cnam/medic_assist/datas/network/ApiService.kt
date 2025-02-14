package com.cnam.medic_assist.datas.network

import com.cnam.medic_assist.datas.models.CentreMedical
import com.cnam.medic_assist.datas.models.EtatRdv
import com.cnam.medic_assist.datas.models.ModeTransport
import com.cnam.medic_assist.datas.models.Patient
import com.cnam.medic_assist.datas.models.PatientAPI
import com.cnam.medic_assist.datas.models.Proche
import com.cnam.medic_assist.datas.models.RDVSend
import com.cnam.medic_assist.datas.models.RendezVous
import com.cnam.medic_assist.datas.models.Utilisateur
import com.cnam.medic_assist.datas.models.UtilisateurRequete
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("rendezvous")
    fun addRendezvous(@Body rdv: RDVSend): Call<RDVSend>
    /** Requêtes pour les utilisateurs **/

    // Obtenir tous les utilisateurs
    @GET("utilisateurs")
    fun getAllUsers(): Call<List<Utilisateur>>

    @POST("utilisateurs/call-utilisateurs")
    fun callUser(@Body user: UtilisateurRequete): Call<Utilisateur>

    // Ajouter un utilisateur
    @POST("utilisateurs")
    fun addUser(@Body user: Utilisateur): Call<Utilisateur>

    // Obtenir un utilisateur par ID
    @GET("utilisateurs/{id}")
    fun getUserById(@Path("id") id: Int): Call<Utilisateur>

    // Mettre à jour le nom et prénom d'un utilisateur
    @PUT("utilisateurs/nom/{id}")
    fun updateUserName(@Path("id") id: Int, @Body updatedData: Map<String, String>): Call<Void>

    // Ajouter une adresse temporaire au patient
    @PUT("utilisateurs/adresseTMP/{id}")
    fun updatePatientTemporaryAddress(@Path("id") id: Int, @Body addressData: Map<String, String>): Call<Void>

    // Supprimer un utilisateur
    @DELETE("utilisateurs/{id}")
    fun deleteUser(@Path("id") id: Int): Call<Void>

    // Obtenir un patient par ID
    @GET("utilisateurs/patient/{id}")
    fun getPatientById(@Path("id") id: Int): Call<Patient>

    // Ajouter un patient
    @POST("utilisateurs/patient")
    fun addPatient(@Body patient: Patient): Call<Patient>

    // Obtenir un proche par ID
    @GET("utilisateurs/proche/{id}")
    fun getProcheById(@Path("id") id: Int): Call<Proche>

    // Ajouter un proche
    @POST("utilisateurs/proche")
    fun addProche(@Body proche: Proche): Call<Proche>

    // Ajouter une relation Proche-Patient
    @POST("utilisateurs/proche_patient")
    fun addProchePatientRelation(@Body relationData: Map<String, Int?>): Call<Void>

    // Obtenir le personnel médical par ID
    @GET("utilisateurs/personnelMed/{id}")
    fun getPersonnelMedById(@Path("id") id: Int): Call<Utilisateur>


    @GET("utilisateurs/patient")
    fun getAllPatients(): Call<List<PatientAPI>>
    // Ajouter un personnel médical
    @POST("utilisateurs/personnelMed")
    fun addPersonnelMed(@Body personnelMedData: Map<String, Int>): Call<Void>

    // Obtenir les proches d'un patient par ID
    @GET("utilisateurs/proches_patient/{id}")
    fun getProchesByPatientId(@Path("id") id: Int): Call<List<Proche>>

    // Obtenir les patients surveillés par un proche par ID
    @GET("utilisateurs/patients_proche/{id}")
    fun getPatientsByProcheId(@Path("id") id: Int): Call<List<Patient>>

    // Liste des modes de transports
    @GET("utilisateurs/modes_transports")
    fun getModesTransports(): Call<List<ModeTransport>>

    @DELETE("utilisateurs/proche/{id}")
    fun deleteProche(@Path("id") id: Int): Call<Void>

    // Mettre à jour un proche
    @PUT("utilisateurs/proche/{id}")
    fun updateProche(@Path("id") id: Int, @Body updatedData: Proche): Call<Void>

    // Mettre à jour un procpatienthe
    @PUT("utilisateurs/patient/{id}")
    fun updatePatient(@Path("id") id: Int, @Body updatedData: Patient): Call<Void>


    /** Requêtes pour les rendez-vous **/
    // Liste des rendez-vous associés à un patient donné
    @GET("rendezvous/patient/{idUser}")
    fun getRendezvousByUserId(@Path("idUser") idUser: Int): Call<List<RendezVous>>

    // Mettre à jour un rendez-vous
    @PUT("rendezvous/{id}")
    fun updateRDV(@Path("id") id: Int, @Body rdv: RendezVous): Call<Void>

    // Liste des rendez-vous associés à un patient donné
    @GET("rendezvous/statusTrajet/{id}")
    fun getStatusRDV(@Path("id") id: Int): Call<EtatRdv>

    @PUT("status/{id}")
    fun updateStatusRDV(@Path("id") id: Int, @Body body: Map<String, String>): Call<Void>

    /** Requêtes pour les centres médicaux **/
    // Liste des centres médicaux
    @GET("centres")
    fun getCentresMedicaux(): Call<List<CentreMedical>>
}