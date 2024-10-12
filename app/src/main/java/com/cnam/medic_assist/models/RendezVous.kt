package com.cnam.medic_assist.models

data class RendezVous(
    val id: Int,
    val intitule: String,
    val adresse: String,
    val heureRDV: String,
    val dateRDV: String
)
