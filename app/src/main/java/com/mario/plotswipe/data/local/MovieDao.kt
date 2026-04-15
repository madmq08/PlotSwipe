package com.mario.plotswipe.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    // 👇 A todas las consultas les exigimos ahora que nos pasen el userId (DNI) 👇

    @Query("SELECT * FROM movies WHERE userId = :userId")
    suspend fun getAllMovies(userId: String): List<MovieEntity>

    @Query("SELECT * FROM movies WHERE userId = :userId")
    fun getAllSavedMovies(userId: String): Flow<List<MovieEntity>>

    @Query("DELETE FROM movies WHERE userId = :userId")
    suspend fun deleteAllMovies(userId: String)

    // Solo las que están pendientes de ver de ESE usuario
    @Query("SELECT * FROM movies WHERE isWatched = 0 AND userId = :userId")
    fun getFavoriteMovies(userId: String): Flow<List<MovieEntity>>

    // Solo las que ya ha visto ESE usuario
    @Query("SELECT * FROM movies WHERE isWatched = 1 AND userId = :userId")
    fun getWatchedMovies(userId: String): Flow<List<MovieEntity>>

    // Actualizamos el estado SOLO para la peli de ESE usuario
    @Query("UPDATE movies SET isWatched = 1 WHERE id = :movieId AND userId = :userId")
    suspend fun markAsWatched(movieId: Int, userId: String)

    @Query("DELETE FROM movies WHERE isWatched = 1 AND userId = :userId")
    suspend fun deleteWatchedMovies(userId: String)

    @Query("UPDATE movies SET isWatched = 2 WHERE id = :movieId AND userId = :userId")
    suspend fun markAsDiscarded(movieId: Int, userId: String)
}