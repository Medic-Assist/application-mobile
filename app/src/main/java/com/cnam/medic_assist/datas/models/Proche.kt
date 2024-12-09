package com.cnam.medic_assist.datas.models

class Proche (
        iduser: Int? = null,
        prenom : String,
        nom : String,
        numero_tel : String,
        var mail : String? = null,

    ) : Utilisateur(iduser,prenom, nom, RoleUser.Proche,numero_tel)
{
}