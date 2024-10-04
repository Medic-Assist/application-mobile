package com.cnam.medic_assist.models

class Patient (
    prenom : String,
    nom : String,
    var adresse_principale : String,
    var codePostal_principal : Int,
    var ville_principale : String,
    var adresse_temporaire : String,
    var codePostal_temporaire : Int,
    var ville_temporaire : String,
    idUser: Int? = null

) : Utilisateur(idUser,prenom, nom,RoleUser.Patient)