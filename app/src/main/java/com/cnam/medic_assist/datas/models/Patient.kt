package com.cnam.medic_assist.datas.models

import android.os.Parcelable

class Patient (
    iduser: Int? = null,
    prenom : String,
    nom : String,
    idcontact: String? = null,
    var mail : String?=null,
    var date_naissance : String,
    var numero_rue_principal : String,
    var rue_principale : String,
    var codepostal_principal : String,
    var ville_principale : String,
    var adresse_temporaire : String?=null,
    var codePostal_temporaire : String?=null,
    var ville_temporaire : String?=null,
    var modetransport : String?="Voiture"


    ) : Utilisateur(iduser,prenom, nom, RoleUser.Patient, idcontact), Parcelable
{

}