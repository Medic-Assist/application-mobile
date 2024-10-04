package com.cnam.medic_assist.models


enum class RoleUser {
    Patient,
    Proche,
    PersonnelMed
}

open class Utilisateur (
    val idUser: Int? = null, // Nullable et initialisé à null pour laisser Mysql générer l'idUnique
    var prenom : String,
    var nom : String,
    val role : RoleUser
)