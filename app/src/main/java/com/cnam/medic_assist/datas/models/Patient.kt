package com.cnam.medic_assist.datas.models

import android.os.Parcelable

class Patient (
    iduser: Int? = null,
    prenom : String,
    nom : String,
    var mail : String?=null,
    var date_naissance : String,
    var numero_rue_principal : String,
    var rue_principale : String,
    var codepostal_principal : Int,
    var ville_principale : String,
    var adresse_temporaire : String?=null,
    var codePostal_temporaire : Int?=null,
    var ville_temporaire : String?=null,
    var modetransport : String?="Voiture"


    ) : Utilisateur(iduser,prenom, nom, RoleUser.Patient), Parcelable
{
    fun ajouterAdresseTemporaire(adresseTmp: String, CPTmp: Int, villeTmp: String) {
        adresse_temporaire = adresseTmp
        codePostal_temporaire = CPTmp
        ville_temporaire = villeTmp
    }
}