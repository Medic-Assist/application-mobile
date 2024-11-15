package com.cnam.medic_assist

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class RouteResponse(
    val duration: Double, // Temps en secondes
    val distance: Double // Distance en kilomètres
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
