package com.cnam.medic_assist.datas

import com.cnam.medic_assist.models.RendezVous

object Constants {
    val rdvList = listOf(
        RendezVous(1, "Consultation générale", "123 Rue de la Santé 67200 Strasbourg", "10:00", "2024-09-20"),
        RendezVous(2, "Dentiste", "456 Rue des Oliviers 88100 Saint-Die-des-Vosges", "14:30", "2024-09-21"),
        RendezVous(3, "Ophtalmologue", "789 Rue de la Vigne 67230 Benfeld", "09:00", "2024-09-22"),
        RendezVous(4, "Kinésithérapeute", "321 Rue du Sport 68000 Colmar", "11:00", "2024-09-23"),
        RendezVous(5, "Chirurgien", "Hopital de Hautepierre 67200 Strasbourg", "08:30", "2024-09-24")
    )
}
