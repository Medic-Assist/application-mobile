package com.cnam.medic_assist.datas.models

class Proche (
        idUser: Int? = null,
        prenom : String,
        nom : String,
        var numero_rue : String,
        var rue : String,
        var codePostal : Int,
        var ville : String

    ) : Utilisateur(idUser,prenom, nom, RoleUser.Proche)
{
}