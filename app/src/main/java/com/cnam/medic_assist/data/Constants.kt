package com.cnam.medic_assist.data

import com.cnam.medic_assist.data.model.CentreMedical
import com.cnam.medic_assist.data.model.Patient
import com.cnam.medic_assist.data.model.PersonnelMed
import com.cnam.medic_assist.data.model.Proche
import com.cnam.medic_assist.data.model.Proche_Patient
import com.cnam.medic_assist.data.model.RendezVous
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat


val timeFormat = SimpleDateFormat("HH:mm")
val dateFormat = SimpleDateFormat("yyyy-MM-dd")
object Constants {

    val centresMedicaux = listOf(
        CentreMedical(1, "Clinique Sainte-Anne", "182", "Rue Philippe Thys", 67000, "Strasbourg"),
        CentreMedical(2, "Centre Dentaire", "4", "Rue Alphonse Matter", 88100, "Saint-Die-des-Vosges"),
        CentreMedical(3, "Cabinet d'ostéopathie", "1", "Rue du Pont Neuf", 67230, "Benfeld"),
        CentreMedical(4, "Ophtalmo Jung Michel", "1", "Place de Bergopre", 67130, "Schirmeck"),
        CentreMedical(5, "Hôpital de Hautepierre", "1", "Av. Molière", 67200, "Strasbourg"),
        CentreMedical(6, "Dr Guillaume Récher", "9", "Rte Marcel Proust", 67200, "Strasbourg"),
        CentreMedical(7, "Radiologie Clemenceau Sélestat", "4A", "Rue Georges Clemenceau", 67600, "Sélestat")
    )

    val patients = listOf(
        Patient(1,"Alice", "Dupont", "5", "Rue des Tulipes", 67600, "Sélestat"),
        Patient(2,"Bob", "Martin", "7", "Rue Gambetta", 88100, "Saint-Dié-des-Vosges"),
        Patient(3,"Claire", "Lemoine", "3", "Place du Marché", 67130, "Schirmeck"),
        Patient(4,"David", "Durand", "8", "Rue des Jardins", 88480, "Étival-Clairefontaine"),
        Patient(5,"Eve", "Moreau", "12", "Rue de la Poste", 67120, "Molsheim"),
        Patient(6,"Frank", "Petit", "9", "Rue de la Gare", 67240, "Bischwiller"),
        Patient(7,"Grace", "Kemberg", "14" ,"Rue des Tilleuls", 67390, "Marckolsheim"),
        Patient(8,"Hugo", "Blanc", "17" ,"Rue de la Liberté", 67240, "Oberhoffen-sur-Moder"),
        Patient(9,"Isabelle", "Verde", "22", "Rue des Vosges", 67100, "Strasbourg"),
        Patient(10,"Jack", "Rouge", "20", "Rue de la Forêt", 67600, "Sélestat")
    )

    val proches = listOf(
        Proche(11, "Louis", "Lemoine", "23", "Rue des Champs", 67210, "Obernai"),
        Proche(12, "Marie", "Durand", "10", "Rue du Lac", 88470, "La Bourgonce"),
        Proche(13, "Nina", "Moreau", "8", "Rue de la Poste", 67380, "Lingolsheim")
    )
    val prochePatientRelations = listOf(
        Proche_Patient(3, 11),  // Louis proche de Claire
        Proche_Patient(4, 12),  // Marie proche de David
        Proche_Patient(5, 13),  // Nina proche d'Eve
        Proche_Patient(1, 12),  // Marie proche d'Alice
        Proche_Patient(2, 11)   // Louis proche de Bob
    )

    val personnelMed = listOf(
        PersonnelMed("Jean", "Michel",14, 1),  // Jean Michel au Centre 1 (Strasbourg)
        PersonnelMed("Sophie", "Legrand",15, 2),  // Sophie Legrand au Centre 2 (Saint-Die-des-Vosges)
        PersonnelMed("Paul", "Durand",16, 5),  // Paul Durand au Centre 5 (Hôpital de Hautepierre)
        PersonnelMed("Julie", "Martin",17, 6),  // Julie Martin au Centre 6 (Strasbourg)
        PersonnelMed("Luc", "Dubois",18, 7)   // Luc Dubois au Centre 7 (Sélestat)
    )

    /*val rdvList = listOf(
        // Rendez-vous pour Alice
        RendezVous(1, "Radio Hanche Droite", Time.valueOf("10:00:00"), Date.valueOf("2024-09-20"), 1, 7, true),
        RendezVous(2, "Osthéopathie", Time.valueOf("11:30:00"), Date.valueOf("2024-09-25"), 1, 3, true),

        // Rendez-vous pour Bob
        RendezVous(3, "Dévitalisation Dent", Time.valueOf("14:30:00"), Date.valueOf("2024-09-21"), 2, 2, true),
        RendezVous(4, "Controle Dent dévitalisé", Time.valueOf("09:00:00"), Date.valueOf("2024-09-26"), 2, 2, true),

        // Rendez-vous pour Claire
        RendezVous(5, "Ophtalmo", Time.valueOf("09:00:00"), Date.valueOf("2024-09-22"), 3, 4, true),
        RendezVous(6, "Médecin généraliste", Time.valueOf("10:30:00"), Date.valueOf("2024-09-27"), 3, 6, true),

        // Rendez-vous pour David
        RendezVous(7, "Dentiste", Time.valueOf("11:00:00"), Date.valueOf("2024-09-23"), 4, 2, true),
        RendezVous(8, "Ophtalmo", Time.valueOf("08:30:00"), Date.valueOf("2024-09-28"), 4, 4, true),

        // Rendez-vous pour Eve
        RendezVous(9, "RDV Anestésiste", Time.valueOf("08:30:00"), Date.valueOf("2024-09-24"), 5, 5, true),
        RendezVous(10, "Opération", Time.valueOf("09:45:00"), Date.valueOf("2024-09-29"), 5, 5, true),

        // Rendez-vous pour Frank
        RendezVous(11, "Médecin Généraliste", Time.valueOf("10:00:00"), Date.valueOf("2024-09-30"), 6, 6, true),

        // Rendez-vous pour Grace
        RendezVous(12, "Radio épaule gauche", Time.valueOf("14:00:00"), Date.valueOf("2024-10-01"), 7, 7, true),

        // Rendez-vous pour Hugo
        RendezVous(13, "Controle après opération", Time.valueOf("09:30:00"), Date.valueOf("2024-10-02"), 8, 1, true),

        // Rendez-vous pour Isabelle
        RendezVous(14, "RDV Sage femme", Time.valueOf("11:00:00"), Date.valueOf("2024-10-03"), 9, 5, true),

        // Rendez-vous pour Jack
        RendezVous(15, "Radio mâchoire", Time.valueOf("15:30:00"), Date.valueOf("2024-10-04"), 10, 7, true)
    )*/
}
