package com.cnam.medic_assist.data.model


enum class RoleUser {
    Patient,
    Proche,
    PersonnelMed
}

open class Utilisateur (
    val idUser: Int? = null, // Nullable et initialisé à null pour laisser Mysql générer l'idUnique
    var prenom : String,
    var nom : String,
    val role : RoleUser,
    var numero_tel : String?= null
){
    fun afficherDetailsUtilisateur() {
        println("Utilisateur: $prenom $nom ($role)")
    }
    fun changerNom(nouveauNom: String) {
        nom = nouveauNom
    }
    fun changerPrenom(nouveauPrenom: String) {
        prenom = nouveauPrenom
    }
}