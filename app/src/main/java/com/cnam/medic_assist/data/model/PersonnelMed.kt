package com.cnam.medic_assist.data.model

class PersonnelMed (
    prenom : String,
    nom : String,
    var idCentreMed : Int,
    idUser: Int? = null

) : Utilisateur(idUser,prenom, nom, RoleUser.PersonnelMed)