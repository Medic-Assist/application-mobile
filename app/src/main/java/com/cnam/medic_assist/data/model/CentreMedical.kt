package com.cnam.medic_assist.data.model

class CentreMedical (
    val idCentreMed: Int? = null,
    var nom: String,
    var numero_rue: String,
    var rue: String,
    var codePostal: Int,
    var ville : String
){
    fun afficherDetailsCentreMedical() {
        println("Centre: $nom")
        println("Adresse: $numero_rue,$rue, $codePostal, $ville")
    }
}