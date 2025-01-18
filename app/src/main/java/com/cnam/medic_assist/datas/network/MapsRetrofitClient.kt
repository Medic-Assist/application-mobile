package com.cnam.medic_assist.datas.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MapsRetrofitClient {
    private const val BASE_URL = "https://data.geopf.fr/navigation/"

    val instance: GeoportailApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeoportailApi::class.java)
    }
}
