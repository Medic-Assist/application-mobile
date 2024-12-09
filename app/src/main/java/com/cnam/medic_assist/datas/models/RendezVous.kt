package com.cnam.medic_assist.datas.models

data class RendezVous(
    var idrdv: Int? = null,
    var intitule: String,
    var horaire : String,
    var daterdv: String,
    val idUser : Int,
    val idCentreMedical: Int,
    var isADRPrincipale : Boolean,
    //element venant de la jointure avec le centre médical (idCentreMedical)
    val nom : String? = "", //nom du cntre medical
    val numero_rue : String? = "",
    val rue : String? = "",
    val codepostal : String? = "",
    val ville : String? = ""
    )