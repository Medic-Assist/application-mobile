package com.cnam.medic_assist.data.model

class CentreMedical (
    val idCentreMed: Int? = null,
    var nom: String,
    var adresse: String,
    var codePostal: Int,
    var ville : String
){
    fun afficherDetailsCentreMedical() {
        println("Centre: $nom")
        println("Adresse: $adresse, $codePostal, $ville")
    }
    fun changerAdresse(nouvelleAdresse: String, nouveauCodePostal: Int, nouvelleVille: String) {
        adresse = nouvelleAdresse
        codePostal = nouveauCodePostal
        ville = nouvelleVille
    }
}