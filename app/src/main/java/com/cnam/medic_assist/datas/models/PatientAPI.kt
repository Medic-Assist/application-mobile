package com.cnam.medic_assist.datas.models

import com.google.gson.annotations.SerializedName

data class PatientAPI(
    @SerializedName("iduser") val iduser: Int,
    @SerializedName("nom") val nom: String,
    @SerializedName("prenom") val prenom: String,
    @SerializedName("idcontact") val idcontact: String?,
    @SerializedName("mail") val mail: String?,
    @SerializedName("date_naissance") val date_naissance: String,
    @SerializedName("numero_rue_principal") val numero_rue_principal: String,
    @SerializedName("rue_principale") val rue_principale: String,
    @SerializedName("codepostal_principal") val codepostal_principal: String,
    @SerializedName("ville_principale") val ville_principale: String
)