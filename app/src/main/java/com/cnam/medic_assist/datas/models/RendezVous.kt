package com.cnam.medic_assist.datas.models

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

data class RendezVous(
    var idrdv: Int? = null,
    var intitule: String,
    var horaire : String,
    var daterdv : String,
    val idUser : Int,
    val idCentreMedical: Int,
    var isADRPrincipale : Boolean,
    //element venant de la jointure avec le centre médical (idCentreMedical)
    val nom : String? = "", //nom du cntre medical
    val numero_rue : String? = "",
    val rue : String? = "",
    val codepostal : String? = "",
    val ville : String? = ""
    ) {
    val dateFormatted: Date?
        get() {
            return try {
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE)
                format.parse(daterdv)
            } catch (e: Exception) {
                null  // en cas d'erreur de parsing
            }
        }
}