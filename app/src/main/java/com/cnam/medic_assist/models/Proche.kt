package com.cnam.medic_assist.models

class Proche (
        prenom : String,
        nom : String,
        var adresse : String,
        var codePostal : Int,
        var ville : String,
        idUser: Int? = null

    ) : Utilisateur(idUser,prenom, nom,RoleUser.Proche)