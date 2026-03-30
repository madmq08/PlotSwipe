package com.mario.plotswipe.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface TmdbApi {
    @GET("movie/popular") // o la ruta que estés usando ("discover/movie", etc.)
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("region") region: String = "ES",
        @Query("page") page: Int = 1 // 👈 ¡AÑADIMOS ESTO!
    ): MovieResponse
    @GET("movie/{movie_id}/watch/providers")
    suspend fun getMovieProviders(
        @Path("movie_id") movieId: Int, // Sustituimos el hueco de la URL por el ID de la peli
        @Query("api_key") apiKey: String = "TU_API_KEY" // (Pon aquí tu clave de TMDB si la tenías puesta en la otra)
    ): ProviderResponse
}
