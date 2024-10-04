package com.cnam.medic_assist.models

class Patient (
    idUser: Int? = null,
    prenom : String,
    nom : String,
    var adresse_principale : String,
    var codePostal_principal : Int,
    var ville_principale : String,
    var adresse_temporaire : String?=null,
    var codePostal_temporaire : Int?=null,
    var ville_temporaire : String?=null,


) : Utilisateur(idUser,prenom, nom,RoleUser.Patient)
{
    fun afficherDetailsPatient() {
        println("Patient: $prenom $nom")
        println("Adresse: $adresse_principale, $codePostal_principal, $ville_principale")
    }

    fun changerAdresse(nouvelleAdresse: String, nouveauCodePostal: Int, nouvelleVille: String) {
        adresse_principale = nouvelleAdresse
        codePostal_principal = nouveauCodePostal
        ville_principale = nouvelleVille
    }

    fun ajouterAdresseTemporaire(adresseTmp: String, CPTmp: Int, villeTmp: String) {
        adresse_temporaire = adresseTmp
        codePostal_temporaire = CPTmp
        ville_temporaire = villeTmp
    }
}