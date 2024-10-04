package com.cnam.medic_assist.models

import java.sql.Time
import java.util.Date

data class RendezVous(
    val idRDV: Int? = null,
    var intitule: String,
    val horaire : Time,
    var dateRDV: Date,
    val iduser : Int,
    val idCentreMedical: Int,
    var isADRPrincipale : Boolean
    )
