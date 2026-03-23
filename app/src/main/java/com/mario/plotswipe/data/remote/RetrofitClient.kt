package com.mario.plotswipe.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // La dirección principal de la base de datos de películas
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    // Tu llave VIP
    const val API_KEY = "b5eef53add80750ba4251fb213ca8be8"

    // La "radio" que usaremos en toda la app para pedir películas
    val api: TmdbApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // El traductor de JSON a Kotlin
            .build()
            .create(TmdbApi::class.java)
    }
}