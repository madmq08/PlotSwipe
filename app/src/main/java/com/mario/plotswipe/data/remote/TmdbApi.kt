package com.mario.plotswipe.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {
    @GET("movie/popular") // o la ruta que estés usando ("discover/movie", etc.)
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("page") page: Int = 1 // 👈 ¡AÑADIMOS ESTO!
    ): MovieResponse
}