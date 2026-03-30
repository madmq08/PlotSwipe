package com.mario.plotswipe.data.repository

import com.mario.plotswipe.data.local.AppDatabase
import com.mario.plotswipe.data.local.MovieEntity
import com.mario.plotswipe.data.remote.MovieDto
import com.mario.plotswipe.data.remote.ProviderInfo
import com.mario.plotswipe.data.remote.RetrofitClient

// Ahora el repositorio necesita saber de la base de datos (AppDatabase)
class MovieRepository(private val database: AppDatabase) {
    private val api = RetrofitClient.api

    suspend fun fetchPopularMovies(page: Int = 1): List<MovieDto> {
        return try {
            // ¡Aquí le añadimos el número de página a la llamada de la API!
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

            // 🛡️ EL FILTRO: Quitamos las plataformas que contengan "Ads" o "anuncios"
            plataformas.filter { proveedor ->
                !proveedor.providerName.contains("Ads", ignoreCase = true) &&
                        !proveedor.providerName.contains("anuncios", ignoreCase = true)
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    // NUEVO: Función para guardar una película en Room
    suspend fun insertMovieToFavorites(movieDto: MovieDto) {
        // Traducimos el DTO (Internet) a Entity (Base de Datos)
        val entity = MovieEntity(
            id = movieDto.id,
            title = movieDto.title,
            posterPath = movieDto.posterPath ?: "",
            overview = movieDto.overview
        )
        // Usamos el DAO para guardarlo en Room
        database.movieDao().insertMovie(entity)
    }
    fun getAllSavedMovies() = database.movieDao().getAllSavedMovies()
    suspend fun deleteAllMovies() = database.movieDao().deleteAllMovies()

    // Trae SOLO las que están pendientes de ver (isWatched = 0)
    fun getFavoriteMovies() = database.movieDao().getFavoriteMovies()

    // Trae SOLO las que ya has visto (isWatched = 1)
    fun getWatchedMovies() = database.movieDao().getWatchedMovies()

    // El comando para cambiarla de lista
    suspend fun markAsWatched(movieId: Int) {
        database.movieDao().markAsWatched(movieId)
    }

    suspend fun deleteWatchedMovies() {
        database.movieDao().deleteWatchedMovies()
    }

    suspend fun markAsDiscarded(movieId: Int) {
        database.movieDao().markAsDiscarded(movieId)
    }

}