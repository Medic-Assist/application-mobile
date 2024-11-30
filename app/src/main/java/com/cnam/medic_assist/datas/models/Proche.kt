package com.cnam.medic_assist.datas.models

class Proche (
        idUser: Int? = null,
        prenom : String,
        nom : String,
        numero_tel : String,
        var numero_rue : String? = null,
        var rue : String? = null,
        var codePostal : Int? = null,
        var ville : String? = null

    ) : Utilisateur(idUser,prenom, nom, RoleUser.Proche,numero_tel)
{
}