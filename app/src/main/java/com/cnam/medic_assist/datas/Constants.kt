package com.cnam.medic_assist.datas

import com.cnam.medic_assist.models.RendezVous
import java.sql.Time
import java.text.SimpleDateFormat


val timeFormat = SimpleDateFormat("HH:mm")
val dateFormat = SimpleDateFormat("yyyy-MM-dd")
object Constants {
    val rdvList = listOf(
        RendezVous(
            idRDV = 1,
            intitule = "Consultation générale",
            horaire = Time(timeFormat.parse("10:00").time),
            dateRDV = dateFormat.parse("2024-09-20"),
            idUser = 123,
            idCentreMedical = 1,
            isADRPrincipale = true
        ),
        RendezVous(
            idRDV = 2,
            intitule = "Dentiste",
            horaire = Time(timeFormat.parse("14:30").time),
            dateRDV = dateFormat.parse("2024-09-21"),
            idUser = 124,
            idCentreMedical = 2,
            isADRPrincipale = false
        ),
        RendezVous(
            idRDV = 3,
            intitule = "Ophtalmologue",
            horaire = Time(timeFormat.parse("09:00").time),
            dateRDV = dateFormat.parse("2024-09-22"),
            idUser = 125,
            idCentreMedical = 3,
            isADRPrincipale = true
        ),
        RendezVous(
            idRDV = 4,
            intitule = "Kinésithérapeute",
            horaire = Time(timeFormat.parse("11:00").time),
            dateRDV = dateFormat.parse("2024-09-23"),
            idUser = 126,
            idCentreMedical = 4,
            isADRPrincipale = true
        ),
        RendezVous(
            idRDV = 5,
            intitule = "Chirurgien",
            horaire = Time(timeFormat.parse("08:30").time),
            dateRDV = dateFormat.parse("2024-09-24"),
            idUser = 127,
            idCentreMedical = 5,
            isADRPrincipale = false
        )
    )
}
