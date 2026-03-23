package com.mario.plotswipe.data.repository

import com.mario.plotswipe.data.local.AppDatabase
import com.mario.plotswipe.data.local.MovieEntity
import com.mario.plotswipe.data.remote.MovieDto
import com.mario.plotswipe.data.remote.RetrofitClient

// Ahora el repositorio necesita saber de la base de datos (AppDatabase)
class MovieRepository(private val database: AppDatabase) {
    private val api = RetrofitClient.api

    suspend fun fetchPopularMovies(): List<MovieDto> {
        return try {
            val response = api.getPopularMovies(RetrofitClient.API_KEY)
            response.results
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
}