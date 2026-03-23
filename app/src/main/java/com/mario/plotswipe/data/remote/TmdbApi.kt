package com.mario.plotswipe.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/popular") // Pedimos las películas más populares
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES", // Para que nos vengan en español
        @Query("page") page: Int = 1
    ): MovieResponse
}