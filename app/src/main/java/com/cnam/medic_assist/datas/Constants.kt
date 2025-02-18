package com.cnam.medic_assist.datas

import com.cnam.medic_assist.datas.models.CentreMedical
import com.cnam.medic_assist.datas.models.ModeTransport
import com.cnam.medic_assist.datas.models.Patient
import com.cnam.medic_assist.datas.models.PersonnelMed
import com.cnam.medic_assist.datas.models.Proche
import com.cnam.medic_assist.datas.models.Proche_Patient
import com.cnam.medic_assist.datas.models.RendezVous
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
        Patient(1,"Alice", "Dupont", "fdffd", "alice.dupont@mail.com","1962-02-23","5", "Rue des Tulipes", "67600", "Sélestat"),
        Patient(2,"Bob", "Martin", "fdffd","bob.martin@mail.com","1955-06-18","7", "Rue Gambetta", "88100", "Saint-Dié-des-Vosges"),
        Patient(3,"Claire", "Lemoine", "fdffd","claire.lemoine@mail.com","1980-03-01", "3", "Place du Marché", "67130", "Schirmeck"),
        Patient(4,"David", "Durand", "fdffd","david.durand@mail.com","1949-10-05","8", "Rue des Jardins", "88480", "Étival-Clairefontaine"),
        Patient(5,"Eve", "Moreau", "fdffd","eve.moreau@mail.com","1952-08-16", "12", "Rue de la Poste", "67120", "Molsheim"),
        Patient(6,"Frank", "Petit", "fdffd",null,"1940-01-25","9", "Rue de la Gare", "67240", "Bischwiller"),
        Patient(7,"Grace", "Kemberg","fdffd", "grace.kemberg@mail.com","1990-05-05","14" ,"Rue des Tilleuls", "67390", "Marckolsheim"),
        Patient(8,"Hugo", "Blanc","fdffd", "hugo.blanc@mail.com","1973-11-19","17" ,"Rue de la Liberté", "67240", "Oberhoffen-sur-Moder"),
        Patient(9,"Isabelle", "Verde", "fdffd","isabelle.verde@mail.com","1951-09-20","22", "Rue des Vosges", "67100", "Strasbourg"),
        Patient(10,"Jack", "Rouge", "fdffd","jack.rouge@mail.com","1939-04-21","20", "Rue de la Forêt", "67600", "Sélestat")
    )

    val proches = listOf(
        Proche(11, "Louis", "Lemoine","0123456789" ,"louis.lemoine@mail.com"),
        Proche(12, "Marie", "Durand","0123456789" ,"marie.durand@mail.com"),
        Proche(13, "Nina", "Moreau","0123456789" ,"nina.moreau@mail.com")
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

    var rdvList = listOf(
        // Rendez-vous pour Alice
        RendezVous(1, "Radio Hanche Droite", "14:30:00", "2025-02-12", "",1, 7, true,"Radiologie Clemenceau Sélestat", "4A", "Rue Georges Clemenceau", "67600", "Sélestat"),
        RendezVous(2, "Osthéopathie", "11:30:00", "2024-09-25", "",1, 3, true),

        // Rendez-vous pour Bob
        RendezVous(3, "Dévitalisation Dent", "14:30:00", "2024-09-21", "",2, 2, true),
        RendezVous(4, "Controle Dent dévitalisé", "09:00:00","2024-09-26", "",2, 2, true),

        // Rendez-vous pour Claire
        RendezVous(5, "Ophtalmo", "09:00:00", "2024-09-22", "",3, 4, true),
        RendezVous(6, "Médecin généraliste", "10:30:00", "2024-09-27","", 3, 6, true),

        // Rendez-vous pour David
        RendezVous(7, "Dentiste", "11:00:00", "2024-09-23", "",4, 2, true),
        RendezVous(8, "Ophtalmo", "08:30:00", "2024-09-28", "",4, 4, true),

        // Rendez-vous pour Eve
        RendezVous(9, "RDV Anestésiste", "08:30:00", "2024-09-24","", 5, 5, true),
        RendezVous(10, "Opération", "09:45:00", "2024-09-29","", 5, 5, true),

        // Rendez-vous pour Frank
        RendezVous(11, "Médecin Généraliste", "10:00:00", "2024-09-30","", 6, 6, true),

        // Rendez-vous pour Grace
        RendezVous(12, "Radio épaule gauche", "14:00:00", "2024-10-01", "",7, 7, true),

        // Rendez-vous pour Hugo
        RendezVous(13, "Controle après opération", "09:30:00", "2024-10-02", "",8, 1, true),

        // Rendez-vous pour Isabelle
        RendezVous(14, "RDV Sage femme", "11:00:00", "2024-10-03","", 9, 5, true),

        // Rendez-vous pour Jack
        RendezVous(15, "Radio mâchoire", "15:30:00", "2024-10-04", "",10, 7, true) ,

        RendezVous(15, "TEST NOTIF", "21:30:00", "2024-11-27","", 10, 7, true),
        RendezVous(16, "TEST NOTIF !!!!!!!!!!", "14:35:00", "2024-11-29", "", 10, 7, true)
    )

    val modes_transports = listOf(ModeTransport("Voiture"),
            ModeTransport("Taxi"),
            ModeTransport("Transports en commun"),

    )
}
