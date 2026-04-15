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

    // 👇 NUEVO: Añadimos userId para guardar la película a nombre de un usuario específico
    suspend fun insertMovieToFavorites(movieDto: MovieDto, userId: String) {
        // Traducimos el DTO (Internet) a Entity (Base de Datos)
        val entity = MovieEntity(
            id = movieDto.id,
            userId = userId, // 👈 Le pegamos el DNI del usuario a la entidad
            title = movieDto.title,
            posterPath = movieDto.posterPath ?: "",
            overview = movieDto.overview
        )
        // Usamos el DAO para guardarlo en Room
        database.movieDao().insertMovie(entity)
    }

    // 👇 Actualizamos TODAS las lecturas para que pasen el userId al DAO 👇
    fun getAllSavedMovies(userId: String) = database.movieDao().getAllSavedMovies(userId)

    suspend fun deleteAllMovies(userId: String) = database.movieDao().deleteAllMovies(userId)

    // Trae SOLO las que están pendientes de ver de ESE usuario (isWatched = 0)
    fun getFavoriteMovies(userId: String) = database.movieDao().getFavoriteMovies(userId)

    // Trae SOLO las que ya ha visto ESE usuario (isWatched = 1)
    fun getWatchedMovies(userId: String) = database.movieDao().getWatchedMovies(userId)

    // 👇 Los comandos para cambiar de lista también necesitan el userId 👇
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