package com.cnam.medic_assist.models

class Proche (
        idUser: Int? = null,
        prenom : String,
        nom : String,
        var adresse : String,
        var codePostal : Int,
        var ville : String

    ) : Utilisateur(idUser,prenom, nom,RoleUser.Proche)
{
        fun afficherDetailsProche() {
                println("Proche: $prenom $nom")
                println("Adresse: $adresse, $codePostal, $ville")
        }
        fun changerAdresse(nouvelleAdresse: String, nouveauCodePostal: Int, nouvelleVille: String) {
                adresse = nouvelleAdresse
                codePostal = nouveauCodePostal
                ville = nouvelleVille
        }
}