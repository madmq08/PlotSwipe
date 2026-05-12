package com.mario.plotswipe.data.repository

import com.mario.plotswipe.data.local.AppDatabase
import com.mario.plotswipe.data.local.MovieEntity
import com.mario.plotswipe.data.remote.MovieDto
import com.mario.plotswipe.data.remote.ProviderInfo
import com.mario.plotswipe.data.remote.RetrofitClient

class MovieRepository(private val database: AppDatabase) {
    private val api = RetrofitClient.api

    suspend fun fetchPopularMovies(page: Int = 1): List<MovieDto> {
        return try {
            val response = api.getPopularMovies(apiKey = RetrofitClient.API_KEY, page = page)
            response.results
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMovieProviders(movieId: Int): List<ProviderInfo> {
        return try {
            val response = api.getMovieProviders(movieId, RetrofitClient.API_KEY)
            val plataformas = response.results["ES"]?.flatrate ?: emptyList()

            plataformas.filter { proveedor ->
                !proveedor.providerName.contains("Ads", ignoreCase = true) &&
                        !proveedor.providerName.contains("anuncios", ignoreCase = true)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // 👇 LA CLAVE DEL CAMBIO 👇
    suspend fun insertMovieToFavorites(movieDto: MovieDto, userId: String) {
        // 1. Creamos la entidad SIN userId (ahora MovieEntity es solo info técnica)
        val entity = MovieEntity(
            id = movieDto.id,
            title = movieDto.title,
            posterPath = movieDto.posterPath ?: "",
            overview = movieDto.overview
        )

        // 2. Usamos la función @Transaction del DAO que creamos antes.
        // Esta función guardará la peli en 'movies' y la relación en 'user_movie_cross_ref'
        database.movieDao().saveMovieForUser(entity, userId)
    }

    // Las lecturas siguen funcionando igual porque mantuvimos los nombres en el DAO,
    // pero ahora por debajo Room está haciendo los INNER JOIN automáticos.
    fun getAllSavedMovies(userId: String) = database.movieDao().getAllSavedMovies(userId)

    suspend fun deleteAllMovies(userId: String) = database.movieDao().deleteAllMovies(userId)

    fun getFavoriteMovies(userId: String) = database.movieDao().getFavoriteMovies(userId)

    fun getWatchedMovies(userId: String) = database.movieDao().getWatchedMovies(userId)

    // Estas funciones ahora actualizan la tabla intermedia (user_movie_cross_ref)
    suspend fun markAsWatched(movieId: Int, userId: String) {
        database.movieDao().markAsWatched(movieId, userId)
    }

    suspend fun deleteWatchedMovies(userId: String) {
        database.movieDao().deleteWatchedMovies(userId)
    }

    suspend fun markAsDiscarded(movieId: Int, userId: String) {
        database.movieDao().markAsDiscarded(movieId, userId)
    }
}