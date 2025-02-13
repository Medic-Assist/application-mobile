package com.cnam.medic_assist.datas.models

class RDVSend (
    var intitule: String,
    var horaire : String,
    var dateRDV: String,
    var idBulleRainbow: String,
    val idUser : Int,
    val idCentreMed: Int,
    var isADRPrincipale : Boolean,
    val idDoctor : Int? = null,
)