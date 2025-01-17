package com.cnam.medic_assist.datas.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class RouteResponse(
    val geometry: Geometry,
    val distance: Double,
    val duration: String,
    val portions: List<Portion>
)

data class Geometry(
    val coordinates: List<List<Double>>,  // Liste des [longitude, latitude]
    val type: String
)

data class Portion(
    val start: String,
    val end: String,
    val distance: Double,
    val duration: String,
    val steps: List<Step>
)

data class Step(
    val geometry: Geometry,
    val attributes: Attributes,
    val distance: Double,
    val duration: String,
    val instruction: Instruction
)

data class Attributes(
    val name: Map<String, String>  // "nom_1_gauche", "nom_1_droite", etc.
)

data class Instruction(
    val type: String,  // "depart", "turn", etc.
    val modifier: String?  // "left", "right", "straight", etc.
)


interface GeoportailApi {
    @GET("itineraire")
    fun getRoute(
        @Query("resource") resource: String = "bdtopo-osrm",
        @Query("start") start: String,
        @Query("end") end: String,
        @Query("profile") profile: String = "car",
        @Query("optimization") optimization: String = "fastest",
        @Query("distanceUnit") distanceUnit: String = "kilometer",
        @Query("timeUnit") timeUnit: String = "standard"
    ): Call<RouteResponse>
}
