package com.cnam.medic_assist.data.model

import java.sql.Time
import java.sql.Date

data class RendezVous(
    val idRDV: Int? = null,
    var intitule: String,
    var horaire : String,
    var daterdv: String,
    val idUser : Int,
    val idCentreMedical: Int,
    var isADRPrincipale : Boolean,
    //element venant de la jointure avec le centre médical (idCentreMedical)
    val nom : String? = null, //nom du cntre medical
    val numero_rue : String? = null,
    val rue : String? = null,
    val codepostal : Int? = null,
    val ville : String? = null
    )