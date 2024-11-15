package com.cnam.medic_assist.data.model

class Patient (
    idUser: Int? = null,
    prenom : String,
    nom : String,
    var numero_rue_principale : String,
    var rue_principale : String,
    var codePostal_principal : Int,
    var ville_principale : String,
    var adresse_temporaire : String?=null,
    var codePostal_temporaire : Int?=null,
    var ville_temporaire : String?=null,


) : Utilisateur(idUser,prenom, nom, RoleUser.Patient)
{
    fun ajouterAdresseTemporaire(adresseTmp: String, CPTmp: Int, villeTmp: String) {
        adresse_temporaire = adresseTmp
        codePostal_temporaire = CPTmp
        ville_temporaire = villeTmp
    }
}