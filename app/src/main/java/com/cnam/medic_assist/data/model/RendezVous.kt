package com.cnam.medic_assist.data.model

import java.sql.Time
import java.util.Date

data class RendezVous(
    val idRDV: Int? = null,
    var intitule: String,
    val horaire : Time,
    var daterdv: Date,
    val idUser : Int,
    val idCentreMedical: Int,
    var isADRPrincipale : Boolean
    )