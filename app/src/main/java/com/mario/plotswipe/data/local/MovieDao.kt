package com.mario.plotswipe.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<MovieEntity>
    @Query("SELECT * FROM movies")
    fun getAllSavedMovies(): kotlinx.coroutines.flow.Flow<List<MovieEntity>>
    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()

    // 2. 🍿 NUEVO: Solo las que están pendientes de ver (Para tu pantalla de Favoritos actual)
    @Query("SELECT * FROM movies WHERE isWatched = 0")
    fun getFavoriteMovies(): kotlinx.coroutines.flow.Flow<List<MovieEntity>>

    // 3. 👁️ NUEVO: Solo las que ya has visto (Para tu futura 3ª pantalla)
    @Query("SELECT * FROM movies WHERE isWatched = 1")
    fun getWatchedMovies(): kotlinx.coroutines.flow.Flow<List<MovieEntity>>

    // 4. 🪄 NUEVO: El comando mágico para cambiarla de lista
    @Query("UPDATE movies SET isWatched = 1 WHERE id = :movieId")
    suspend fun markAsWatched(movieId: Int)
}